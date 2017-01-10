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

import com.cognifide.knotx.util.DataObjectsUtil;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class ClientResponse {

  private int statusCode;

  private JsonObject headers = new JsonObject();

  private Buffer body;

  public ClientResponse() {
    //Empty object
  }

  public ClientResponse(JsonObject json) {
    ClientResponseConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ClientResponseConverter.toJson(this, json);
    return json;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public JsonObject getHeaders() {
    return headers;
  }

  public Buffer getBody() {
    return body;
  }

  public ClientResponse setStatusCode(int statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  public ClientResponse setHeaders(JsonObject headers) {
    this.headers = headers.copy();
    return this;
  }

  public ClientResponse setBody(Buffer body) {
    this.body = body.copy();
    return this;
  }

  public ClientResponse clearBody() {
    this.body = Buffer.buffer();
    return this;
  }

  public JsonObject toMetadataJson() {
    JsonObject json = new JsonObject();
    json.put("statusCode", statusCode);
    json.put("headers", headers);
    return json;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ClientResponse)) {
      return false;
    }
    ClientResponse that = (ClientResponse) o;
    return Objects.equal(statusCode, that.statusCode) &&
        Objects.equal(this.headers, that.headers) &&
        DataObjectsUtil.equalsBody(this.body, that.body);
  }

  @Override
  public int hashCode() {
    return 31 * Objects.hashCode(statusCode, body) + Objects.hashCode(headers);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("statusCode", statusCode)
        .add("headers", headers.encode())
        .add("body", body)
        .toString();
  }
}
