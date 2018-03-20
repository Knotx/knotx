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
import java.util.List;

/**
 * Describes a collection of server routing entries
 */
@DataObject(generateConverter = true, publicConverter = false)
public class MethodRoutingEntries {

  private List<RoutingEntry> items;

  /**
   * Default constructor
   */
  public MethodRoutingEntries() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public MethodRoutingEntries(MethodRoutingEntries other) {
    this.items = new ArrayList<>(other.items);
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public MethodRoutingEntries(JsonObject json) {
    init();
    MethodRoutingEntriesConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    MethodRoutingEntriesConverter.toJson(this, json);
    return json;
  }

  private void init() {
    items = new ArrayList<>();
  }

  /**
   *
   * @return list of routing entries
   */
  public List<RoutingEntry> getItems() {
    return items;
  }

  /**
   * Sets the list of routing entries
   * @param items list of RoutingEntry objects
   * @return a reference to this, so the API can be used fluently
   */
  public MethodRoutingEntries setItems(List<RoutingEntry> items) {
    this.items = items;
    return this;
  }
}
