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
package io.knotx.options;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * Describes SnippetOptions Knot configuration
 */
@DataObject(generateConverter = true, publicConverter = false)
public class SnippetOptions {

  private static final String DEFAULT_TAG_NAME = "script";
  private static final String DEFAULT_PARAMS_PREFIX = "data-knotx-";

  private String tagName;
  private String paramsPrefix;

  /**
   * Default constructor
   */
  public SnippetOptions() {
    init();
  }

  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public SnippetOptions(SnippetOptions other) {
    this.tagName = other.tagName;
    this.paramsPrefix = other.paramsPrefix;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public SnippetOptions(JsonObject json) {
    init();
    SnippetOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    SnippetOptionsConverter.toJson(this, json);
    return json;
  }


  private void init() {
    tagName = DEFAULT_TAG_NAME;
    paramsPrefix = DEFAULT_PARAMS_PREFIX;
  }

  /**
   * @return a snippet tag name.
   */
  public String getTagName() {
    return tagName;
  }

  /**
   * Sets a Knot.x snippet HTML tag name. Default is 'script'
   *
   * @param tagName tag name
   * @return a reference to this, so the API can be used fluently
   */
  public SnippetOptions setTagName(String tagName) {
    this.tagName = tagName;
    return this;
  }

  /**
   * @return a snippet params prefix
   */
  public String getParamsPrefix() {
    return paramsPrefix;
  }

  /**
   * Sets Knot.x snippet parameters prefix. Default is 'data-knotx-'
   *
   * @param paramsPrefix prefix
   * @return a reference to this, so the API can be used fluently
   */
  public SnippetOptions setParamsPrefix(String paramsPrefix) {
    this.paramsPrefix = paramsPrefix == null ? StringUtils.EMPTY : paramsPrefix;
    return this;
  }
}
