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
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@DataObject(generateConverter = true)
public class ClientResponse {

  private HttpResponseStatus statusCode;

  private JsonObject headers = new JsonObject();

  private Buffer body;

  public ClientResponse() {
    //Empty object
  }


  public ClientResponse(JsonObject json) {
    ClientResponseConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    return new JsonObject();
  }

  public HttpResponseStatus getStatusCode() {
    return statusCode;
  }

  public Map<String, List<String>> getHeaders() {
    return headers;
  }

  public Buffer getBody() {
    return body;
  }

  public ClientResponse setStatusCode(HttpResponseStatus statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  public ClientResponse setHeaders(Map<String, List<String>> headers) {
    this.headers = Collections.unmodifiableMap(headers);
    return this;
  }

  public ClientResponse setBody(Buffer body) {
    this.body = body;
    return this;
  }

  public JsonObject toMetadataJson() {
    JsonObject json = new JsonObject();
    json.put("statusCode", statusCode.code());

    json.put("headers", headers.keySet().stream()
        .map(name -> new JsonObject().put(name, headers.get(name)
                .stream()
                .reduce(new JsonArray(), JsonArray::add, JsonArray::addAll)
            )
        ));

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
        DataObjectsUtil.equalsMap(this.headers, that.headers) &&
        DataObjectsUtil.equalsBody(this.body, that.body);
  }

  @Override
  public int hashCode() {
    return 31 * Objects.hashCode(statusCode, body) + DataObjectsUtil.mapHash(headers);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("statusCode", statusCode)
        .add("headers", DataObjectsUtil.toString(headers))
        .add("body", body)
        .toString();
  }
}
