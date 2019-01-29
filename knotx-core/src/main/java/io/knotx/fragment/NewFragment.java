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


import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@DataObject(generateConverter = true)
public class NewFragment {

  private List<FragmentProcessingLog> auditLog;
  private JsonObject payload;

  private String type;
  private JsonObject configuration;
  private String body;

  public NewFragment(String type, JsonObject configuration, String body) {
    init();
    this.type = type;
    this.configuration = configuration;
    this.body = body;
  }

  public NewFragment(JsonObject json) {
    init();
    NewFragmentConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    NewFragmentConverter.toJson(this, json);
    return json;
  }

  private void init() {
    this.auditLog = new ArrayList<>();
    this.payload = new JsonObject();
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

  public void appendLog(FragmentProcessingLog historyLog) {
    auditLog.add(historyLog);
  }

  public boolean failed() {
    if (auditLog.isEmpty()) {
      return false;
    }
    return auditLog.stream()
        .anyMatch(f -> f.getStatus().equals(FragmentProcessingStatus.FAILURE));
  }

  public boolean processed() {
    if (auditLog.isEmpty()) {
      return true;
    }
    return auditLog.stream()
        .allMatch(f -> f.getStatus().equals(FragmentProcessingStatus.SUCCESS));
  }

  public JsonObject getPayload() {
    return payload;
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
        "auditLog=" + auditLog +
        ", type='" + type + '\'' +
        ", configuration=" + configuration +
        ", body='" + body + '\'' +
        '}';
  }
}
