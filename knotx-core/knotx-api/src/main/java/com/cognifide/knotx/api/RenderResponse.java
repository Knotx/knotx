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
package com.cognifide.knotx.api;

import io.vertx.core.json.JsonObject;

public class RenderResponse extends JsonObjectRequest {

  private String html;

  private boolean success;

  private String reason;

  public RenderResponse() {
    // No default constructor
  }

  public RenderResponse(JsonObject body) {
    this.success = body.getBoolean("success");
    if (success) {
      this.html = body.getString("html");
    } else {
      this.reason = body.getString("reason");
    }
  }

  public static RenderResponse success(String html) {
    RenderResponse response = new RenderResponse();
    response.success = true;
    response.html = html;

    return response;
  }

  public static RenderResponse error(String reason) {
    RenderResponse response = new RenderResponse();
    response.success = false;
    response.reason = reason;

    return response;
  }

  @Override
  public JsonObject toJsonObject() {
    JsonObject object = new JsonObject()
        .put("success", success);
    if (success) {
      object.put("html", html);
    } else {
      object.put("reason", reason);
    }
    return object;
  }

  public String getHtml() {
    return html;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getReason() {
    return reason;
  }
}
