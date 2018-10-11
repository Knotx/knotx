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
package io.knotx.server.handler.csrf;

import io.knotx.server.handler.api.RoutingHandlerFactory;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.CSRFHandler;

public class CSRFHandlerFactory implements RoutingHandlerFactory {

  private static final Logger LOG = LoggerFactory.getLogger(RoutingHandlerFactory.class);

  @Override
  public String getName() {
    return "csrfHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    CSRFOptions options = new CSRFOptions(config);
    return routingContext ->
        CSRFHandler.create(options.getSecret())
            .setNagHttps(true) //Generates warning message in log if https is not used
            .setCookieName(options.getCookieName())
            .setCookiePath(options.getCookiePath())
            .setHeaderName(options.getHeaderName())
            .setTimeout(options.getTimeout()).handle(routingContext);
  }
}
