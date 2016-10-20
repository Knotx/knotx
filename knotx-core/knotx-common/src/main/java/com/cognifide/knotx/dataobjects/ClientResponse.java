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

import com.google.common.base.Objects;

import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;

public class ClientResponse {

  private HttpResponseStatus statusCode;

  private MultiMap headers;

  private Buffer body;

  public ClientResponse() {
    this.headers = MultiMap.caseInsensitiveMultiMap();
  }

  /**
   * Initialise the fields of this instance from the specified JSON
   *
   * @param json the JSON
   */
  public ClientResponse(JsonObject json) {
    this.statusCode = HttpResponseStatus.valueOf(json.getInteger("statusCode"));

    if (json.containsKey("body")) {
      this.body = Buffer.buffer(json.getString("body"));
    }

    if (json.containsKey("headers")) {
      this.headers = MultiMap.caseInsensitiveMultiMap();
      json.getJsonArray("headers").stream()
          .map(item -> (JsonObject) item)
          .flatMap(JsonObject::stream)
          .forEach(entry -> this.headers.add(entry.getKey(), entry.getValue().toString()));
    }
  }

  public HttpResponseStatus statusCode() {
    return statusCode;
  }

  public MultiMap headers() {
    MultiMap result = MultiMap.caseInsensitiveMultiMap();
    headers.names().forEach(name -> result.add(name, headers.get(name)));

    return result;
  }

  public Buffer body() {
    return body;
  }

  public ClientResponse setStatusCode(HttpResponseStatus statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  public ClientResponse setHeaders(MultiMap headers) {
    this.headers = headers;
    return this;
  }

  public ClientResponse setBody(Buffer body) {
    this.body = body;
    return this;
  }

  /**
   * Convert this to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("statusCode", statusCode.code());

    if (body != null) {
      json.put("body", body.toString());
    }

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
        Objects.equal(headers, that.headers) &&
        Objects.equal(body, that.body);
  }

  @Override
  public int hashCode() {
    return headers.names().stream()
        .mapToInt(name -> Optional.ofNullable(headers.get(name))
            .map(String::hashCode)
            .orElse(0))
        .reduce(Objects.hashCode(statusCode, body), (sum, hash) -> 31 * sum + hash);
  }
}
