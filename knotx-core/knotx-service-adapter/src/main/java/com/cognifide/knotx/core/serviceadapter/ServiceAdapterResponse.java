/*
 * Knot.x - Sample App with Mock service
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
package com.cognifide.knotx.core.serviceadapter;

import java.util.Map;

import io.vertx.core.json.JsonObject;

public class ServiceAdapterResponse {

  private Map<String, Object> serviceResult;

  private boolean success;

  private String reason;

  private ServiceAdapterResponse() {
    // hidden default constructor
  }


  public ServiceAdapterResponse(JsonObject body) {
    this.success = body.getBoolean("success");
    if (success) {
      // TODO deserialize service result
    } else {
      this.reason = body.getString("reason");
    }
  }

  public static ServiceAdapterResponse success(Map<String, Object> serviceResult) {
    ServiceAdapterResponse response = new ServiceAdapterResponse();
    response.success = true;
    response.serviceResult = serviceResult;

    return response;
  }

  public static ServiceAdapterResponse error(String reason) {
    ServiceAdapterResponse response = new ServiceAdapterResponse();
    response.success = false;
    response.reason = reason;

    return response;
  }

  public JsonObject toJsonObject() {
    JsonObject object = new JsonObject()
            .put("success", success);
    if (success) {
      // TODO serialize service result
    } else {
      object.put("reason", reason);
    }
    return object;
  }

  public Map<String, Object> getServiceResult() {
    return serviceResult;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getReason() {
    return reason;
  }

}