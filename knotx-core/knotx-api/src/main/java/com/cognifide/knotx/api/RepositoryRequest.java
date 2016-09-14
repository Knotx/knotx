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
package com.cognifide.knotx.api;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;

public class RepositoryRequest extends JsonObjectRequest {

  private final String path;

  private final MultiMap headers;

  public RepositoryRequest(String path, MultiMap headers) {
    this.path = path;
    this.headers = headers;
  }

  public RepositoryRequest(JsonObject repoMessage) {
    this.path = repoMessage.getString("path");
    this.headers = MultiMap.caseInsensitiveMultiMap();

    repoMessage.getJsonArray("headers").stream()
        .map(item -> (JsonObject) item)
        .flatMap(item -> item.stream())
        .forEach(entry -> headers.add(entry.getKey(), entry.getValue().toString()));
  }

  @Override
  public JsonObject toJsonObject() {
    return new JsonObject()
        .put("path", path)
        .put("headers", toJsonArray(headers));
  }

  public String getPath() {
    return path;
  }

  public MultiMap getHeaders() {
    return headers;
  }
}

