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

@DataObject(generateConverter = true)
public class ResponseProviderKnotOptions {

  public final static String DEFAULT_ADDRESS = "knotx.gateway.responseprovider";

  private String address;

  /**
   * Default constructor
   */
  public ResponseProviderKnotOptions() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public ResponseProviderKnotOptions(ResponseProviderKnotOptions other) {
    this.address = other.address;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public ResponseProviderKnotOptions(JsonObject json) {
    init();
    ResponseProviderKnotOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ResponseProviderKnotOptionsConverter.toJson(this, json);
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
   * The {@code address} on event bus that the service adapter is listening for requests from {@link
   * ServiceKnotVerticle}
   *
   * @param address an event bus address
   * @return a reference to this, so the API can be used fluently
   */
  public ResponseProviderKnotOptions setAddress(String address) {
    this.address = address;
    return this;
  }
}
