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

import com.cognifide.knotx.api.RepositoryRequest;
import com.cognifide.knotx.api.RepositoryResponse;
import com.cognifide.knotx.repository.Repository;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
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

    private HttpClient httpClient;

    private HttpRepository() {
        // hidden constructor
    }

    static HttpRepository of(String path, String domain, Integer port, JsonObject clientOptions, Vertx vertx) {
        HttpRepository remoteRepository = new HttpRepository();
        remoteRepository.path = path;
        remoteRepository.domain = domain;
        remoteRepository.port = port;
        remoteRepository.vertx = vertx;
        remoteRepository.httpClient = vertx.createHttpClient(new HttpClientOptions(clientOptions));
        return remoteRepository;
    }


    @Override
    public Observable<RepositoryResponse> get(RepositoryRequest repositoryRequest) {
        Observable<HttpClientResponse> clientResponse =
                RxHelper.get(httpClient, port, domain, repositoryRequest.getPath(), repositoryRequest.getHeaders());

        return clientResponse
                .doOnNext(this::traceResponse)
                .flatMap(this::processResponse)
                .onErrorReturn(error -> {
                            LOGGER.error("Error occurred while trying to fetch template from remote repository for path `{}`", repositoryRequest.getPath(), error);
                            return RepositoryResponse
                                    .error(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), error.getMessage(), MultiMap.caseInsensitiveMultiMap());
                        }
                );
    }

    private Observable<RepositoryResponse> processResponse(final HttpClientResponse response) {
        return Observable.just(Buffer.buffer())
                .mergeWith(response.toObservable())
                .reduce(Buffer::appendBuffer)
                .flatMap(buffer -> toRepositoryResponse(buffer, response));
    }

    private Observable<RepositoryResponse> toRepositoryResponse(Buffer buffer, final HttpClientResponse httpClientResponse) {
        Observable<RepositoryResponse> response;
        if (httpClientResponse.statusCode() == HttpResponseStatus.OK.code()) {
            response = RepositoryResponse.success(buffer.toString(), httpClientResponse.headers()).toObservable();
        } else {
            LOGGER.info("Remote repository returned with status code {} for path `{}`",
                    httpClientResponse.statusCode(), path);
            response = RepositoryResponse.error(httpClientResponse.statusCode(), buffer.toString(),
                    httpClientResponse.headers()).toObservable();
        }
        return response;
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
