/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.dataobjects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.knotx.util.DataObjectsUtil;
import io.knotx.util.MultiMapConverter;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;

@DataObject(generateConverter = true)
public class ClientResponse {

  private int statusCode;

  private MultiMap headers = MultiMap.caseInsensitiveMultiMap();

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

  public ClientResponse clearBody() {
    this.body = Buffer.buffer();
    return this;
  }

  public JsonObject toMetadataJson() {
    JsonObject json = new JsonObject();
    json.put("statusCode", statusCode);
    json.put("headers", MultiMapConverter.toJsonObject(headers));
    return json;
  }


  public int getStatusCode() {
    return statusCode;
  }

  public ClientResponse setStatusCode(int statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  public Buffer getBody() {
    return body;
  }

  public ClientResponse setBody(Buffer body) {
    this.body = body.copy();
    return this;
  }

  @GenIgnore
  public MultiMap getHeaders() {
    return MultiMap.caseInsensitiveMultiMap().addAll(headers);
  }

  @GenIgnore
  public ClientResponse setHeaders(MultiMap headers) {
    this.headers = MultiMap.caseInsensitiveMultiMap().addAll(headers);
    return this;
  }

  /**
   * Serialization variants of MultiMap fields
   **/

  JsonObject getJsonHeaders() {
    return MultiMapConverter.toJsonObject(headers);
  }

  void setJsonHeaders(JsonObject headers) {
    this.headers = MultiMapConverter.fromJsonObject(headers);
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
        DataObjectsUtil.equalsMultiMap(this.headers, that.headers) &&
        DataObjectsUtil.equalsBody(this.body, that.body);
  }

  @Override
  public int hashCode() {
    return 31 * Objects.hashCode(statusCode, body) + DataObjectsUtil.multiMapHash(headers);
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
