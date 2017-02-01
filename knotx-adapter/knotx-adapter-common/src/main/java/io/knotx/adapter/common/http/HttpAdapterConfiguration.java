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
package io.knotx.adapter.common.http;

import io.knotx.http.StringToPatternFunction;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.stream.Collectors;

public class HttpAdapterConfiguration {

  private String address;

  private List<ServiceMetadata> services;

  private JsonObject clientOptions;

  public HttpAdapterConfiguration(JsonObject config) {
    address = config.getString("address");
    services = config.getJsonArray("services").stream()
        .map(item -> (JsonObject) item)
        .map(item -> {
          ServiceMetadata metadata = new ServiceMetadata();
          metadata.path = item.getString("path");
          metadata.domain = item.getString("domain");
          metadata.port = item.getInteger("port");
          metadata.allowedRequestHeaderPatterns = item
              .getJsonArray("allowedRequestHeaders", new JsonArray()).stream()
              .map(object -> (String) object)
              .map(new StringToPatternFunction())
              .collect(Collectors.toList());
          return metadata;
        }).collect(Collectors.toList());
    clientOptions = config.getJsonObject("client.options", new JsonObject());
  }

  public JsonObject getClientOptions() {
    return clientOptions;
  }

  public List<ServiceMetadata> getServices() {
    return services;
  }

  public String getAddress() {
    return address;
  }
}
