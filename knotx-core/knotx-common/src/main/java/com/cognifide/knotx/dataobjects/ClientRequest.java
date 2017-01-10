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
import com.cognifide.knotx.util.MultimapUtil;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    return new JsonObject();
  }

  public ClientRequest(ClientRequest request) {
    this.path = request.path;
    this.method = request.method;
    this.headers = Collections.unmodifiableMap(new HashMap<>(request.headers));
    this.params = Collections.unmodifiableMap(new HashMap<>(request.params));
    this.formAttributes = Collections.unmodifiableMap(new HashMap<>(request.formAttributes));
  }

  public ClientRequest(HttpServerRequest serverRequest) {
    this.path = serverRequest.path();
    this.method = serverRequest.method();
    this.headers = MultimapUtil.toMap(serverRequest.headers(), false);
    this.params = UriHelper.getParams(serverRequest.uri());
    this.formAttributes = MultimapUtil.toMap(serverRequest.formAttributes(), true);

  }

  public String getPath() {
    return path;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public Map<String, List<String>> getHeaders() {
    return headers;
  }

  public Map<String, List<String>> getParams() {
    return params;
  }

  public Map<String, List<String>> getFormAttributes() {
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

  public ClientRequest setHeaders(Map<String, List<String>> headers) {
    this.headers = Collections.unmodifiableMap(headers);
    return this;
  }

  public ClientRequest setParams(Map<String, List<String>> params) {
    this.params = Collections.unmodifiableMap(params);
    return this;
  }

  public ClientRequest setFormAttributes(Map<String, List<String>> formAttributes) {
    this.formAttributes = Collections.unmodifiableMap(formAttributes);
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
        DataObjectsUtil.equalsMap(this.headers, that.headers) &&
        DataObjectsUtil.equalsMap(this.params, that.params) &&
        DataObjectsUtil.equalsMap(this.formAttributes, that.formAttributes);
  }

  @Override
  public int hashCode() {
    return 41 * Objects.hashCode(path, method) + 37 * DataObjectsUtil.mapHash(headers) + 31 * DataObjectsUtil.mapHash(params)
        + DataObjectsUtil.mapHash(formAttributes);
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

