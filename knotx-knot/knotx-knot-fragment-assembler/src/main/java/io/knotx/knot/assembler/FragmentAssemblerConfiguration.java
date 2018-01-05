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
package io.knotx.knot.assembler;

import io.knotx.knot.assembler.impl.UnprocessedFragmentStrategy;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonObject;
import java.util.Optional;

@DataObject(generateConverter = true, publicConverter = false)
public class FragmentAssemblerConfiguration {

  private final static String DEFAULT_ADDRESS = "knotx.core.assembler";

  private final static String DEFAULT_UNPROCESSED_STRATEGY = "UNWRAP";

  private String address;

  private String unprocessedStrategy;

  /**
   * Default constructor
   */
  public FragmentAssemblerConfiguration() {
    init();
  }

  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public FragmentAssemblerConfiguration(FragmentAssemblerConfiguration other) {
    this.address = other.address;
    this.unprocessedStrategy = other.unprocessedStrategy;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public FragmentAssemblerConfiguration(JsonObject json) {
    init();
    FragmentAssemblerConfigurationConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    FragmentAssemblerConfigurationConverter.toJson(this, json);
    return json;
  }

  private void init() {
    address = DEFAULT_ADDRESS;
    unprocessedStrategy = DEFAULT_UNPROCESSED_STRATEGY;
  }

  /**
   * @return EB address of the verticle
   */
  public String getAddress() {
    return address;
  }

  /**
   * Set the EB address of the verticle
   *
   * @param address EB address
   * @return a reference to this, so the API can be used fluently
   */
  public FragmentAssemblerConfiguration setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * @return Unprocessed snippets strategy name
   */
  public String getUnprocessedStrategy() {
    return unprocessedStrategy;
  }

  /**
   * Set the strategy how to assembly markup with snippets that were not processed by any Knot.
   * Allowed values are:
   * <ul>
   * <li>AS_IS - Keep the whole unprocessed snippet as is</li>
   * <li>UNWRAP - Remove the wrapping script tag from the snippet</li>
   * <li>IGNORE - Remove snippet from the markup</li>
   * </ul>
   * If not set, a default value is <b>UNWRAP</b>
   *
   * @param unprocessedStrategy a strategy name
   * @return a reference to this, so the API can be used fluently
   */
  public FragmentAssemblerConfiguration setUnprocessedStrategy(String unprocessedStrategy) {
    this.unprocessedStrategy = unprocessedStrategy;
    return this;
  }

  @GenIgnore
  /**
   * Gets the {@link UnprocessedFragmentStrategy} enum representing strategy
   */
  public UnprocessedFragmentStrategy unprocessedFragmentStrategy() {
    return Optional.ofNullable(unprocessedStrategy)
        .map(String::toUpperCase)
        .map(UnprocessedFragmentStrategy::valueOf)
        .orElse(UnprocessedFragmentStrategy.UNWRAP);
  }
}
