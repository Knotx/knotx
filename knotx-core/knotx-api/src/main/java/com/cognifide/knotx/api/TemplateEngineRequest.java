/*
 * Knot.x - Reactive microservice assembler - API
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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Map;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import rx.Observable;

public class TemplateEngineRequest extends JsonObjectRequest {
    private final MultiMap formAttributes;
    private String template;
    private HttpMethod serverRequestMethod;
    private MultiMap headers;
    private MultiMap params;
    private String uri;

    private Cache<String, Observable<Map<String, Object>>> cache = CacheBuilder.newBuilder().build();

    public TemplateEngineRequest(String template, HttpMethod serverRequestMethod, MultiMap headers,
                                 MultiMap params, MultiMap formAttributes, String uri) {
        this.template = template;
        this.serverRequestMethod = serverRequestMethod;
        this.headers = headers;
        this.params = params;
        this.formAttributes = formAttributes;
        this.uri = uri;
    }

    public TemplateEngineRequest(JsonObject object) {
        this.template = object.getString("template");
        this.serverRequestMethod = HttpMethod.valueOf(object.getString("serverRequestMethod"));
        this.headers = fromJsonArray(object.getJsonArray("headers"));
        this.params = fromJsonArray(object.getJsonArray("params"));
        this.formAttributes = fromJsonArray(object.getJsonArray("formAttributes"));
        this.uri = object.getString("uri");
    }

    public String getTemplate() {
        return template;
    }

    public MultiMap getHeaders() {
        return headers;
    }

    public MultiMap getParams() {
        return params;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getServerRequestMethod() {
        return serverRequestMethod;
    }

    public MultiMap getFormAttributes() {
        return formAttributes;
    }

    public Cache<String, Observable<Map<String, Object>>> getCache() {
        return cache;
    }

    @Override
    public String toString() {
        return "TemplateEngineRequest{" + "template='" + template + '\'' //
                + ", headers=" + multimapToSting(headers) //
                + ", params=" + multimapToSting(params) //
                + ", formAttributes=" + multimapToSting(formAttributes) //
                + ", uri=" + uri //
                + ", serverRequestMethod=" + serverRequestMethod + '}';
    }

    @Override
    public JsonObject toJsonObject() {
        return new JsonObject().put("template", template)
                .put("serverRequestMethod", serverRequestMethod.toString()) //
                .put("headers", toJsonArray(headers)) //
                .put("params", toJsonArray(params)) //
                .put("formAttributes", toJsonArray(formAttributes)) //
                .put("uri", uri);
    }

    private String multimapToSting(MultiMap map) {
        StringBuilder result = new StringBuilder();
        map.names().forEach(header -> result.append(header).append("=").append(headers.get(header))
                .append("\n"));
        return result.toString();
    }
}
