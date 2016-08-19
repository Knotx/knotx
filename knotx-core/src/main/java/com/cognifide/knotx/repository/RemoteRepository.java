/*
 * Knot.x - Reactive microservice assembler
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
package com.cognifide.knotx.repository;

import com.cognifide.knotx.api.RepositoryResponse;

import org.springframework.http.HttpStatus;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

class RemoteRepository implements Repository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteRepository.class);

    private String path;

    private String domain;

    private Integer port;

    private HttpClient httpClient;

    private RemoteRepository() {
        // hidden constructor
    }

    static RemoteRepository of(String path, String domain, Integer port, HttpClient httpClient) {
        RemoteRepository remoteRepository = new RemoteRepository();
        remoteRepository.path = path;
        remoteRepository.domain = domain;
        remoteRepository.port = port;
        remoteRepository.httpClient = httpClient;
        return remoteRepository;
    }

    @Override
    public Observable<RepositoryResponse> get(String path) {
        Observable<HttpClientResponse> clientResponse = RxHelper.get(httpClient, port, domain, path);

        return clientResponse
                .doOnNext(this::traceResponse)
                .filter(this::onlySuccess)
                .flatMap(this::reduceBuffers)
                .flatMap(this::toRepositoryResponse)
                .defaultIfEmpty(RepositoryResponse.error("No Template found for <%s>", path))
                .onErrorReturn(error -> {
                            LOGGER.error("Unable to fetch template from remote repository for path `{0}`", path, error);
                            return RepositoryResponse.error("No Template found for path %s", path);
                        }
                );
    }

    private Observable<Buffer> reduceBuffers(HttpClientResponse response) {
        return Observable.just(Buffer.buffer())
                .mergeWith(response.toObservable())
                .reduce(Buffer::appendBuffer);
    }

    private Observable<RepositoryResponse> toRepositoryResponse(Buffer buffer) {
        Observable<RepositoryResponse> response;
        if (buffer.length() > 0) {
            response = RepositoryResponse.success(buffer.copy()).toObservable();
        } else {
            LOGGER.error("Remote repository returned empty template for path `{0}`", path);
            response = RepositoryResponse.error("No Template found for path %s", path).toObservable();
        }
        return response;
    }

    private void traceResponse(HttpClientResponse response) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Got response from remote repository {0}", response.statusCode());
        }
    }

    private boolean onlySuccess(HttpClientResponse response) {
        return response.statusCode() == HttpStatus.OK.value();
    }

    @Override
    public boolean support(String path) {
        return path.matches(this.path);
    }
}
