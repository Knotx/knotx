/*
 * Knot.x - Reactive microservice assembler - HTTP Server
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
package com.cognifide.knotx.server;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonObject;

public class KnotxServerConfiguration {

  private Integer httpPort;

  private String engineAddress;

  private Map<String, String> repositoryAddressMapping;

  public KnotxServerConfiguration(JsonObject config) {
    httpPort = config.getInteger("http.port");

    engineAddress = config.getJsonObject("engine").getString("address");

    repositoryAddressMapping = Maps.newHashMap();
    config.getJsonArray("repositories").stream()
        .map(item -> (JsonObject) item)
        .forEach(object -> repositoryAddressMapping.put(object.getString("path"), object.getString("address")));
  }

  public Integer httpPort() {
    return httpPort;
  }

  List<String> serviceCallHeaders() {
    return serviceCallHeaders;
  }
  public List<String> serviceCallHeaders() {
    return serviceCallHeaders;
  }

  public String engineAddress() {
    return engineAddress;
  }

  public Optional<String> repositoryForPath(final String path) {
    return repositoryAddressMapping.entrySet().stream()
        .filter(mapping -> path.matches(mapping.getKey()))
        .findFirst().map(matching -> matching.getValue());
  }
}
