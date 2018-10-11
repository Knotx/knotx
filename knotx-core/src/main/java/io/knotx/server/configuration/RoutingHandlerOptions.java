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
package io.knotx.server.configuration;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;

@DataObject(generateConverter = true, publicConverter = false)
public class RoutingHandlerOptions {

  private String name;
  private JsonObject config;

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public RoutingHandlerOptions(JsonObject json) {
    init();
    RoutingHandlerOptionsConverter.fromJson(json, this);
    if (StringUtils.isBlank(name)) {
      throw new IllegalStateException("Handler name in routing configuration can not be null!");
    }
  }

  private void init() {
    this.config = new JsonObject();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public JsonObject getConfig() {
    return config == null ? new JsonObject() : config;
  }

  public void setConfig(JsonObject config) {
    this.config = config;
  }

  @Override
  public String toString() {
    return "RoutingHandlerOptions{" +
        "name='" + name + '\'' +
        ", config=" + config +
        '}';
  }
}
