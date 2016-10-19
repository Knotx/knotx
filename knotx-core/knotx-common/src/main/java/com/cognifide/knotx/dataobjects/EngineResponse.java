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

import java.util.Optional;

import io.vertx.core.json.JsonObject;

public class EngineResponse {

  private Optional<String> transition;

  private String html;

  private boolean success;

  public EngineResponse() {
    // No default constructor
  }

  public EngineResponse(JsonObject body) {
    this.success = body.getBoolean("success");
    if (success) {
      this.html = body.getString("html");
    }
    this.transition = Optional.ofNullable(body.getString("transition"));
  }

  public static EngineResponse success(String html) {
    EngineResponse response = new EngineResponse();
    response.success = true;
    response.html = html;

    return response;
  }

  public static EngineResponse error(String reason) {
    EngineResponse response = new EngineResponse();
    response.success = false;

    return response;
  }

  public JsonObject toJsonObject() {
    JsonObject object = new JsonObject()
        .put("success", success);
    if (success) {
      object.put("html", html);
    }
    transition.ifPresent(value -> object.put("transition", value));
    return object;
  }

  public String getHtml() {
    return html;
  }

  public boolean isSuccess() {
    return success;
  }

  public Optional<String> getTransition() {
    return transition;
  }
}
