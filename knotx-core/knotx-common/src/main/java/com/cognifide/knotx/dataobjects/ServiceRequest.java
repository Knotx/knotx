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
package com.cognifide.knotx.dataobjects;

import com.google.common.base.Objects;

import io.vertx.core.json.JsonObject;

public class ServiceRequest {

  private HttpRequestWrapper request;

  private JsonObject params;

  public ServiceRequest() {
    //Nothing to set by default
  }

  /**
   * Initialise the fields of this instance from the specified JSON
   *
   * @param json the JSON
   */
  public ServiceRequest(JsonObject json) {
    this.request = new HttpRequestWrapper(json.getJsonObject("request"));
    if (json.containsKey("params")) {
      this.params = json.getJsonObject("params");
    }
  }

  public ServiceRequest setRequest(HttpRequestWrapper request) {
    this.request = request;
    return this;
  }

  public ServiceRequest setParams(JsonObject params) {
    this.params = params;
    return this;
  }

  public HttpRequestWrapper request() {
    return request;
  }

  public JsonObject params() {
    return params;
  }

  /**
   * Convert this to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("request", request.toJson());
    if (params != null) {
      json.put("params", params);
    }
    return json;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RenderRequest)) return false;
    ServiceRequest that = (ServiceRequest) o;
    return Objects.equal(request, that.request) &&
            Objects.equal(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(request, params);
  }

}
