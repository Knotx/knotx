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
import com.cognifide.knotx.util.DataObjectsUtil;
import com.cognifide.knotx.util.MultimapConverter;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpServerRequest;

@DataObject(generateConverter = true)
public class ClientRequest {

  private String path;

  private HttpMethod method;

  private MultiMap headers = MultiMap.caseInsensitiveMultiMap();

  private MultiMap params = MultiMap.caseInsensitiveMultiMap();

  private MultiMap formAttributes = MultiMap.caseInsensitiveMultiMap();

  public ClientRequest() {
    //Nothing to set by default
  }

  public ClientRequest(JsonObject json) {
    ClientRequestConverter.fromJson(json, this);
  }

  public ClientRequest(ClientRequest request) {
    this.path = request.path;
    this.method = request.method;
    this.headers = MultiMap.caseInsensitiveMultiMap().setAll(request.headers);
    this.params = MultiMap.caseInsensitiveMultiMap().setAll(request.params);
    this.formAttributes = MultiMap.caseInsensitiveMultiMap().setAll(request.formAttributes);
  }

  public ClientRequest(HttpServerRequest serverRequest) {
    this.path = serverRequest.path();
    this.method = serverRequest.method();
    this.headers = MultiMap.caseInsensitiveMultiMap().setAll(serverRequest.headers());
    this.params = UriHelper.getParams(serverRequest.uri());
    this.formAttributes = MultiMap.caseInsensitiveMultiMap().setAll(serverRequest.formAttributes());
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ClientRequestConverter.toJson(this, json);
    return json;
  }

  public String getPath() {
    return path;
  }

  public ClientRequest setPath(String path) {
    this.path = path;
    return this;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public ClientRequest setMethod(HttpMethod method) {
    this.method = method;
    return this;
  }

  @GenIgnore
  public MultiMap getHeaders() {
    return MultiMap.caseInsensitiveMultiMap().addAll(headers);
  }

  @GenIgnore
  public ClientRequest setHeaders(MultiMap headers) {
    this.headers = MultiMap.caseInsensitiveMultiMap().addAll(headers);
    return this;
  }

  @GenIgnore
  public MultiMap getParams() {
    return MultiMap.caseInsensitiveMultiMap().addAll(params);
  }

  @GenIgnore
  public ClientRequest setParams(MultiMap params) {
    this.params = MultiMap.caseInsensitiveMultiMap().addAll(params);
    return this;
  }

  @GenIgnore
  public MultiMap getFormAttributes() {
    return MultiMap.caseInsensitiveMultiMap().addAll(formAttributes);
  }

  @GenIgnore
  public ClientRequest setFormAttributes(MultiMap formAttributes) {
    this.formAttributes = MultiMap.caseInsensitiveMultiMap().addAll(formAttributes);
    return this;
  }

  /**
   * Serialization variants of Multimap fields
   **/

  JsonObject getJsonHeaders() {
    return MultimapConverter.toJsonObject(headers);
  }

  void setJsonHeaders(JsonObject headers) {
    this.headers = MultimapConverter.fromJsonObject(headers);
  }

  JsonObject getJsonParams() {
    return MultimapConverter.toJsonObject(params);
  }

  void setJsonParams(JsonObject params) {
    this.params = MultimapConverter.fromJsonObject(params);
  }

  JsonObject getJsonFormAttributes() {
    return MultimapConverter.toJsonObject(formAttributes);
  }

  void setJsonFormAttributes(JsonObject formAttributes) {
    this.formAttributes = MultimapConverter.fromJsonObject(formAttributes);
  }

  /**
   * Object overrides
   **/

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
        DataObjectsUtil.equalsMultimap(headers, that.headers) &&
        DataObjectsUtil.equalsMultimap(params, that.params) &&
        DataObjectsUtil.equalsMultimap(formAttributes, that.formAttributes);
  }

  @Override
  public int hashCode() {
    return 41 * Objects.hashCode(path, method) + 37 * DataObjectsUtil.multiMapHash(headers)
        + 31 * DataObjectsUtil.multiMapHash(params)
        + DataObjectsUtil.multiMapHash(formAttributes);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("path", path)
        .add("method", method)
        .add("headers", DataObjectsUtil.toString(headers))
        .add("params", DataObjectsUtil.toString(params))
        .add("formAttributes", DataObjectsUtil.toString(formAttributes))
        .toString();
  }

}

