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
package io.knotx.assembler;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Describes {@link FragmentAssemblerHandler} configuration.
 */
@DataObject(generateConverter = true, publicConverter = false)
public class FragmentAssemblerOptions {

  /**
   * Default strategy of handling unprocessed knotx snippets = IGNORE
   */
  private static final FragmentAssembleStrategy DEFAULT_UNPROCESSED_STRATEGY = FragmentAssembleStrategy.IGNORE;

  private FragmentAssembleStrategy assembleStrategy;

  /**
   * Default constructor
   */
  public FragmentAssemblerOptions() {
    init();
  }

  public FragmentAssemblerOptions(FragmentAssembleStrategy assembleStrategy) {
    init();
    this.assembleStrategy = assembleStrategy;
  }

  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public FragmentAssemblerOptions(FragmentAssemblerOptions other) {
    this.assembleStrategy = other.assembleStrategy;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public FragmentAssemblerOptions(JsonObject json) {
    init();
    FragmentAssemblerOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    FragmentAssemblerOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    assembleStrategy = DEFAULT_UNPROCESSED_STRATEGY;
  }

  /**
   * @return fragment strategy name
   */
  public FragmentAssembleStrategy getAssembleStrategy() {
    return assembleStrategy;
  }

  /**
   * Set the strategy how to assembly markup with all fragments.. Allowed values are:
   * <ul>
   * <li>AS_IS - Keep all fragments as they are</li>
   * <li>IGNORE - Remove frangments that were not processed from the markup</li>
   * </ul>
   * If not set, a default value is <b>IGNORE</b>
   *
   * @param assembleStrategy a strategy enum
   * @return a reference to this, so the API can be used fluently
   */
  public FragmentAssemblerOptions setAssembleStrategy(
      FragmentAssembleStrategy assembleStrategy) {
    this.assembleStrategy = assembleStrategy;
    return this;
  }
}
