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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.concurrent.atomic.AtomicInteger;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;

public class ClientResponse extends Codec {

  private HttpResponseStatus statusCode;

  private MultiMap headers = MultiMap.caseInsensitiveMultiMap();

  private Buffer body;

  public ClientResponse() {
    //Empty object
  }

  public HttpResponseStatus statusCode() {
    return statusCode;
  }

  public MultiMap headers() {
    return headers;
  }

  public Buffer body() {
    return body;
  }

  public ClientResponse clearBody() {
    body = Buffer.buffer("");
    return this;
  }

  public ClientResponse setStatusCode(HttpResponseStatus statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  public ClientResponse setHeaders(MultiMap headers) {
    this.headers = MultiMap.newInstance((io.vertx.core.MultiMap) headers.getDelegate());
    return this;
  }

  public ClientResponse setBody(Buffer body) {
    this.body = body;
    return this;
  }

  public JsonObject toMetadataJson() {
    JsonObject json = new JsonObject();
    json.put("statusCode", statusCode.code());

    json.put("headers", headers.names().stream()
        .map(name -> new JsonObject().put(name, headers.get(name)))
        .reduce(new JsonArray(), JsonArray::add, JsonArray::addAll));

    return json;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ClientResponse)) return false;
    ClientResponse that = (ClientResponse) o;
    return Objects.equal(statusCode, that.statusCode) &&
        equalsMultimap(this.headers, that.headers) &&
        equalsBody(this.body, that.body);
  }

  @Override
  public int hashCode() {
    return 31 * Objects.hashCode(statusCode, body) + multiMapHash(headers);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("statusCode", statusCode)
        .add("headers", toString(headers))
        .add("body", body)
        .toString();
  }

  @Override
  public void encodeToWire(io.vertx.core.buffer.Buffer buffer) {
    encodeInt(buffer, statusCode != null ? statusCode.code() : 0);
    encodeMultiMap(buffer, headers);
    encodeBuffer(buffer, body);
  }

  @Override
  public void decodeFromWire(AtomicInteger position, io.vertx.core.buffer.Buffer buffer) {
    Integer code = decodeInt(position, buffer);
    if (code > 0) {
      statusCode = HttpResponseStatus.valueOf(code);
    }
    headers = decodeMultiMap(position, buffer);
    body = decodeBuffer(position, buffer);
  }
}
