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
package com.cognifide.knotx.http;

import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class UriHelper {

  public static JsonObject getParams(String uri) {
    JsonObject json = new JsonObject();
    QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
    queryStringDecoder.parameters().entrySet().forEach(
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
}
