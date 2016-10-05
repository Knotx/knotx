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
package com.cognifide.knotx.dataobjects;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;

abstract public class JsonObjectRequest {

  protected static MultiMap fromJsonArray(final JsonArray array) {
    MultiMap map = MultiMap.caseInsensitiveMultiMap();
    if (array != null) {
      array.stream()
          .map(item -> (JsonObject) item)
          .flatMap(item -> item.stream())
          .forEach(entry -> map.add(entry.getKey(), entry.getValue().toString()));
    }
    return map;
  }

  public abstract JsonObject toJsonObject();

  protected JsonArray toJsonArray(final MultiMap multiMap) {
    JsonArray jsonArray = new JsonArray();
    if (multiMap != null) {
      jsonArray = multiMap.names().stream()
          .map(name -> new JsonObject().put(name, multiMap.get(name)))
          .reduce(new JsonArray(),
              (objects, item) -> objects.add(item),
              (u, u2) -> u.addAll(u2));
    }
    return jsonArray;
  }
}
