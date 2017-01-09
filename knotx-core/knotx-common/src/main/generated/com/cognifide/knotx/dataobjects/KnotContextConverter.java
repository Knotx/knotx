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
 * Converter for {@link com.cognifide.knotx.dataobjects.KnotContext}.
 *
 * NOTE: This class has been automatically generated from the {@link com.cognifide.knotx.dataobjects.KnotContext} original class using Vert.x codegen.
 */
public class KnotContextConverter {

  public static void fromJson(JsonObject json, KnotContext obj) {
    if (json.getValue("clientRequest") instanceof JsonObject) {
      obj.setClientRequest(new com.cognifide.knotx.dataobjects.ClientRequest((JsonObject)json.getValue("clientRequest")));
    }
    if (json.getValue("clientResponse") instanceof JsonObject) {
      obj.setClientResponse(new com.cognifide.knotx.dataobjects.ClientResponse((JsonObject)json.getValue("clientResponse")));
    }
    if (json.getValue("fragments") instanceof JsonArray) {
      java.util.ArrayList<com.cognifide.knotx.fragments.Fragment> list = new java.util.ArrayList<>();
      json.getJsonArray("fragments").forEach( item -> {
        if (item instanceof JsonObject)
          list.add(new com.cognifide.knotx.fragments.Fragment((JsonObject)item));
      });
      obj.setFragments(list);
    }
    if (json.getValue("transition") instanceof String) {
      obj.setTransition((String)json.getValue("transition"));
    }
  }

  public static void toJson(KnotContext obj, JsonObject json) {
    if (obj.getClientRequest() != null) {
      json.put("clientRequest", obj.getClientRequest().toJson());
    }
    if (obj.getClientResponse() != null) {
      json.put("clientResponse", obj.getClientResponse().toJson());
    }
    if (obj.getFragments() != null) {
      json.put("fragments", new JsonArray(
          obj.getFragments().
              stream().
              map(item -> item.toJson()).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getTransition() != null) {
      json.put("transition", obj.getTransition());
    }
  }
}