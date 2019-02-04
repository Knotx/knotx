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
package io.knotx.knotengine.core;

import io.knotx.knotengine.api.SnippetFragmentsContext;
import io.knotx.server.api.handler.RoutingHandlerFactory;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;

public class KnotEngineRoutingHandlerFactory implements RoutingHandlerFactory {

  @Override
  public String getName() {
    return "knotEngineHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    return new KnotEngineHandler(vertx, config);
  }

  static class KnotEngineHandler implements Handler<RoutingContext> {

    private final Logger LOGGER = LoggerFactory.getLogger(KnotEngineHandler.class);

    private KnotEngine engine;
    private KnotEngineOptions options;

    KnotEngineHandler(Vertx vertx, JsonObject configuration) {
      this(vertx, new KnotEngineOptions(configuration));
    }

    KnotEngineHandler(Vertx vertx, KnotEngineOptions options) {
      this.options = options;
      this.engine = new KnotEngine(vertx, getDeliveryOptions());
    }

    @Override
    public void handle(RoutingContext context) {
      try {
        RoutingEntry firstRouteEntry = options.getRouting();
        engine.handleRoute(context, firstRouteEntry,
            new OnCompleteObserver<SnippetFragmentsContext>() {
              @Override
              public void onSuccess(SnippetFragmentsContext snippetFragmentsContext) {
                context.next();
              }

              @Override
              public void onError(Throwable e) {
                context.fail(e);
              }
            });
      } catch (Exception ex) {
        LOGGER.error("Something very unexpected happened", ex);
        context.fail(ex);
      }
    }

    private DeliveryOptions getDeliveryOptions() {
      DeliveryOptions result = options.getDeliveryOptions();
      if (result == null) {
        result = new DeliveryOptions();
      }
      return result;
    }

  }
}
