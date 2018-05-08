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
package io.knotx.splitter;

import io.knotx.options.SnippetOptions;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Describes FragmentSplitter Knot configuration
 */
@DataObject(generateConverter = true, publicConverter = false)
public class FragmentSplitterOptions {

  /**
   * Default address of the verticle on the event bus
   */
  private static final String DEFAULT_ADDRESS = "knotx.core.splitter";

  private String address;
  private SnippetOptions snippetOptions;

  /**
   * Default constructor
   */
  public FragmentSplitterOptions() {
    init();
  }

  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public FragmentSplitterOptions(FragmentSplitterOptions other) {
    this.address = other.address;
    this.snippetOptions = other.snippetOptions;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public FragmentSplitterOptions(JsonObject json) {
    init();
    FragmentSplitterOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    FragmentSplitterOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    address = DEFAULT_ADDRESS;
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
  public FragmentSplitterOptions setAddress(String address) {
    this.address = address;
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
  public FragmentSplitterOptions setSnippetOptions(SnippetOptions snippetOptions) {
    this.snippetOptions = snippetOptions;
    return this;
  }
}
