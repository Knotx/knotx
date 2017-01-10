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
 * Converter for {@link com.cognifide.knotx.dataobjects.ClientRequest}.
 *
 * NOTE: This class has been automatically generated from the {@link com.cognifide.knotx.dataobjects.ClientRequest} original class using Vert.x codegen.
 */
public class ClientRequestConverter {

  public static void fromJson(JsonObject json, ClientRequest obj) {
    if (json.getValue("formAttributes") instanceof JsonObject) {
      obj.setFormAttributes(((JsonObject)json.getValue("formAttributes")).copy());
    }
    if (json.getValue("headers") instanceof JsonObject) {
      obj.setHeaders(((JsonObject)json.getValue("headers")).copy());
    }
    if (json.getValue("method") instanceof String) {
      obj.setMethod(io.vertx.core.http.HttpMethod.valueOf((String)json.getValue("method")));
    }
    if (json.getValue("params") instanceof JsonObject) {
      obj.setParams(((JsonObject)json.getValue("params")).copy());
    }
    if (json.getValue("path") instanceof String) {
      obj.setPath((String)json.getValue("path"));
    }
  }

  public static void toJson(ClientRequest obj, JsonObject json) {
    if (obj.getFormAttributes() != null) {
      json.put("formAttributes", obj.getFormAttributes());
    }
    if (obj.getHeaders() != null) {
      json.put("headers", obj.getHeaders());
    }
    if (obj.getMethod() != null) {
      json.put("method", obj.getMethod().name());
    }
    if (obj.getParams() != null) {
      json.put("params", obj.getParams());
    }
    if (obj.getPath() != null) {
      json.put("path", obj.getPath());
    }
  }
}