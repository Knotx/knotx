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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class KnotxServerConfiguration {

  private Integer httpPort;

  private Set<String> allowedResponseHeaders;

  private Map<String, String> repositoryAddressMapping;

  private EnumMap<HttpMethod, List<RoutingCriteria>> engineRouting;

  public KnotxServerConfiguration(JsonObject config) {
    httpPort = config.getInteger("http.port");

    engineRouting = Maps.newEnumMap(HttpMethod.class);
    config.getJsonObject("engines").stream()
        .forEach(entry -> parseMethodRouting(entry));

    repositoryAddressMapping = Maps.newHashMap();
    config.getJsonArray("repositories").stream()
        .map(item -> (JsonObject) item)
        .forEach(object -> repositoryAddressMapping.put(object.getString("path"), object.getString("address")));

    allowedResponseHeaders = config.getJsonArray("allowed.response.headers").stream()
        .map(item -> ((String) item).toLowerCase())
        .collect(Collectors.toSet());
  }

  public Integer httpPort() {
    return httpPort;
  }

  public Optional<String> repositoryForPath(final String path) {
    return repositoryAddressMapping.entrySet().stream()
        .filter(mapping -> path.matches(mapping.getKey()))
        .findFirst().map(matching -> matching.getValue());
  }

  public Set<String> allowedResponseHeaders() {
    return allowedResponseHeaders;
  }

  public EnumMap<HttpMethod, List<RoutingCriteria>> getEngineRouting() {
    return engineRouting;
  }

  private void parseMethodRouting(Map.Entry<String, Object> entry) {
    final List<RoutingCriteria> methodCriteria = getMethodCriterias(HttpMethod.valueOf(entry.getKey()));

    ((JsonArray) entry.getValue()).stream()
        .map(item -> (JsonObject) item)
        .map(item -> new RoutingCriteria(item.getString("path"), item.getString("address")))
        .forEach(methodCriteria::add);
  }

  private List<RoutingCriteria> getMethodCriterias(HttpMethod method) {
    List<RoutingCriteria> routingCriterias = Lists.newArrayList();
    if (engineRouting.containsKey(method)) {
      routingCriterias = engineRouting.get(method);
    } else {
      engineRouting.put(method, routingCriterias);
    }

    return routingCriterias;
  }
}
