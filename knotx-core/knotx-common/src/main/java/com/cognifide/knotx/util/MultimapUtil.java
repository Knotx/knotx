/*
 * Knot.x - Reactive microservice assembler - Common
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
package com.cognifide.knotx.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;

public class MultimapUtil {

  public static JsonObject toJsonObject(MultiMap multiMap, boolean caseSensitive) {
    JsonObject json = new JsonObject();

    ((io.vertx.core.MultiMap) multiMap.getDelegate()).forEach(
        entry -> {
          String key = caseSensitive ? entry.getKey() : entry.getKey().toLowerCase();
          JsonArray values;
          if (json.containsKey(key)) {
            values = json.getJsonArray(key);
          } else {
            values = new JsonArray();
            json.put(key, values);
          }
          values.add(entry.getValue());
        }
    );

    return json;
  }
}
