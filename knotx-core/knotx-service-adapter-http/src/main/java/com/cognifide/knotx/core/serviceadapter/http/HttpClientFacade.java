/*
 * Knot.x - Reactive microservice assembler - http service adapter
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.knotx.core.serviceadapter.http;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import com.cognifide.knotx.dataobjects.HttpRequestWrapper;
import com.cognifide.knotx.dataobjects.HttpResponseWrapper;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

class HttpClientFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientFacade.class);
  private static final String REQUEST_KEY = "request";
  private static final String PARAMS_KEY = "params";
  private static final String PATH_PROPERTY_KEY = "path";
  private static final HttpResponseWrapper DEFAULT_ERROR_RESPONSE = new HttpResponseWrapper().setStatusCode(HttpResponseStatus.BAD_REQUEST);

  private final List<HttpServiceAdapterConfiguration.ServiceMetadata> services;

  private final HttpClient httpClient;

  HttpClientFacade(HttpClient httpClient, List<HttpServiceAdapterConfiguration.ServiceMetadata> services) {
    this.httpClient = httpClient;
    this.services = services;
  }

  Observable<HttpResponseWrapper> process(JsonObject message) {
    return Observable.just(message)
        .filter(this::validateContract)
        .map(this::prepareRequest)
        .filter(this::ensureRequestIsSupported)
        .flatMap(this::callService)
        .flatMap(this::wrapResponse)
        .defaultIfEmpty(DEFAULT_ERROR_RESPONSE);
  }

  private Boolean validateContract(JsonObject message) {
    return message.getJsonObject(PARAMS_KEY).containsKey(PATH_PROPERTY_KEY);
  }

  private Pair<HttpRequestWrapper, HttpServiceAdapterConfiguration.ServiceMetadata> prepareRequest(JsonObject message) {
    final Pair<HttpRequestWrapper, HttpServiceAdapterConfiguration.ServiceMetadata> serviceRequest;

    final HttpRequestWrapper originalRequest = new HttpRequestWrapper(message.getJsonObject(REQUEST_KEY));
    final JsonObject params = message.getJsonObject(PARAMS_KEY);

    //TODO prepare service path with request attributes
    final String servicePath = params.getString(PATH_PROPERTY_KEY);

    final Optional<HttpServiceAdapterConfiguration.ServiceMetadata> serviceMetadata = findServiceMetadata(servicePath);
    if (serviceMetadata.isPresent()) {
      final HttpRequestWrapper serviceRequestWrapper = new HttpRequestWrapper(originalRequest.toJson());
      serviceRequestWrapper.setPath(servicePath);
      serviceRequest = Pair.of(serviceRequestWrapper, serviceMetadata.get());
    } else {
      serviceRequest = Pair.of(null, null);
    }

    return serviceRequest;
  }

  private Optional<HttpServiceAdapterConfiguration.ServiceMetadata> findServiceMetadata(String servicePath) {
    return services.stream().filter(metadata -> servicePath.matches(metadata.getPath())).findAny();
  }

  private Boolean ensureRequestIsSupported(Pair<HttpRequestWrapper, HttpServiceAdapterConfiguration.ServiceMetadata> serviceRequest) {
    return serviceRequest.getRight() != null;
  }

  private Observable<HttpClientResponse> callService(Pair<HttpRequestWrapper, HttpServiceAdapterConfiguration.ServiceMetadata> serviceRequest) {
    final HttpRequestWrapper requestWrapper = serviceRequest.getLeft();
    final HttpServiceAdapterConfiguration.ServiceMetadata serviceMetadata = serviceRequest.getRight();

    return Observable.create(subscriber -> {
      HttpClientRequest request = httpClient.get(serviceMetadata.getPort(), serviceMetadata.getDomain(), requestWrapper.path());
      Observable<HttpClientResponse> resp = request.toObservable();
      resp.subscribe(subscriber);
      request.headers().addAll(getFilteredHeaders(requestWrapper.headers(), serviceMetadata.getAllowedRequestHeaderPatterns()));

      if (HttpMethod.POST == requestWrapper.method()) {
        Buffer buffer = createFormPostBody(requestWrapper.formAttributes());
        request.headers().set(HttpHeaders.CONTENT_LENGTH.toString(), String.valueOf(buffer.length()));
        request.write(buffer);
      } else {
        request.headers().remove(HttpHeaders.CONTENT_LENGTH.toString());
      }

      request.end();
    });
  }

  private MultiMap getFilteredHeaders(MultiMap headers, List<Pattern> allowedHeaders) {
    return headers.names().stream()
        .filter(AllowedHeadersFilter.create(allowedHeaders))
        .collect(MultiMapCollector.toMultimap(o -> o, headers::get));
  }

  private Buffer createFormPostBody(MultiMap formAttributes) {
    Buffer buffer = Buffer.buffer();

    String formPostContent = Joiner.on("&").withKeyValueSeparator("=")
        .join((Iterable<Map.Entry<String, String>>) formAttributes.getDelegate());
    buffer.appendString(formPostContent, Charsets.UTF_8.toString());
    return buffer;
  }

  private Observable<HttpResponseWrapper> wrapResponse(HttpClientResponse response) {
    return Observable.just(Buffer.buffer())
        .mergeWith(response.toObservable())
        .reduce(Buffer::appendBuffer)
        .doOnNext(this::traceServiceCall)
        .map(buffer -> new HttpResponseWrapper()
            .setBody(buffer)
            .setStatusCode(HttpResponseStatus.valueOf(response.statusCode()))
        );
  }

  private void traceServiceCall(Buffer results) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Service call returned <{}>", results.toJsonObject().encodePrettily());
    }
  }
}
