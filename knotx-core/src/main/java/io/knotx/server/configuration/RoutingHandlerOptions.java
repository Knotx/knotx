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

import io.knotx.server.handler.api.RoutingHandlerFactory;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.apache.commons.lang3.StringUtils;

/**
 * Handler definition that contains {@link RoutingHandlerFactory} name and JSON configuration.
 * During {@link io.knotx.server.KnotxServerVerticle} deployment all implementations of {@link
 * RoutingHandlerFactory} are loaded from the classpath and based on {@link
 * RoutingHandlerFactory#getName()} are initiated.
 */
@DataObject(generateConverter = true, publicConverter = false)
public class RoutingHandlerOptions {

  private String name;
  private JsonObject config;

  /**
   * Create settings from JSON
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

  /**
   * @return {@link io.knotx.server.handler.api.RoutingHandlerFactory} name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets {@link io.knotx.server.handler.api.RoutingHandlerFactory} name
   *
   * @param name handler factory name
   * @return reference to this, so the API can be used fluently
   */
  public RoutingHandlerOptions setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return JSON configuration used during {@link io.knotx.server.handler.api.RoutingHandlerFactory#create(Vertx,
   * JsonObject)} initialization
   */
  public JsonObject getConfig() {
    return config == null ? new JsonObject() : config;
  }

  /**
   * Sets {@link io.vertx.core.Handler} configuration.
   *
   * @param config handler JSON configuration
   * @return reference to this, so the API can be used fluently
   */
  public RoutingHandlerOptions setConfig(JsonObject config) {
    this.config = config;
    return this;
  }

  @Override
  public String toString() {
    return "RoutingHandlerOptions{" +
        "name='" + name + '\'' +
        ", config=" + config +
        '}';
  }
}
