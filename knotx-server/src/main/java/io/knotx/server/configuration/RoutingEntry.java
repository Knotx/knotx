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

import com.google.common.collect.Maps;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes a routing entry of Knot.x Server
 */
@DataObject(generateConverter = true, publicConverter = false)
public class RoutingEntry {

  public final static boolean DEFAULT_CSRF_ENABLED = false;

  private String path;
  private boolean csrf;
  private String address;
  private Map<String, RoutingEntry> onTransition;

  /**
   * Default constructor
   */
  public RoutingEntry() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public RoutingEntry(RoutingEntry other) {
    this.path = other.path;
    this.csrf = other.csrf;
    this.address = other.address;
    this.onTransition = Maps.newHashMap(other.onTransition);
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public RoutingEntry(JsonObject json) {
    init();
    RoutingEntryConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    RoutingEntryConverter.toJson(this, json);
    return json;
  }

  private void init() {
    csrf = DEFAULT_CSRF_ENABLED;
    onTransition = new HashMap<>();
  }

  /**
   * @return Regular expression of HTTP Request path
   */
  public String getPath() {
    return path;
  }

  /**
   * Sets the Regular expression of HTTP Request path
   *
   * @param path a string with Regexp
   * @return a reference to this, so the API can be used fluently
   */
  public RoutingEntry setPath(String path) {
    this.path = path;
    return this;
  }

  /**
   * @return true if CSRF is enabled for this entry
   */
  public boolean isCsrf() {
    return csrf;
  }

  /**
   * Enables/Disabled CSRF support for a given routing entry
   *
   * @param csrfEnabled true if enabled
   * @return a reference to this, so the API can be used fluently
   */
  public RoutingEntry setCsrf(boolean csrfEnabled) {
    this.csrf = csrfEnabled;
    return this;
  }

  /**
   * @return event bus address of the Knot that should process the request for a given path
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the event bus address of the Knot that should process the request for a given path
   *
   * @param address a string
   * @return a reference to this, so the API can be used fluently
   */
  public RoutingEntry setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   *
   * @return a transition mapping
   */
  public Map<String, RoutingEntry> getOnTransition() {
    return onTransition;
  }

  /**
   * Describes routing to addresses of other Knots based on the transition trigger returned from current Knot.
   * {@code
   *  "onTransition": {
   *    "go-a": {},
   *    "go-b": {}
   *   }
   * }
   *
   * @param onTransition a map of String-RoutingEntry
   * @return a reference to this, so the API can be used fluently
   */
  public RoutingEntry setOnTransition(
      Map<String, RoutingEntry> onTransition) {
    this.onTransition = onTransition;
    return this;
  }
}
