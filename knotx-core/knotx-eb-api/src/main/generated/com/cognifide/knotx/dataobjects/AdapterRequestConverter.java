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
 * Converter for {@link com.cognifide.knotx.dataobjects.AdapterRequest}.
 *
 * NOTE: This class has been automatically generated from the {@link com.cognifide.knotx.dataobjects.AdapterRequest} original class using Vert.x codegen.
 */
public class AdapterRequestConverter {

  public static void fromJson(JsonObject json, AdapterRequest obj) {
    if (json.getValue("params") instanceof JsonObject) {
      obj.setParams(((JsonObject)json.getValue("params")).copy());
    }
    if (json.getValue("request") instanceof JsonObject) {
      obj.setRequest(new com.cognifide.knotx.dataobjects.ClientRequest((JsonObject)json.getValue("request")));
    }
  }

  public static void toJson(AdapterRequest obj, JsonObject json) {
    if (obj.getParams() != null) {
      json.put("params", obj.getParams());
    }
    if (obj.getRequest() != null) {
      json.put("request", obj.getRequest().toJson());
    }
  }
}