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
package io.knotx.knot.templating;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Describes Handlebars Knot configuration
 */
@DataObject(generateConverter = true, publicConverter = false)
public class HandlebarsKnotOptions {

  /**
   * Default key hash algorithm = MD5
   */
  public final static String DEFAULT_CACHE_KEY_ALGORITHM = "MD5";

  /**
   * Default EB address of the verticle
   */
  public final static String DEFAULT_ADDRESS = "knotx.knot.handlebars";

  private String address;
  private String cacheKeyAlgorithm;
  private Long cacheSize;

  /**
   * Default constructor
   */
  public HandlebarsKnotOptions() {
    init();
  }

  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public HandlebarsKnotOptions(HandlebarsKnotOptions other) {
    this.address = other.address;
    this.cacheKeyAlgorithm = other.cacheKeyAlgorithm;
    this.cacheSize = other.cacheSize;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public HandlebarsKnotOptions(JsonObject json) {
    init();
    HandlebarsKnotOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    HandlebarsKnotOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    address = DEFAULT_ADDRESS;
    cacheKeyAlgorithm = DEFAULT_CACHE_KEY_ALGORITHM;
  }

  /**
   * @return size of the cache
   */
  public Long getCacheSize() {
    return cacheSize;
  }

  /**
   * Set the size of the cache. After reaching the max size, new elements will replace the oldest one.
   *
   * @param cacheSize size of the cache
   * @return a reference to this, so the API can be used fluently
   */
  public HandlebarsKnotOptions setCacheSize(Long cacheSize) {
    this.cacheSize = cacheSize;
    return this;
  }

  /**
   * @return name of the algorithm used to generate hash from the handlebars snippet
   */
  public String getCacheKeyAlgorithm() {
    return cacheKeyAlgorithm;
  }

  /**
   * Set the algorithm used to build a hash from the handlebars snippet.
   * The hash is to be used as a cache key.
   *
   * The name should be a standard Java Security name (such as "SHA", "MD5", and so on).
   *
   * @param cacheKeyAlgorithm algorithm name
   * @return a reference to this, so the API can be used fluently
   */
  public HandlebarsKnotOptions setCacheKeyAlgorithm(String cacheKeyAlgorithm) {
    this.cacheKeyAlgorithm = cacheKeyAlgorithm;
    return this;
  }

  /**
   * @return EB address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the EB address of the verticle
   *
   * @param address EB address of the verticle
   * @return a reference to this, so the API can be used fluently
   */
  public HandlebarsKnotOptions setAddress(String address) {
    this.address = address;
    return this;
  }
}
