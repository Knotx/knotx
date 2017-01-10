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
package com.cognifide.knotx.dataobjects;


import com.cognifide.knotx.http.UriHelper;
import com.cognifide.knotx.util.MultimapUtil;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;

@DataObject(generateConverter = true)
public class ClientRequest {

  private String path;

  private HttpMethod method;

  private JsonObject headers = new JsonObject();

  private JsonObject params = new JsonObject();

  private JsonObject formAttributes = new JsonObject();

  public ClientRequest() {
    //Nothing to set by default
  }

  public ClientRequest(JsonObject json) {
    ClientRequestConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ClientRequestConverter.toJson(this, json);
    return json;
  }

  public ClientRequest(ClientRequest request) {
    this.path = request.path;
    this.method = request.method;
    this.headers = request.headers.copy();
    this.params = request.params.copy();
    this.formAttributes = request.formAttributes.copy();
  }

  public ClientRequest(HttpServerRequest serverRequest) {
    this.path = serverRequest.path();
    this.method = serverRequest.method();
    this.headers = MultimapUtil.toJsonObject(serverRequest.headers(), false);
    this.params = UriHelper.getParams(serverRequest.uri());
    this.formAttributes = MultimapUtil.toJsonObject(serverRequest.formAttributes(), true);
  }

  public String getPath() {
    return path;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public JsonObject getHeaders() {
    return headers;
  }

  public JsonObject getParams() {
    return params;
  }

  public JsonObject getFormAttributes() {
    return formAttributes;
  }

  public ClientRequest setPath(String path) {
    this.path = path;
    return this;
  }

  public ClientRequest setMethod(HttpMethod method) {
    this.method = method;
    return this;
  }

  public ClientRequest setHeaders(JsonObject headers) {
    this.headers = headers.copy();
    return this;
  }

  public ClientRequest setParams(JsonObject params) {
    this.params = params.copy();
    return this;
  }

  public ClientRequest setFormAttributes(JsonObject formAttributes) {
    this.formAttributes = formAttributes.copy();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ClientRequest)) {
      return false;
    }
    ClientRequest that = (ClientRequest) o;
    return Objects.equal(path, that.path) &&
        Objects.equal(method, that.method) &&
        Objects.equal(headers, that.headers) &&
        Objects.equal(params, that.params) &&
        Objects.equal(formAttributes, that.formAttributes);
  }

  @Override
  public int hashCode() {
    return 41 * Objects.hashCode(path, method) + 37 * Objects.hashCode(headers) + 31 * Objects.hashCode(params)
        + Objects.hashCode(formAttributes);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("path", path)
        .add("method", method)
        .add("headers", headers.encode())
        .add("params", params.encode())
        .add("formAttributes", formAttributes.encode())
        .toString();
  }

}

