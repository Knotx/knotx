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
package io.knotx.fragment;

import io.vertx.core.json.JsonObject;
import java.util.Objects;
import java.util.UUID;

public class Fragment {

  private static final String ID_KEY = "id";
  private static final String TYPE_KEY = "type";
  private static final String CONFIGURATION_KEY = "configuration";
  private static final String BODY_KEY = "body";
  private static final String PAYLOAD_KEY = "payload";

  private final String id;
  private final String type;
  private final JsonObject configuration;
  private String body;
  private final JsonObject payload;

  public Fragment(String type, JsonObject configuration, String body) {
    this.id = UUID.randomUUID().toString();
    this.type = type;
    this.configuration = configuration;
    this.body = body;
    this.payload = new JsonObject();
  }

  public Fragment(JsonObject json) {
    this.id = json.getString(ID_KEY);
    this.type = json.getString(TYPE_KEY);
    this.configuration = json.getJsonObject(CONFIGURATION_KEY);
    this.body = json.getString(BODY_KEY);
    this.payload = json.getJsonObject(PAYLOAD_KEY);
  }

  public JsonObject toJson() {
    return new JsonObject()
        .put(ID_KEY, id)
        .put(TYPE_KEY, type)
        .put(CONFIGURATION_KEY, configuration)
        .put(BODY_KEY, body)
        .put(PAYLOAD_KEY, payload);
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public JsonObject getConfiguration() {
    return configuration.copy();
  }

  public String getBody() {
    return body;
  }

  public Fragment setBody(String body) {
    this.body = body;
    return this;
  }

  public JsonObject getPayload() {
    return payload.copy();
  }

  public Fragment appendPayload(String key, Object value) {
    this.payload.put(key, value);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Fragment fragment = (Fragment) o;
    return Objects.equals(id, fragment.id) &&
        Objects.equals(type, fragment.type) &&
        Objects.equals(configuration, fragment.configuration) &&
        Objects.equals(body, fragment.body) &&
        Objects.equals(payload, fragment.payload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, configuration, body, payload);
  }

  @Override
  public String toString() {
    return "Fragment{" +
        "id='" + id + '\'' +
        ", type='" + type + '\'' +
        ", configuration=" + configuration +
        ", body='" + body + '\'' +
        ", payload=" + payload +
        '}';
  }
}
