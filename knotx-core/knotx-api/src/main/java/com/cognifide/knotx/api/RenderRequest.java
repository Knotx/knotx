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

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Map;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import rx.Observable;

public class RenderRequest {

  private HttpRequestWrapper request;

  private String template;

  private Cache<String, Observable<Map<String, Object>>> cache = CacheBuilder.newBuilder().build();

  public RenderRequest() {
    //Nothing to set by default
  }

  /**
   * Initialise the fields of this instance from the specified JSON
   *
   * @param json the JSON
   */
  public RenderRequest(JsonObject json) {
    this.request = new HttpRequestWrapper(json.getJsonObject("request"));
    if (json.containsKey("template")) {
      this.template = json.getString("template");
    }
  }

  public RenderRequest setRequest(HttpRequestWrapper request) {
    this.request = request;
    return this;
  }

  public RenderRequest setTemplate(String template) {
    this.template = template;
    return this;
  }

  public HttpRequestWrapper request() { return request;}

  public String template() {
    return template;
  }

  public Cache<String, Observable<Map<String, Object>>> getCache() {
    return cache;
  }

  /**
   * Convert this to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("request", request.toJson());
    if (template != null) {
      json.put("template", template);
    }
    return json;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RenderRequest)) return false;
    RenderRequest that = (RenderRequest) o;
    return Objects.equal(request, that.request) &&
        Objects.equal(template, that.template);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(request, template);
  }
}
