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
package io.knotx.mocks;


import io.knotx.mocks.handler.MockRemoteRepositoryHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.ErrorHandler;
import java.io.IOException;
import java.net.URISyntaxException;

public class MockRemoteRepositoryVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockRemoteRepositoryVerticle.class);

  private HttpServer httpServer;

  @Override
  public void start(Future<Void> fut) throws IOException, URISyntaxException {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    httpServer = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.route().method(HttpMethod.GET).handler(createRepositoryHandler());
    router.route().failureHandler(ErrorHandler.create(true));

    httpServer.requestHandler(router::accept).listen(
        config().getInteger("httpPort"),
        result -> {
          if (result.succeeded()) {
            LOGGER.info("Mock Remote Repository server started. Listening on port {}",
                config().getInteger("httpPort"));
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

  private Handler<RoutingContext> createRepositoryHandler() {
    return new MockRemoteRepositoryHandler(vertx, config().getString("mockDataRoot"),
        config().getLong("delayAllMs", 0L),
        config().getJsonObject("delay", new JsonObject()));
  }
}

