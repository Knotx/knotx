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

@DataObject(generateConverter = true, publicConverter = false)
public class RepositoryEntry {

  public final static boolean DEFAULT_DO_PROCESSING = true;

  private String path;
  private String address;
  private boolean doProcessing;

  /**
   * Default constructor
   */
  public RepositoryEntry() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public RepositoryEntry(RepositoryEntry other) {
    this.path = other.path;
    this.address = other.address;
    this.doProcessing = other.doProcessing;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public RepositoryEntry(JsonObject json) {
    init();
    RepositoryEntryConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    RepositoryEntryConverter.toJson(this, json);
    return json;
  }

  private void init() {
    doProcessing = DEFAULT_DO_PROCESSING;
  }

  /**
   * @return Regular expression of the HTTP Request path
   */
  public String getPath() {
    return path;
  }

  /**
   * Sets the Regular expression of the HTTP Request path
   *
   * @param path a string
   * @return a reference to this, so the API can be used fluently
   */
  public RepositoryEntry setPath(String path) {
    this.path = path;
    return this;
  }

  /**
   * @return repository connector event bus address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Event bus address of the Repository Connector modules, that should deliver content for the requested path matching the regexp in path
   *
   * @param address a string
   * @return a reference to this, so the API can be used fluently
   */
  public RepositoryEntry setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * @return true - if response from a given repository should be processed by knots
   */
  public boolean isDoProcessing() {
    return doProcessing;
  }

  /**
   * Defines if the given repository path should be processed by the Knots or not. If not set, a processing is enabled by default.
   *
   * @param doProcessing false - if request path shouldn't be processed by knots
   * @return a reference to this, so the API can be used fluently
   */
  public RepositoryEntry setDoProcessing(boolean doProcessing) {
    this.doProcessing = doProcessing;
    return this;
  }
}
