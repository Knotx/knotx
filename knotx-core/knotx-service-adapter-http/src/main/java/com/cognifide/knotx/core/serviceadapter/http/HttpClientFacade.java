/*
 * Knot.x - Sample App with Mock service
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;
import rx.functions.Action1;

class HttpClientFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientFacade.class);

  private final List<HttpServiceAdapterConfiguration.ServiceMetadata> services;

  private final HttpClient httpClient;

  HttpClientFacade(Vertx vertx, HttpServiceAdapterConfiguration configuration) {
    final JsonObject clientOptions = configuration.getClientOptions();
    this.services = configuration.getServices();
    this.httpClient = clientOptions.isEmpty() ?
        vertx.createHttpClient() : vertx.createHttpClient(new HttpClientOptions(clientOptions));
  }

  private static Observable<HttpClientResponse> request(HttpClient client, HttpMethod method, int port, String domain, String uri, Action1<HttpClientRequest> requestBuilder) {
    return Observable.create(subscriber -> {
      HttpClientRequest req = client.request(method, port, domain, uri);
      Observable<HttpClientResponse> resp = req.toObservable();
      resp.subscribe(subscriber);
      requestBuilder.call(req);
      req.end();
    });
  }

  Observable<HttpResponseWrapper> process(JsonObject httpRequest) {
    HttpRequestWrapper request = new HttpRequestWrapper(httpRequest);

    Optional<HttpServiceAdapterConfiguration.ServiceMetadata> serviceEntry = services.stream()
        .filter(metadata -> request.path().matches(metadata.getPath()))
        .findFirst();

    if (serviceEntry.isPresent()) {
      Observable<HttpClientResponse> serviceResponse = request(
          httpClient, request.method(), serviceEntry.get().getPort(), serviceEntry.get().getDomain(), request.path(),
          req -> buildRequestBody(req, request.headers(), request.formAttributes(),
              request.method()));

      return serviceResponse.flatMap(item -> transformResponse(item, request.method(), request.path()));
    } else {
      LOGGER.error("Could not handle request with path [{}]", request.path());
      return Observable.just(new HttpResponseWrapper().setStatusCode(HttpResponseStatus.BAD_REQUEST));
    }
  }

  private Observable<HttpResponseWrapper> transformResponse(HttpClientResponse response, HttpMethod method, String path) {
    return Observable.just(Buffer.buffer()).mergeWith(response.toObservable()).reduce(Buffer::appendBuffer)
        .doOnNext(buffer -> traceServiceCall(method, path, buffer))
        .map(buffer -> new HttpResponseWrapper().setBody(buffer).setStatusCode(HttpResponseStatus.valueOf(response.statusCode())));
  }

  private void buildRequestBody(HttpClientRequest request, MultiMap headers,
                                MultiMap formAttributes, HttpMethod httpMethod) {
    request.headers().addAll(headers);
    if (!formAttributes.isEmpty() && HttpMethod.POST.equals(httpMethod)) {
      Buffer buffer = createFormPostBody(formAttributes);
      request.headers().set("content-length", String.valueOf(buffer.length()));
      request.headers().set("content-type", "application/x-www-form-urlencoded");
      request.write(buffer);
    }
  }

  private Buffer createFormPostBody(MultiMap formAttributes) {
    Buffer buffer = Buffer.buffer();

    String formPostContent = Joiner.on("&").withKeyValueSeparator("=")
        .join((Iterable<Map.Entry<String, String>>) formAttributes.getDelegate());
    buffer.appendString(formPostContent, Charsets.UTF_8.toString());
    return buffer;
  }

  private void traceServiceCall(HttpMethod method, String path, Buffer results) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Service call returned <{}> <{}>", method, path, results.toJsonObject().encodePrettily());
    }
  }
}
