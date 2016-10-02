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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpServerRequest;

public class HttpRequestWrapper {
  private String path;

  private HttpMethod method;

  private MultiMap headers = MultiMap.caseInsensitiveMultiMap();

  private MultiMap params = MultiMap.caseInsensitiveMultiMap();

  private MultiMap formAttributes = MultiMap.caseInsensitiveMultiMap();

  public HttpRequestWrapper() {
    //Nothing to set by default
  }

  /**
   * Initialise the fields of this instance from the specified JSON
   *
   * @param json the JSON
   */
  public HttpRequestWrapper(JsonObject json) {
    this.path = json.getString("path");
    if (json.containsKey("method")) {
      this.method = HttpMethod.valueOf(json.getString("method"));
    }
    if (json.containsKey("params")) {
      this.params = fromJsonArray(json.getJsonArray("params"));
    }
    if (json.containsKey("formAttributes")) {
      this.formAttributes = fromJsonArray(json.getJsonArray("formAttributes"));
    }
    if (json.containsKey("headers")) {
      this.headers = fromJsonArray(json.getJsonArray("headers"));
    }
  }

  public HttpRequestWrapper(HttpServerRequest serverRequest) {
    this.path = serverRequest.path();
    this.method = serverRequest.method();
    this.headers = MultiMap.newInstance((io.vertx.core.MultiMap) serverRequest.headers().getDelegate());
    this.params = MultiMap.newInstance((io.vertx.core.MultiMap) serverRequest.params().getDelegate());
    this.formAttributes = MultiMap.newInstance((io.vertx.core.MultiMap) serverRequest.formAttributes().getDelegate());
  }

  public String path() {
    return path;
  }

  public HttpMethod method() {
    return method;
  }

  public MultiMap headers() {
    return headers;
  }

  public MultiMap params() {
    return params;
  }

  public MultiMap formAttributes() {
    return formAttributes;
  }

  public HttpRequestWrapper setPath(String path) {
    this.path = path;
    return this;
  }

  public HttpRequestWrapper setMethod(HttpMethod method) {
    this.method = method;
    return this;
  }

  public HttpRequestWrapper setHeaders(MultiMap headers) {
    this.headers = headers;
    return this;
  }

  public HttpRequestWrapper setParams(MultiMap params) {
    this.params = params;
    return this;
  }

  public HttpRequestWrapper setFormAttributes(MultiMap formAttributes) {
    this.formAttributes = formAttributes;
    return this;
  }

  /**
   * Convert this to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("path", path);
    if (method != null) {
      json.put("method", method.name());
    }
    if (headers != null && !headers.isEmpty()) {
      json.put("headers", toJsonArray(headers));
    }
    if (params != null && !params.isEmpty()) {
      json.put("params", toJsonArray(params));
    }
    if (formAttributes != null && !formAttributes.isEmpty()) {
      json.put("formAttributes", toJsonArray(formAttributes));
    }
    return json;
  }

//  @Override
//  public boolean equals(Object o) {
//    if (this == o) return true;
//    if (!(o instanceof HttpRequestWrapper)) return false;
//    HttpRequestWrapper that = (HttpRequestWrapper) o;
//    return Objects.equal(path, that.path) &&
//        method == that.method &&
//        Objects.equal(headers, that.headers) &&
//        Objects.equal(params, that.params) &&
//        Objects.equal(formAttributes, that.formAttributes);
//  }
//
//  @Override
//  public int hashCode() {
//    return headers.names().stream()
//        .mapToInt(name -> Optional.ofNullable(headers.get(name))
//            .map(String::hashCode)
//            .orElse(0))
//        .reduce(Objects.hashCode(path, method, body), (sum, hash) -> 31 * sum + hash);
//  }

  private JsonArray toJsonArray(MultiMap multiMap) {
    return multiMap.names().stream()
        .map(name -> new JsonObject().put(name, multiMap.get(name)))
        .reduce(new JsonArray(), (objects, item) -> objects.add(item),
            (u, u2) -> u.addAll(u2));
  }

  private MultiMap fromJsonArray(JsonArray jsonArray) {
    MultiMap result = MultiMap.caseInsensitiveMultiMap();
    jsonArray.stream()
        .map(item -> (JsonObject) item)
        .flatMap(item -> item.stream())
        .forEach(entry -> result.add(entry.getKey(), entry.getValue().toString()));
    return result;
  }
}
