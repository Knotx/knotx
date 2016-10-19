/*
 * Knot.x - Mocked services for sample app
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.knotx.mocks;


import com.cognifide.knotx.mocks.knot.MockKnotHandler;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;

public class MockKnotVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockKnotVerticle.class);

  @Override
  public void start() {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    final JsonArray handlers = config().getJsonArray("handlers");
    handlers.forEach(config -> {
      final JsonObject handlerConfig = (JsonObject) config;
      final String address = handlerConfig.getString("address");
      final JsonArray handledMocks = handlerConfig.getJsonArray("mocks");
      LOGGER.info("  listening @ `{}`", address);
      vertx.eventBus().consumer(address, createHandler(handledMocks));
    });
  }

  private MockKnotHandler createHandler(JsonArray handledMocks) {
    return new MockKnotHandler(handledMocks, vertx.fileSystem());
  }

}
