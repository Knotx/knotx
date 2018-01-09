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
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@DataObject(generateConverter = true, publicConverter = false)
public class KnotxFlowSettings {

  private final static String DEFAULT_SPLITTER_ADDRESS = "knotx.core.splitter";
  private final static String DEFAULT_ASSEMBLER_ADDRESS = "knotx.core.assembler";
  private final static String DEFAULT_RESPONSE_PROVIDER_ADDRESS = "knotx.gateway.responseprovider";

  private final static List<RepositoryEntry> DEFAULT_REPOSITORIES = Lists.newArrayList(
      new RepositoryEntry().setPath("/content/local/.*")
          .setAddress("knotx.core.repository.filesystem"),
      new RepositoryEntry().setPath("/content/.*").setAddress("knotx.core.repository.http")
  );

  private final static Map<String, MethodRoutingEntries> DEFAULT_ROUTING = Maps.newHashMap();

  private final static Map<String, RoutingEntry> DEFAULT_ROUTING_TRANSITION = Maps.newHashMap();

  static {
    DEFAULT_ROUTING_TRANSITION.put("next", new RoutingEntry().setAddress("knotx.knot.handlebars"));

    DEFAULT_ROUTING
        .put(HttpMethod.GET.toString(), new MethodRoutingEntries().setItems(Lists.newArrayList(
            new RoutingEntry().setPath(".*").setAddress("knotx.knot.service").setCsrf(false)
                .setOnTransition(DEFAULT_ROUTING_TRANSITION)
        )));
  }

  private List<RepositoryEntry> repositories;
  private String splitter;
  private String assembler;
  private String responseProvider;
  private Map<String, MethodRoutingEntries> routing;

  /**
   * Default constructor
   */
  public KnotxFlowSettings() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public KnotxFlowSettings(KnotxFlowSettings other) {
    this.repositories = new ArrayList<>(other.repositories);
    this.splitter = other.splitter;
    this.assembler = other.assembler;
    this.responseProvider = other.responseProvider;
    this.routing = new HashMap<>(other.routing);
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public KnotxFlowSettings(JsonObject json) {
    init();
    KnotxFlowSettingsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    KnotxFlowSettingsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    repositories = DEFAULT_REPOSITORIES;
    splitter = DEFAULT_SPLITTER_ADDRESS;
    assembler = DEFAULT_ASSEMBLER_ADDRESS;
    responseProvider = DEFAULT_RESPONSE_PROVIDER_ADDRESS;
    routing = DEFAULT_ROUTING;
  }

  public Optional<RepositoryEntry> repositoryForPath(final String path) {
    return repositories.stream()
        .filter(mapping -> path.matches(mapping.getPath()))
        .findFirst();
  }

  /****/
  public List<RepositoryEntry> getRepositories() {
    return repositories;
  }

  public KnotxFlowSettings setRepositories(
      List<RepositoryEntry> repositoryAddressMapping) {
    this.repositories = repositoryAddressMapping;
    return this;
  }

  public String getSplitter() {
    return splitter;
  }

  public KnotxFlowSettings setSplitter(String splitter) {
    this.splitter = splitter;
    return this;
  }

  public String getAssembler() {
    return assembler;
  }

  public KnotxFlowSettings setAssembler(String assembler) {
    this.assembler = assembler;
    return this;
  }

  public String getResponseProvider() {
    return responseProvider;
  }

  public KnotxFlowSettings setResponseProvider(String responseProvider) {
    this.responseProvider = responseProvider;
    return this;
  }

  public Map<String, MethodRoutingEntries> getRouting() {
    return routing;
  }

  public KnotxFlowSettings setRouting(Map<String, MethodRoutingEntries> engineRouting) {
    this.routing = engineRouting;
    return this;
  }
}
