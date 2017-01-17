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

public class MultimapConverter {

  /**
   * Converts Multimap to JsonObject<br/>
   * It expects the MultiMap key, contains List of String objects, so the result of conversion will look like below<br/>
   * <pre>
   *   {
   *      "mapKey1": ["val1", "val2"],
   *      "mapKey2": ["val1"]
   *   }
   * </pre>
   * @param multiMap - Multimap to convert
   * @return - JsonObject with JsonArray under each object key
   */
  public static JsonObject toJsonObject(MultiMap multiMap) {
    JsonObject json = new JsonObject();

    ((io.vertx.core.MultiMap) multiMap.getDelegate()).forEach(
        entry -> {
          JsonArray values;
          if (json.containsKey(entry.getKey())) {
            values = json.getJsonArray(entry.getKey());
          } else {
            values = new JsonArray();
            json.put(entry.getKey(), values);
          }
          values.add(entry.getValue());
        }
    );

    return json;
  }

  /**
   * Converts JsonObject to Multimap. It expects the JsonObject key, contains JsonArray with list of String objects.<br/>
   * Each jsonObject key is converted into MutliMap "key", while JsonArray as List of String objects for this velu.
   * @param json - JsonObject to convert
   * @return - Multimap
   */
  public static MultiMap fromJsonObject(JsonObject json) {
    MultiMap map = MultiMap.caseInsensitiveMultiMap();

    json.stream()
        .forEach(
            entry ->
                ((JsonArray) entry.getValue())
                    .stream()
                    .forEach(
                        value ->
                            map.add(entry.getKey(), (String) value)
                    )
        );

    return map;
  }
}
