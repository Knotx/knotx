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
package com.cognifide.knotx.repository.template;

import com.cognifide.knotx.repository.Action;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerResponse;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class HttpTemplate implements Template<String, URI> {


    private int httpStatus;

    private String content;

    private MultiMap headers;

    private URI uri;

    public HttpTemplate(URI uri, int httpStatus, MultiMap headers, String responseContent) {
        this.uri = uri;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.content = responseContent;
    }

    @Override
    public URI getId() {
        return uri;
    }

    @Override
    public String get() {
        return content;
    }

    @Override
    public void handle(HttpServerResponse httpServerResponse, Action process, Action done) {
        if (httpStatus == HttpStatus.OK.value()) {
            process.handle(this);
        } else {
            headers.forEach(headerEntry -> httpServerResponse.putHeader(headerEntry.getKey(), headerEntry.getValue()));
            httpServerResponse.setStatusCode(httpStatus).end();
            done.handle(this);
        }

    }
}
