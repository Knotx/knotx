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

import io.vertx.codegen.annotations.DataObject;
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
    repositories = new ArrayList<>();
    splitter = DEFAULT_SPLITTER_ADDRESS;
    assembler = DEFAULT_ASSEMBLER_ADDRESS;
    responseProvider = DEFAULT_RESPONSE_PROVIDER_ADDRESS;
    routing = new HashMap<>();
  }

  public Optional<RepositoryEntry> repositoryForPath(final String path) {
    return repositories.stream()
        .filter(mapping -> path.matches(mapping.getPath()))
        .findFirst();
  }

  /**
   * @return list of RepositoryEntry items
   */
  public List<RepositoryEntry> getRepositories() {
    return repositories;
  }

  /**
   * Sets the list of RepositoryEntry items
   *
   * @param repositoryAddressMapping list of RepositoryEntry
   * @return a reference to this, so the API can be used fluently
   */
  public KnotxFlowSettings setRepositories(
      List<RepositoryEntry> repositoryAddressMapping) {
    this.repositories = repositoryAddressMapping;
    return this;
  }

  /**
   * @return Event bus address of the splitter verticle
   */
  public String getSplitter() {
    return splitter;
  }

  /**
   * Sets the event bus address of the splitter verticle. Default is knotx.core.splitter
   *
   * @param splitter event bus address
   * @return a reference to this, so the API can be used fluently
   */
  public KnotxFlowSettings setSplitter(String splitter) {
    this.splitter = splitter;
    return this;
  }

  /**
   * @return Event bus address of the assembler verticle
   */
  public String getAssembler() {
    return assembler;
  }

  /**
   * Sets the event bus address of the assembler verticle. Default is knotx.core.assembler
   *
   * @param assembler event bus address
   * @return a reference to this, so the API can be used fluently
   */
  public KnotxFlowSettings setAssembler(String assembler) {
    this.assembler = assembler;
    return this;
  }

  /**
   * @return Event bus address of the response provider verticle
   */
  public String getResponseProvider() {
    return responseProvider;
  }

  /**
   * Sets the event bus address of the Custom Flow' response provider verticle. Default is 'knotx.gateway.responseprovider'
   *
   * @param responseProvider event bus address
   * @return a reference to this, so the API can be used fluently
   */
  public KnotxFlowSettings setResponseProvider(String responseProvider) {
    this.responseProvider = responseProvider;
    return this;
  }

  /**
   * @return routing configuration
   */
  public Map<String, MethodRoutingEntries> getRouting() {
    return routing;
  }

  /**
   * Set of HTTP method based routing entries, describing communication between Knots
   * <pre>routing": {"GET": {}, "POST": {}}</pre>
   *
   * @param engineRouting map of HTTP Method to MethodRoutingEntries items
   * @return a reference to this, so the API can be used fluently
   */
  public KnotxFlowSettings setRouting(Map<String, MethodRoutingEntries> engineRouting) {
    this.routing = engineRouting;
    return this;
  }
}
