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
package io.knotx.configuration;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Describes a custom header
 */
@DataObject(generateConverter = true)
public class CustomHttpHeader {

  /**
   * The default request header = Service-User-Agent
   */
  public final static String DEFAULT_HEADER_NAME = "Server-User-Agent";

  /**
   * The default request header value = Knot.x
   */
  public final static String DEFAULT_HEADER_VALUE = "Knot.x";

  private String name;
  private String value;

  /**
   * Default Constructor
   */
  public CustomHttpHeader() {
    init();
  }

  /**
   * Default copy constructor
   *
   * @param other the customHeader configuration to copy
   */
  public CustomHttpHeader(CustomHttpHeader other) {
    this.name = other.name;
    this.value = other.value;
  }

  /**
   * Creates from JSON
   *
   * @param json the JSON
   */
  public CustomHttpHeader(JsonObject json) {
    init();
    CustomHttpHeaderConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    CustomHttpHeaderConverter.toJson(this, json);
    return json;
  }

  private void init() {
    name = DEFAULT_HEADER_NAME;
    value = DEFAULT_HEADER_VALUE;
  }


  /**
   * Set the name of the request header. If not set a {@code Server-User-Agent} is used.
   */
  public CustomHttpHeader setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Set the value of the request header. If not set a @{code Knot.x} is used.
   */
  public CustomHttpHeader setValue(String value) {
    this.value = value;
    return this;
  }


  /**
   * @return a request header name
   */
  public String getName() {
    return name;
  }

  /**
   * @return a request header value
   */
  public String getValue() {
    return value;
  }
}
