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


import io.knotx.mocks.handler.MockServiceHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import java.util.function.BiConsumer;

public class MockServiceVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockServiceVerticle.class);
  private static final BiConsumer<RoutingContext, String> BOUNCER = (context, mockData) -> {
    JsonObject responseBody = new JsonObject(mockData);
    context.response().putHeader("Set-Cookie", "mockCookie=" + context.request().path());
    context.response().setStatusCode(200).end(responseBody.encodePrettily());
  };
  private HttpServer httpServer;

  @Override
  public void start(Future<Void> fut) {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    httpServer = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.route().method(HttpMethod.POST).handler(createPostHandler());
    router.route().method(HttpMethod.GET).handler(createGetHandler());

    router.route().failureHandler(ErrorHandler.create(true));
    httpServer.requestHandler(router::accept).listen(
        config().getInteger("httpPort"),
        result -> {
          if (result.succeeded()) {
            LOGGER.info("Mock Service server started. Listening on port {}",
                config().getInteger("httpPort"));
            fut.complete();
          } else {
            LOGGER.error("Unable to start Mock Service server.", result.cause());
            fut.fail(result.cause());
          }
        });
  }

  @Override
  public void stop() {
    httpServer.close();
  }

  private MockServiceHandler createGetHandler() {
    return new MockServiceHandler(config().getString("mockDataRoot"), vertx,
        config().getLong("delayAllMs", 0L),
        config().getJsonObject("delay", new JsonObject()));
  }

  private MockServiceHandler createPostHandler() {
    MockServiceHandler mockServiceHandler = new MockServiceHandler(
        config().getString("mockDataRoot"), vertx, config().getLong("delayAllMs", 0L),
        config().getJsonObject("delay", new JsonObject()));
    return config().getBoolean("bouncing", false)
        ? mockServiceHandler.withBodyProcessor(BOUNCER)
        : mockServiceHandler;
  }

}
