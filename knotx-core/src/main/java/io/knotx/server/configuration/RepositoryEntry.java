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

  private String address;

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
    this.address = other.address;
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
  }

  /**
   * @return repository connector event bus address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Event bus address of the Repository Connector modules, that should deliver content for the
   * requested path matching the regexp in path
   *
   * @param address a string
   * @return a reference to this, so the API can be used fluently
   */
  public RepositoryEntry setAddress(String address) {
    this.address = address;
    return this;
  }

}
