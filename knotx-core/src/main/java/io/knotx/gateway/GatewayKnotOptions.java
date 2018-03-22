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
package io.knotx.gateway;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Describes a configuration of Knot.x Gateway knot
 */
@DataObject(generateConverter = true)
public class GatewayKnotOptions {

  public final static String DEFAULT_ADDRESS = "knotx.gateway.gatewayknot";

  private String address;

  /**
   * Default constructor
   */
  public GatewayKnotOptions() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public GatewayKnotOptions(GatewayKnotOptions other) {
    this.address = other.address;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public GatewayKnotOptions(JsonObject json) {
    init();
    GatewayKnotOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    GatewayKnotOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    address = DEFAULT_ADDRESS;
  }

  /**
   * @return event bus address the service adapter is registered on
   */
  public String getAddress() {
    return address;
  }

  /**
   * The event bus {@code address} the knot is listening on.
   * Default is {@code knotx.gateway.gatewayknot}
   *
   * @param address an event bus address
   * @return a reference to this, so the API can be used fluently
   */
  public GatewayKnotOptions setAddress(String address) {
    this.address = address;
    return this;
  }
}
