/*
 * Knot.x - Reactive microservice assembler - Core
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
package io.knotx.util;

import io.vertx.core.json.JsonObject;

public class JsonObjectUtil {

  public static void deepMerge(JsonObject source, JsonObject other) {
    other.forEach(entry -> {
      if (source.containsKey(entry.getKey())) {
        if (source.getMap().get(entry.getKey()) instanceof JsonObject
            && entry.getValue() instanceof JsonObject) {
          JsonObject sourceJson = source.getJsonObject(entry.getKey());
          deepMerge(sourceJson, (JsonObject) entry.getValue());
          source.put(entry.getKey(), sourceJson);
        } else { //Override whole key, if value is not jsonObject
          source.put(entry.getKey(), entry.getValue());
        }
      } else {
        source.put(entry.getKey(), entry.getValue());
      }
    });
  }
}
