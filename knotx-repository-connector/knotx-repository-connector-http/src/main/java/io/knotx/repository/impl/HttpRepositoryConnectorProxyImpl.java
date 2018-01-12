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
package io.knotx.repository.impl;

import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.http.AllowedHeadersFilter;
import io.knotx.http.MultiMapCollector;
import io.knotx.http.StringToPatternFunction;
import io.knotx.proxy.RepositoryConnectorProxy;
import io.knotx.util.DataObjectsUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class HttpRepositoryConnectorProxyImpl implements RepositoryConnectorProxy {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(HttpRepositoryConnectorProxyImpl.class);

  private static final String ERROR_MESSAGE = "Unable to get template from the repository";

  private final JsonObject clientOptions;
  private final JsonObject clientDestination;
  private final List<Pattern> allowedRequestHeaders;
  private final HttpClient httpClient;
  private final JsonObject customRequestHeader;


  public HttpRepositoryConnectorProxyImpl(Vertx vertx, JsonObject configuration) {
    clientOptions = configuration.getJsonObject("clientOptions", new JsonObject());
    clientDestination = configuration.getJsonObject("clientDestination");
    customRequestHeader = configuration.getJsonObject("customRequestHeader", new JsonObject());
    allowedRequestHeaders = configuration.getJsonArray("allowedRequestHeaders", new JsonArray())
        .stream()
        .map(object -> (String) object)
        .map(new StringToPatternFunction())
        .collect(Collectors.toList());
    httpClient = createHttpClient(vertx);
  }

  @Override
  public void process(ClientRequest request, Handler<AsyncResult<ClientResponse>> result) {
    MultiMap requestHeaders = buildHeaders(clientDestination.getString("hostHeader"),
        request.getHeaders());

    RequestOptions httpRequestData = buildRequestData(request);

    if (LOGGER.isTraceEnabled()) {
      LOGGER.debug("GET HTTP Repository: {}://{}:{}/{} with headers [{}]",
          httpRequestData.isSsl() ? "https" : "http",
          httpRequestData.getHost(),
          httpRequestData.getPort(),
          httpRequestData.getURI(),
          DataObjectsUtil.toString(requestHeaders)
      );
    }

    get(httpClient, httpRequestData, requestHeaders)
        .doOnNext(this::traceHttpResponse)
        .flatMap(this::processResponse)
        .subscribe(
            response -> result.handle(Future.succeededFuture(response)),
            error -> {
              LOGGER.error(ERROR_MESSAGE, error);
              result.handle(Future.succeededFuture(toInternalError()));
            }
        );
  }

  private RequestOptions buildRequestData(ClientRequest request) {
    return new RequestOptions()
        .setSsl(clientDestination.getString("scheme", "http").equals("https") ? true : false)
        .setURI(buildRepoUri(request))
        .setPort(clientDestination.getInteger("port"))
        .setHost(clientDestination.getString("domain"));
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

  private HttpClient createHttpClient(Vertx vertx) {
    return clientOptions.isEmpty()
        ? io.vertx.reactivex.core.Vertx.newInstance(vertx).createHttpClient()
        : io.vertx.reactivex.core.Vertx.newInstance(vertx)
            .createHttpClient(new HttpClientOptions(clientOptions));
  }

  private String buildRepoUri(ClientRequest repoRequest) {
    StringBuilder uri = new StringBuilder(repoRequest.getPath());
    MultiMap params = repoRequest.getParams();
    if (params != null && params.names() != null && !params.names().isEmpty()) {
      uri.append("?")
          .append(params.names().stream()
              .map(name -> new StringBuilder(name).append("=")
                  .append(encodeParamValue(params.get(name))))
              .collect(Collectors.joining("&"))
          );
    }

    return uri.toString();
  }

  private String encodeParamValue(String value) {
    try {
      return URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("%2F", "/");
    } catch (UnsupportedEncodingException ex) {
      LOGGER.fatal("Unexpected Exception - Unsupported encoding UTF-8", ex);
      throw new UnsupportedCharsetException("UTF-8");
    }
  }

  private Observable<ClientResponse> processResponse(final HttpClientResponse response) {
    return Observable.just(Buffer.buffer())
        .mergeWith(response.toObservable())
        .reduce(Buffer::appendBuffer)
        .toObservable()
        .map(buffer -> toResponse(buffer, response));
  }

  private ClientResponse toResponse(Buffer buffer, final HttpClientResponse httpResponse) {
    if (httpResponse.statusCode() >= 300 && httpResponse.statusCode() < 400) { //redirect responses
      LOGGER.info("Repository 3xx response: {}, Headers[{}]", httpResponse.statusCode(),
          DataObjectsUtil.toString(httpResponse.headers()));
    } else if (httpResponse.statusCode() != 200) {
      LOGGER.error("Repository error response: {}, Headers[{}]", httpResponse.statusCode(),
          DataObjectsUtil.toString(httpResponse.headers()));
    }

    return new ClientResponse()
        .setStatusCode(httpResponse.statusCode())
        .setHeaders(httpResponse.headers())
        .setBody(buffer.getDelegate());

  }

  private ClientResponse toInternalError() {
    return new ClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
  }

  private MultiMap buildHeaders(String hostHeader, MultiMap headers) {
    MultiMap result = filteredHeaders(headers);

    if (customRequestHeader.containsKey("name") && customRequestHeader.containsKey("value")) {
      result.set(
          customRequestHeader.getString("name"),
          customRequestHeader.getString("value")
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
        .filter(AllowedHeadersFilter.create(allowedRequestHeaders))
        .collect(MultiMapCollector.toMultiMap(o -> o, headers::getAll));
  }

  private void traceHttpResponse(HttpClientResponse response) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got response from remote repository status [{}]", response.statusCode());
    }
  }
}
