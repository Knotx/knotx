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
package io.knotx.util;

import io.vertx.core.json.JsonObject;

public final class JsonObjectUtil {

  private JsonObjectUtil() {
    //util class
  }

  public static JsonObject deepMerge(JsonObject source, JsonObject other) {
    JsonObject result = source.copy();
    other.forEach(entry -> {
      if (result.containsKey(entry.getKey())) {
        if (isKeyAJsonObject(result, entry.getKey()) && entry.getValue() instanceof JsonObject) {

          result.put(entry.getKey(),
              deepMerge(
                  source.getJsonObject(entry.getKey()),
                  (JsonObject) entry.getValue()
              )
          );
        } else { //Override whole key, if value is not jsonObject
          result.put(entry.getKey(), entry.getValue());
        }
      } else {
        result.put(entry.getKey(), entry.getValue());
      }
    });

    return result;
  }

  private static boolean isKeyAJsonObject(JsonObject object, String key) {
    return object.getMap().get(key) instanceof JsonObject;
  }
}
