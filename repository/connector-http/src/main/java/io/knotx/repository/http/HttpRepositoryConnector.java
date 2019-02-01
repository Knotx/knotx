/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.repository.http;

import io.knotx.server.api.context.ClientRequest;
import io.knotx.server.api.context.ClientResponse;
import io.knotx.server.api.context.FragmentsContext;
import io.knotx.server.util.AllowedHeadersFilter;
import io.knotx.server.util.DataObjectsUtil;
import io.knotx.server.util.MultiMapCollector;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;
import io.reactivex.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpClientResponse;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

class HttpRepositoryConnector {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpRepositoryConnector.class);

  private static final String ERROR_MESSAGE = "Unable to get template from the repository";

  private final HttpRepositoryOptions configuration;

  private final WebClient webClient;


  HttpRepositoryConnector(Vertx vertx, HttpRepositoryOptions configuration) {
    this.configuration = configuration;
    this.webClient = WebClient.create(vertx, configuration.getClientOptions());
  }

  Single<FragmentsContext> process(FragmentsContext fragmentsContext) {
    ClientRequest request = fragmentsContext.getClientRequest();
    MultiMap requestHeaders = buildHeaders(configuration.getClientDestination().getHostHeader(),
        request.getHeaders());

    RequestOptions httpRequestData = buildRequestData(request);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("GET HTTP Repository: {}  with headers [{}]",
          getUrl(httpRequestData),
          DataObjectsUtil.toString(requestHeaders)
      );
    }

    return getRequest(webClient, httpRequestData, requestHeaders)
        .doOnSuccess(resp -> logResponse(resp, httpRequestData, requestHeaders))
        .flatMap(this::toClientResponse)
        .map(fragmentsContext::setClientResponse)
        .onErrorResumeNext(error -> processFatalError(error, fragmentsContext));
  }

  private Single<FragmentsContext> processFatalError(Throwable error,
      FragmentsContext fragmentsContext) {
    LOGGER.error(ERROR_MESSAGE, error);
    return Single.just(fragmentsContext.setClientResponse(
        new ClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())));
  }

  private void logResponse(HttpResponse<Buffer> resp, RequestOptions httpRequestData,
      MultiMap requestHeaders) {
    int statusCode = resp.statusCode();

    if (HttpStatusClass.SUCCESS.contains(statusCode)) {
      LOGGER.debug("Repository 2xx response: {}, Headers[{}]", statusCode,
          DataObjectsUtil.toString(resp.headers()));
    } else if (HttpStatusClass.REDIRECTION.contains(statusCode)) { // redirect
      LOGGER.info("Repository 3xx response: {}, Headers[{}]", statusCode,
          DataObjectsUtil.toString(resp.headers()));
    } else if (HttpStatusClass.CLIENT_ERROR.contains(statusCode)) { // errors
      LOGGER.warn("Repository client error 4xx. Request URL: {}, response: {}, Headers[{}]",
          getUrl(httpRequestData), statusCode, DataObjectsUtil.toString(resp.headers()));
    } else if (HttpStatusClass.SERVER_ERROR.contains(statusCode)) {
      LOGGER.error("Repository server error 5xx. Request URL: {},  response: {}, Headers[{}]",
          getUrl(httpRequestData), statusCode, DataObjectsUtil.toString(resp.headers()));
    } else {
      LOGGER.warn("Other response: {}, Headers[{}]", statusCode,
          DataObjectsUtil.toString(resp.headers()));
    }
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("HTTP Repository request: [{}, {}] -> got response [{}, {}]",
          httpRequestData, requestHeaders, resp.statusCode(), resp.headers());
    }
  }

  private Single<ClientResponse> toClientResponse(HttpResponse<Buffer> response) {
    return toBody(response)
        .map(buffer -> new ClientResponse()
            .setBody(buffer.getDelegate())
            .setHeaders(response.headers())
            .setStatusCode(response.statusCode())
        );
  }

  private Single<Buffer> toBody(HttpResponse<Buffer> response) {
    if (response.body() != null) {
      return Single.just(response.body());
    } else {
      LOGGER.warn("Repository returned empty body");
      return Single.just(Buffer.buffer());
    }
  }

  private Single<HttpResponse<Buffer>> getRequest(WebClient webClient,
      RequestOptions httpRequestData,
      MultiMap requestHeaders) {
    HttpRequest<Buffer> request = webClient.request(HttpMethod.GET, httpRequestData);
    request.headers().addAll(requestHeaders);
    if (requestHeaders.get(HttpHeaderNames.HOST.toString()) != null) {
      request.host(requestHeaders.get(HttpHeaderNames.HOST.toString()));
    }
    return request.rxSend();
  }

  private String getUrl(RequestOptions httpRequestData) {
    return String.format("%s://%s:%d%s ",
        httpRequestData.isSsl() ? "https" : "http",
        httpRequestData.getHost(),
        httpRequestData.getPort(),
        httpRequestData.getURI()
    );
  }

  private RequestOptions buildRequestData(ClientRequest request) {
    return new RequestOptions()
        .setSsl(configuration.getClientDestination().getScheme().equals("https"))
        .setURI(buildRepoUri(request))
        .setPort(configuration.getClientDestination().getPort())
        .setHost(configuration.getClientDestination().getDomain());
  }

  private String buildRepoUri(ClientRequest repoRequest) {
    StringBuilder uri = new StringBuilder(repoRequest.getPath());
    MultiMap params = repoRequest.getParams();
    if (params != null && params.names() != null && !params.names().isEmpty()) {
      uri.append("?")
          .append(params.names().stream()
              .map(name -> new StringBuilder(encode(name)).append("=")
                  .append(encode(params.get(name))))
              .collect(Collectors.joining("&"))
          );
    }

    return uri.toString();
  }

  private String encode(String value) {
    try {
      return URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("%2F", "/");
    } catch (UnsupportedEncodingException ex) {
      LOGGER.fatal("Unexpected Exception - Unsupported encoding UTF-8", ex);
      throw new UnsupportedCharsetException("UTF-8");
    }
  }

  private MultiMap buildHeaders(String hostHeader, MultiMap headers) {
    MultiMap result = filteredHeaders(headers);

    if (configuration.getCustomHttpHeader() != null) {
      result.set(
          configuration.getCustomHttpHeader().getName(),
          configuration.getCustomHttpHeader().getValue()
      );
    }

    //Override host header if provided in client destination
    if (StringUtils.isNotBlank(hostHeader)) {
      result.set(HttpHeaderNames.HOST.toString(), hostHeader);
    }

    return result;
  }

  private MultiMap filteredHeaders(MultiMap headers) {
    return headers.names().stream()
        .filter(AllowedHeadersFilter.create(configuration.getAllowedRequestHeadersPatterns()))
        .collect(MultiMapCollector.toMultiMap(o -> o, headers::getAll));
  }

}
