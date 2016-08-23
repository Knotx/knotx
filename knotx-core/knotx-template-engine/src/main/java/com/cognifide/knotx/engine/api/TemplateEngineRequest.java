/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
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
package com.cognifide.knotx.engine.api;

import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;

public class TemplateEngineRequest {
    private Buffer template;

    private MultiMap headers;

    public TemplateEngineRequest(Buffer template) {
        this.template = template;
    }

    public Buffer getTemplate() {
        return template;
    }

    public MultiMap getHeaders() {
        return headers;
    }

    public void setHeaders(MultiMap headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "TemplateEngineRequest{" +
                "template=" + template +
                ", headers=[" + headersToString() +
                "]}";
    }

    private String headersToString() {
        StringBuilder result = new StringBuilder();
        headers.names().stream()
                .forEach(header -> result.append(header).append("=").append(headers.get(header)).append("\n"));

        return result.toString();
    }
}
