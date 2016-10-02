/*
 * Knot.x - Reactive microservice assembler - Repository Verticle
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
package com.cognifide.knotx.repository.impl;

import com.cognifide.knotx.api.HttpRequestWrapper;
import com.cognifide.knotx.api.HttpResponseWrapper;
import com.cognifide.knotx.repository.Repository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

class HttpRepository implements Repository {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpRepository.class);

  private String path;

  private String domain;

  private Integer port;

  private Vertx vertx;

  private JsonObject clientOptions;

  private HttpRepository() {
    // hidden constructor
  }

  static HttpRepository of(String path, String domain, Integer port, JsonObject clientOptions, Vertx vertx) {
    HttpRepository remoteRepository = new HttpRepository();
    remoteRepository.path = path;
    remoteRepository.domain = domain;
    remoteRepository.port = port;
    remoteRepository.vertx = vertx;
    remoteRepository.clientOptions = clientOptions;
    return remoteRepository;
  }

  @Override
  public Observable<HttpResponseWrapper> get(HttpRequestWrapper repositoryRequest) {
    final HttpClient httpClient = createHttpClient();
    Observable<HttpClientResponse> clientResponse =
        RxHelper.get(httpClient, port, domain, repositoryRequest.path(), repositoryRequest.headers());

    return clientResponse
        .doOnNext(this::traceResponse)
        .flatMap(this::processResponse)
        .onErrorReturn(error -> {
              LOGGER.error("Error occurred while trying to fetch template from remote repository for path `{}`", repositoryRequest.path(), error);
              return new HttpResponseWrapper().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }
        ).doAfterTerminate(httpClient::close);
  }

  private HttpClient createHttpClient() {
    return clientOptions.isEmpty() ? vertx.createHttpClient() : vertx.createHttpClient(new HttpClientOptions(clientOptions));
  }

  private Observable<HttpResponseWrapper> processResponse(final HttpClientResponse response) {
    return Observable.just(Buffer.buffer())
        .mergeWith(response.toObservable())
        .reduce(Buffer::appendBuffer)
        .map(buffer -> toRepositoryResponse(buffer, response));
  }

  private HttpResponseWrapper toRepositoryResponse(Buffer buffer, final HttpClientResponse httpClientResponse) {
    return new HttpResponseWrapper()
        .setStatusCode(HttpResponseStatus.valueOf(httpClientResponse.statusCode()))
        .setHeaders(httpClientResponse.headers())
        .setBody(buffer);
  }

  private void traceResponse(HttpClientResponse response) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got response from remote repository {}", response.statusCode());
    }
  }

  @Override
  public boolean support(String path) {
    return path.matches(this.path);
  }
}
