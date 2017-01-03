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


import com.cognifide.knotx.mocks.adapter.MockRemoteRepositoryHandler;

import io.vertx.core.Future;
import java.io.IOException;
import java.net.URISyntaxException;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;

public class MockRemoteRepositoryVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockRemoteRepositoryVerticle.class);

  private MockRemoteRepositoryHandler mockRemoteRepositoryHandler;

  private HttpServer httpServer;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    mockRemoteRepositoryHandler = new MockRemoteRepositoryHandler(config().getString("mockDataRoot"));
  }

  @Override
  public void start(Future<Void> fut) throws IOException, URISyntaxException {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    httpServer = vertx.createHttpServer();
    httpServer.requestHandler(mockRemoteRepositoryHandler).listen(
        config().getInteger("httpPort"),
        result -> {
          if (result.succeeded()) {
            LOGGER.info("Mock Remote Repository server started. Listening on port {}", config().getInteger("httpPort"));
            fut.complete();
          } else {
            LOGGER.error("Unable to start Mock Remote Repository server.", result.cause());
            fut.fail(result.cause());
          }
        });
  }

  @Override
  public void stop() throws Exception {
    httpServer.close();
  }
}

