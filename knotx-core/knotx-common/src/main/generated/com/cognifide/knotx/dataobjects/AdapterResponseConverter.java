/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.cognifide.knotx.dataobjects;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link com.cognifide.knotx.dataobjects.AdapterResponse}.
 *
 * NOTE: This class has been automatically generated from the {@link com.cognifide.knotx.dataobjects.AdapterResponse} original class using Vert.x codegen.
 */
public class AdapterResponseConverter {

  public static void fromJson(JsonObject json, AdapterResponse obj) {
    if (json.getValue("response") instanceof JsonObject) {
      obj.setResponse(new com.cognifide.knotx.dataobjects.ClientResponse((JsonObject)json.getValue("response")));
    }
    if (json.getValue("signal") instanceof String) {
      obj.setSignal((String)json.getValue("signal"));
    }
  }

  public static void toJson(AdapterResponse obj, JsonObject json) {
    if (obj.getResponse() != null) {
      json.put("response", obj.getResponse().toJson());
    }
    if (obj.getSignal() != null) {
      json.put("signal", obj.getSignal());
    }
  }
}