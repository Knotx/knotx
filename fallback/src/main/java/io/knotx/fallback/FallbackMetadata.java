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
package io.knotx.fallback;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true, publicConverter = false)
public class FallbackMetadata {
  private String id;
  private String markup;

  public FallbackMetadata(String id, String markup) {
    this.id = id;
    this.markup = markup;
  }

  public FallbackMetadata(FallbackMetadata other) {
    this.id = other.id;
    this.markup = other.markup;
  }

  public String getId() {
    return id;
  }

  public String getMarkup() {
    return markup;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setMarkup(String markup) {
    this.markup = markup;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public FallbackMetadata(JsonObject json) {
    FallbackMetadataConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    FallbackMetadataConverter.toJson(this, json);
    return json;
  }
}
