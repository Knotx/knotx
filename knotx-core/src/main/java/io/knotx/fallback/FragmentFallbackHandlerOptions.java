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

import com.google.common.collect.Lists;
import io.knotx.options.FallbackMetadata;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@DataObject(generateConverter = true)
public class FragmentFallbackHandlerOptions {

  private String defaultFallback;
  private List<FallbackMetadata> fallbacks;

  /**
   * Default constructor
   */
  FragmentFallbackHandlerOptions() {
    configureDefaultFallback();
  }


  /**
   * Copy constructor.
   * @param other the instance to copy
   */
  FragmentFallbackHandlerOptions(FragmentFallbackHandlerOptions other) {
    this.defaultFallback = other.defaultFallback;
    this.fallbacks = new ArrayList<>(other.fallbacks);
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public FragmentFallbackHandlerOptions(JsonObject json) {
    FragmentFallbackHandlerOptionsConverter.fromJson(json, this);
    configureDefaultFallback();
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

  private void configureDefaultFallback() {
    if (CollectionUtils.isEmpty(fallbacks)) {
      fallbacks = Lists.newArrayList(new FallbackMetadata("BLANK", StringUtils.EMPTY));
    }
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

}
