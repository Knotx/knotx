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

public class NewFragment {

  private String type;
  private JsonObject configuration;
  private String body;

  public NewFragment(String type, JsonObject configuration, String body) {
    this.type = type;
    this.configuration = configuration;
    this.body = body;
  }

  public String getType() {
    return type;
  }

  public NewFragment setType(String type) {
    this.type = type;
    return this;
  }

  public JsonObject getConfiguration() {
    return configuration;
  }

  public NewFragment setConfiguration(JsonObject configuration) {
    this.configuration = configuration;
    return this;
  }

  public String getBody() {
    return body;
  }

  public NewFragment setBody(String body) {
    this.body = body;
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
    NewFragment that = (NewFragment) o;
    return Objects.equals(type, that.type) &&
        Objects.equals(configuration, that.configuration) &&
        Objects.equals(body, that.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, configuration, body);
  }

  @Override
  public String toString() {
    return "NewFragment{" +
        "type='" + type + '\'' +
        ", configuration=" + configuration +
        ", body='" + body + '\'' +
        '}';
  }
}
