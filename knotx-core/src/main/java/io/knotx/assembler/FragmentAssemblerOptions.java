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

import io.knotx.options.SnippetOptions;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Describes FragmentAssembler Knot configuration
 */
@DataObject(generateConverter = true, publicConverter = false)
public class FragmentAssemblerOptions {

  /**
   * Default address of the verticle on the event bus = knotx.core.assembler
   */
  private static final String DEFAULT_ADDRESS = "knotx.core.assembler";

  /**
   * Default strategy of handling unprocessed knotx snippets = UNWRAP
   */
  private static final UnprocessedFragmentStrategy DEFAULT_UNPROCESSED_STRATEGY = UnprocessedFragmentStrategy.UNWRAP;

  private String address;
  private UnprocessedFragmentStrategy unprocessedStrategy;
  private SnippetOptions snippetOptions;

  /**
   * Default constructor
   */
  public FragmentAssemblerOptions() {
    init();
  }

  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public FragmentAssemblerOptions(FragmentAssemblerOptions other) {
    this.address = other.address;
    this.snippetOptions = other.snippetOptions;
    this.unprocessedStrategy = other.unprocessedStrategy;
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
    address = DEFAULT_ADDRESS;
    unprocessedStrategy = DEFAULT_UNPROCESSED_STRATEGY;
    snippetOptions = new SnippetOptions();
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
  public FragmentAssemblerOptions setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * @return Unprocessed snippets strategy name
   */
  public UnprocessedFragmentStrategy getUnprocessedStrategy() {
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
   * @param unprocessedStrategy a strategy enum
   * @return a reference to this, so the API can be used fluently
   */
  public FragmentAssemblerOptions setUnprocessedStrategy(
      UnprocessedFragmentStrategy unprocessedStrategy) {
    this.unprocessedStrategy = unprocessedStrategy;
    return this;
  }

  /**
   * @return a snippet configuration
   */
  public SnippetOptions getSnippetOptions() {
    return snippetOptions;
  }

  /**
   * Sets snippet options (e.g. with tag and data prefix names).
   * @param snippetOptions options
   * @return a reference to this, so the API can be used fluently
   */
  public FragmentAssemblerOptions setSnippetOptions(SnippetOptions snippetOptions) {
    this.snippetOptions = snippetOptions;
    return this;
  }

}
