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
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpServerRequest;

public class ClientRequest extends Codec {
  private String path;

  private HttpMethod method;

  private MultiMap headers = MultiMap.caseInsensitiveMultiMap();

  private MultiMap params = MultiMap.caseInsensitiveMultiMap();

  private MultiMap formAttributes = MultiMap.caseInsensitiveMultiMap();

  public ClientRequest() {
    //Nothing to set by default
  }

  public ClientRequest(ClientRequest request) {
    this.path = request.path();
    this.method = request.method();
    this.headers = MultiMap.newInstance((io.vertx.core.MultiMap) request.headers().getDelegate());
    this.params = MultiMap.newInstance((io.vertx.core.MultiMap) request.params().getDelegate());
    this.formAttributes = MultiMap.newInstance((io.vertx.core.MultiMap) request.formAttributes().getDelegate());
  }

  public ClientRequest(HttpServerRequest serverRequest) {
    this.path = serverRequest.path();
    this.method = serverRequest.method();
    this.headers = MultiMap.newInstance((io.vertx.core.MultiMap) serverRequest.headers().getDelegate());
    this.params = UriHelper.getParams(serverRequest.uri());
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

  public ClientRequest setPath(String path) {
    this.path = path;
    return this;
  }

  public ClientRequest setMethod(HttpMethod method) {
    this.method = method;
    return this;
  }

  public ClientRequest setHeaders(MultiMap headers) {
    this.headers = headers;
    return this;
  }

  public ClientRequest setParams(MultiMap params) {
    this.params = params;
    return this;
  }

  public ClientRequest setFormAttributes(MultiMap formAttributes) {
    this.formAttributes = formAttributes;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ClientRequest)) return false;
    ClientRequest that = (ClientRequest) o;
    return Objects.equal(path, that.path) &&
        Objects.equal(method, that.method) &&
        equalsMultimap(this.headers, that.headers) &&
        equalsMultimap(this.params, that.params) &&
        equalsMultimap(this.formAttributes, that.formAttributes);
  }

  @Override
  public int hashCode() {
    return 41 * Objects.hashCode(path, method) + 37 * multiMapHash(headers) + 31 * multiMapHash(params) + multiMapHash(formAttributes);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("path", path)
        .add("method", method)
        .add("headers", toString(headers))
        .add("params", toString(params))
        .add("formAttributes", toString(formAttributes))
        .toString();
  }

  @Override
  public void encodeToWire(Buffer buffer) {
    encodeString(buffer, path);
    encodeString(buffer, method != null ? method.name() : StringUtils.EMPTY);
    encodeMultiMap(buffer, headers);
    encodeMultiMap(buffer, params);
    encodeMultiMap(buffer, formAttributes);
  }

  @Override
  public void decodeFromWire(AtomicInteger position, Buffer buffer) {
    path = decodeString(position, buffer);
    String decodedMethod = decodeString(position, buffer);
    if (StringUtils.isNotBlank(decodedMethod)) {
      method = HttpMethod.valueOf(decodedMethod);
    }
    headers = decodeMultiMap(position, buffer);
    params = decodeMultiMap(position, buffer);
    formAttributes = decodeMultiMap(position, buffer);
  }
}

