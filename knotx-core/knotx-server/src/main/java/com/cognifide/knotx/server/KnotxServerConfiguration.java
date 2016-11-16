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

  private boolean displayExceptionDetails;

  private Integer httpPort;

  private Set<String> allowedResponseHeaders;

  private Map<String, RepositoryEntry> repositoryAddressMapping;

  private String splitterAddress;

  private EnumMap<HttpMethod, List<RoutingEntry>> engineRouting;

  public KnotxServerConfiguration(JsonObject config) {
    httpPort = config.getInteger("http.port");
    splitterAddress = config.getJsonObject("splitter").getString("address");

    displayExceptionDetails = config.getBoolean("displayExceptionDetails", false);

    engineRouting = Maps.newEnumMap(HttpMethod.class);
    config.getJsonObject("routing").stream()
        .forEach(entry -> parseMethodRouting(entry));

    repositoryAddressMapping = Maps.newHashMap();
    config.getJsonArray("repositories").stream()
        .map(item -> (JsonObject) item)
        .forEach(object -> repositoryAddressMapping.put(object.getString("path"),
            new RepositoryEntry(object.getString("address"), object.getBoolean("doProcessing", true))));

    allowedResponseHeaders = config.getJsonArray("allowed.response.headers").stream()
        .map(item -> ((String) item).toLowerCase())
        .collect(Collectors.toSet());
  }

  public String splitterAddress() {
    return splitterAddress;
  }

  public boolean displayExceptionDetails() {
    return displayExceptionDetails;
  }

  public Integer httpPort() {
    return httpPort;
  }

  public Optional<RepositoryEntry> repositoryForPath(final String path) {
    return repositoryAddressMapping.entrySet().stream()
        .filter(mapping -> path.matches(mapping.getKey()))
        .findFirst().map(matching -> matching.getValue());
  }

  public Set<String> allowedResponseHeaders() {
    return allowedResponseHeaders;
  }

  public EnumMap<HttpMethod, List<RoutingEntry>> getEngineRouting() {
    return engineRouting;
  }

  private void parseMethodRouting(Map.Entry<String, Object> entry) {
    final List<RoutingEntry> methodCriteria = getMethodCriterias(HttpMethod.valueOf(entry.getKey()));

    ((JsonArray) entry.getValue()).stream()
        .map(item -> (JsonObject) item)
        .map(item -> parseRoutingCriteria(item))
        .forEach(methodCriteria::add);
  }

  private RoutingEntry parseRoutingCriteria(JsonObject object) {
    return new RoutingEntry(object.getString("path"), object.getString("address"), parseOnTransition(object.getJsonObject("onTransition")));
  }

  private Map<String, RoutingEntry> parseOnTransition(JsonObject onTransition) {
    Map<String, RoutingEntry> transitions = Maps.newHashMap();

    if (onTransition != null) {
      onTransition.stream().forEach(
          entry -> transitions.put(entry.getKey(), parseRoutingCriteria((JsonObject) entry.getValue()))
      );
    }

    return transitions;
  }

  private List<RoutingEntry> getMethodCriterias(HttpMethod method) {
    List<RoutingEntry> routingEntries = Lists.newArrayList();
    if (engineRouting.containsKey(method)) {
      routingEntries = engineRouting.get(method);
    } else {
      engineRouting.put(method, routingEntries);
    }

    return routingEntries;
  }

  public class RepositoryEntry {
    private String address;
    private boolean doProcessing;

    public RepositoryEntry(String address, boolean doProcessing) {
      this.address = address;
      this.doProcessing = doProcessing;
    }

    public String address() {
      return address;
    }

    public boolean doProcessing() {
      return doProcessing;
    }
  }
}
