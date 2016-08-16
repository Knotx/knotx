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

import com.cognifide.knotx.result.AsyncResultFactory;

import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;

import io.vertx.core.AsyncResultHandler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;

class RemoteRepository implements Repository<String, URI> {

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
    public void get(URI uri, AsyncResultHandler<Template<String, URI>> handler) throws IOException {
        HttpClientRequest httpClientRequest = httpClient.get(port, domain, uri.toString(),
                httpClientResponse -> {
                    new HttpTemplate(httpClientResponse).handle(httpClientResponse,
                            template -> handler.handle(AsyncResultFactory.createSuccess(template)),
                            template -> AsyncResultFactory.createSuccess(template)
                    );

                });
        httpClientRequest.end();
    }

    @Override
    public boolean support(URI uri) {
        String path = uri.getPath();
        return path.matches(this.path);
    }

}
