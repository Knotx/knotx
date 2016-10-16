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


import com.cognifide.knotx.mocks.service.MockServiceHandler;

import java.io.IOException;
import java.net.URISyntaxException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import rx.functions.Action2;

public class MockServiceVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockServiceVerticle.class);

  private HttpServer httpServer;

  private static final Action2<RoutingContext, String> BOUNCER = (context, mockData) -> {
    JsonObject responseBody = new JsonObject(mockData);
    MultiMap formParams = context.request().params();
    formParams.names().forEach(name -> responseBody.put(name, formParams.get(name)));
    context.response().setStatusCode(200).end(responseBody.encodePrettily());
  };

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  @Override
  public void start() throws IOException, URISyntaxException {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    httpServer = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.route().method(HttpMethod.POST).handler(createPostHandler());
    router.route().method(HttpMethod.GET).handler(createGetHandler());

    router.route().failureHandler(ErrorHandler.create(true));
    httpServer.requestHandler(router::accept).listen(config().getInteger("http.port"));
  }

  @Override
  public void stop() throws Exception {
    httpServer.close();
  }

  private MockServiceHandler createGetHandler() {
    return new MockServiceHandler(config().getString("mock.data.root"), vertx.fileSystem());
  }

  private MockServiceHandler createPostHandler() {
    MockServiceHandler mockServiceHandler = new MockServiceHandler(config().getString("mock.data.root"), vertx.fileSystem());
    return config().getBoolean("bouncing", false)
        ? mockServiceHandler.withBodyProcessor(BOUNCER)
        : mockServiceHandler;
  }

}
