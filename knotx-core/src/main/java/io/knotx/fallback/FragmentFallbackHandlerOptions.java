/*
 * Copyright (C) 2018 Cognifide Limited
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
package io.knotx.fallback;

import io.knotx.options.FallbackMetadata;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

@DataObject(generateConverter = true)
public class FragmentFallbackHandlerOptions {

  private String defaultFallback;
  private List<FallbackMetadata> fallbacks;

  /**
   * Default constructor
   */
  FragmentFallbackHandlerOptions() {
    init();
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public FragmentFallbackHandlerOptions(JsonObject json) {
    FragmentFallbackHandlerOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    FragmentFallbackHandlerOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    fallbacks = new ArrayList<>();
    fallbacks.add(new FallbackMetadata(FallbackConstants.DEFAULT_FALLBACK_ID,
        FallbackConstants.EMPTY_FALLBACK_VALUE));
    defaultFallback = FallbackConstants.DEFAULT_FALLBACK_ID;
  }

  public String getDefaultFallback() {
    return defaultFallback;
  }

  public FragmentFallbackHandlerOptions setDefaultFallback(String defaultFallback) {
    this.defaultFallback = defaultFallback;
    return this;
  }

  public List<FallbackMetadata> getFallbacks() {
    return fallbacks;
  }

  public FragmentFallbackHandlerOptions setFallbacks(List<FallbackMetadata> fallbacks) {
    this.fallbacks = fallbacks;
    return this;
  }

  public FragmentFallbackHandlerOptions addFallback(FallbackMetadata fallback) {
    this.fallbacks.add(fallback);
    return this;
  }

}
