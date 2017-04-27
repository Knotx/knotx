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
package io.knotx.server.configuration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class KnotxFlowConfiguration {

  private Map<String, RepositoryEntry> repositoryAddressMapping;

  private String splitterAddress;

  private String assemblerAddress;

  private String responseProviderAddress;

  private EnumMap<HttpMethod, List<RoutingEntry>> engineRouting;

  public KnotxFlowConfiguration(JsonObject config) {
    if(config != null) {
      repositoryAddressMapping = Maps.newHashMap();
      JsonArray repositories = config.getJsonArray("repositories");
      if (repositories != null) {
        repositories.stream()
            .map(item -> (JsonObject) item)
            .forEach(object -> repositoryAddressMapping.put(object.getString("path"),
                new RepositoryEntry(object.getString("address"),
                    object.getBoolean("doProcessing", true))));
      }

      JsonObject splitter = config.getJsonObject("splitter");
      if (splitter != null) {
        splitterAddress = splitter.getString("address");
      }


      engineRouting = Maps.newEnumMap(HttpMethod.class);
      config.getJsonObject("routing").stream()
          .forEach(this::parseMethodRouting);

      JsonObject assembler = config.getJsonObject("assembler");
      if (assembler != null) {
        assemblerAddress = assembler.getString("address");
      }

      JsonObject responseProvider = config.getJsonObject("responseProvider");
      if (responseProvider != null) {
        responseProviderAddress = responseProvider.getString("address");
      }
    }
  }


  private void parseMethodRouting(Map.Entry<String, Object> entry) {
    final List<RoutingEntry> methodCriteria = getMethodCriterias(
        HttpMethod.valueOf(entry.getKey()));

    ((JsonArray) entry.getValue()).stream()
        .map(item -> (JsonObject) item)
        .map(this::parseRoutingCriteria)
        .forEach(methodCriteria::add);
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
  private RoutingEntry parseRoutingCriteria(JsonObject object) {
    return new RoutingEntry(object.getString("path"), object.getString("address"),
        parseOnTransition(object.getJsonObject("onTransition")));
  }

  private Map<String, RoutingEntry> parseOnTransition(JsonObject onTransition) {
    Map<String, RoutingEntry> transitions = Maps.newHashMap();

    if (onTransition != null) {
      onTransition.stream().forEach(
          entry -> transitions
              .put(entry.getKey(), parseRoutingCriteria((JsonObject) entry.getValue()))
      );
    }
    return transitions;
  }

  public Optional<RepositoryEntry> repositoryForPath(final String path) {
    return repositoryAddressMapping.entrySet().stream()
        .filter(mapping -> path.matches(mapping.getKey()))
        .findFirst().map(Map.Entry::getValue);
  }

  public String splitterAddress() {
    return splitterAddress;
  }

  public String assemblerAddress() {
    return assemblerAddress;
  }

  public Map<HttpMethod, List<RoutingEntry>> getEngineRouting() {
    return engineRouting;
  }

  public String responseProviderAddress() {
    return responseProviderAddress;
  }
}
