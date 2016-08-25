/*
 * Knot.x - Reactive microservice assembler - API
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
package com.cognifide.knotx.api;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;

public class TemplateEngineRequest extends JsonObjectRequest {
    private String template;

    private MultiMap headers;

    private HttpMethod httpMethod;

    public TemplateEngineRequest(String template, MultiMap headers) {
        this.template = template;
        this.headers = headers;
    }

    public TemplateEngineRequest(JsonObject object) {
        this.template = object.getString("template");
        this.headers = fromJsonArray(object.getJsonArray("headers"));

    }

    public String getTemplate() {
        return template;
    }

    public MultiMap getHeaders() {
        return headers;
    }

    public void setHeaders(MultiMap headers) {
        this.headers = headers;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
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

    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put("template", template)
                .put("headers", toJsonArray(headers));
    }
}
