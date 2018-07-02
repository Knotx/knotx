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

import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.http.AllowedHeadersFilter;
import io.knotx.http.MultiMapCollector;
import io.knotx.proxy.RepositoryConnectorProxy;
import io.knotx.util.DataObjectsUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.core.http.HttpClientRequest;
import io.vertx.reactivex.core.http.HttpClientResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class HttpRepositoryConnectorProxyImpl implements RepositoryConnectorProxy {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(HttpRepositoryConnectorProxyImpl.class);

  private static final String ERROR_MESSAGE = "Unable to get template from the repository";

  private final HttpRepositoryOptions configuration;

  private final HttpClient httpClient;


  public HttpRepositoryConnectorProxyImpl(Vertx vertx, HttpRepositoryOptions configuration) {
    this.configuration = configuration;
    this.httpClient = HttpClient
        .newInstance(vertx.createHttpClient(configuration.getClientOptions()));
  }

  @Override
  public void process(ClientRequest request, Handler<AsyncResult<ClientResponse>> result) {
    MultiMap requestHeaders = buildHeaders(configuration.getClientDestination().getHostHeader(),
        request.getHeaders());

    RequestOptions httpRequestData = buildRequestData(request);

    if (LOGGER.isDebugEnabled()) {    	
      LOGGER.debug("GET HTTP Repository: {}  with headers [{}]",
    	  getUrl(httpRequestData),
          DataObjectsUtil.toString(requestHeaders)
      );
    }

    get(httpClient, httpRequestData, requestHeaders)
        .doOnNext(this::traceHttpResponse)
        .flatMap(response -> processResponse(response, httpRequestData))
        .subscribe(
            response -> result.handle(Future.succeededFuture(response)),
            error -> {
              LOGGER.error(ERROR_MESSAGE, error);
              result.handle(Future.succeededFuture(toInternalError()));
            }
        );
  }
  
  private String getUrl(RequestOptions httpRequestData) {
    return String.format("%s://%s:%d/%s ",
        httpRequestData.isSsl() ? "https" : "http",
        httpRequestData.getHost(),
        httpRequestData.getPort(),
        httpRequestData.getURI()
    );
  }

  private RequestOptions buildRequestData(ClientRequest request) {
    return new RequestOptions()
        .setSsl(configuration.getClientDestination().getScheme().equals("https") ? true : false)
        .setURI(buildRepoUri(request))
        .setPort(configuration.getClientDestination().getPort())
        .setHost(configuration.getClientDestination().getDomain());
  }

  private Observable<HttpClientResponse> get(HttpClient client, RequestOptions requestOptions,
      MultiMap headers) {
    return Observable.unsafeCreate(subscriber -> {
      HttpClientRequest req = client.get(requestOptions);
      req.headers().addAll(headers);
      if (headers.get(HttpHeaderNames.HOST.toString()) != null) {
        req.setHost(headers.get(HttpHeaderNames.HOST.toString()));
      }
      Observable<HttpClientResponse> resp = req.toObservable();
      resp.subscribe(subscriber);
      req.end();
    });
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

  private Observable<ClientResponse> processResponse(final HttpClientResponse response, final RequestOptions httpRequestData) {
    return Observable.just(Buffer.buffer())
        .mergeWith(response.toObservable())
        .reduce(Buffer::appendBuffer)
        .toObservable()
        .map(buffer -> toResponse(buffer, response, httpRequestData));
  }

  private ClientResponse toResponse(Buffer buffer, final HttpClientResponse httpResponse, final RequestOptions httpRequestData) {
    final int statusCode = httpResponse.statusCode();
    
    if (HttpStatusClass.SUCCESS.contains(statusCode)) {
      LOGGER.debug("Repository 2xx response: {}, Headers[{}]", statusCode,
          DataObjectsUtil.toString(httpResponse.headers()));
    } else if (HttpStatusClass.REDIRECTION.contains(statusCode)) { // redirect
      LOGGER.info("Repository 3xx response: {}, Headers[{}]", statusCode,
          DataObjectsUtil.toString(httpResponse.headers()));
    } else if (HttpStatusClass.CLIENT_ERROR.contains(statusCode)) { // errors
      LOGGER.warn("Repository client error 4xx. Request URL: {}, response: {}, Headers[{}]",
          getUrl(httpRequestData), statusCode, DataObjectsUtil.toString(httpResponse.headers()));
    } else if (HttpStatusClass.SERVER_ERROR.contains(statusCode)) {
      LOGGER.error("Repository server error 5xx. Request URL: {},  response: {}, Headers[{}]",
          getUrl(httpRequestData), statusCode, DataObjectsUtil.toString(httpResponse.headers()));
    } else {
      LOGGER.warn("Other response: {}, Headers[{}]", statusCode,
          DataObjectsUtil.toString(httpResponse.headers()));
    }

    return new ClientResponse()
        .setStatusCode(statusCode)
        .setHeaders(httpResponse.headers())
        .setBody(buffer.getDelegate());
  }

  private ClientResponse toInternalError() {
    return new ClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
  }

  private MultiMap buildHeaders(String hostHeader, MultiMap headers) {
    MultiMap result = filteredHeaders(headers);

    if (configuration.getCustomHttpHeader() != null) {
      result.set(
          configuration.getCustomHttpHeader().getName(),
          configuration.getCustomHttpHeader().getValue()
      );
    }

    //Overide host header if provided in client destination
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

  private void traceHttpResponse(HttpClientResponse response) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got response from remote repository status [{}]", response.statusCode());
    }
  }
}
