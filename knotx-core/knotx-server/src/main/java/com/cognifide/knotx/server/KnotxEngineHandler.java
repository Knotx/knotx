/*
 * Knot.x - Reactive microservice assembler - HTTP Server
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
package com.cognifide.knotx.server;

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.util.OptionalAction;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.web.RoutingContext;
import java.util.Map;

class KnotxEngineHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxEngineHandler.class);
  private EventBus eventBus;
  private String address;
  private Map<String, RoutingEntry> routing;

  private KnotxEngineHandler(EventBus eventBus, String address, Map<String, RoutingEntry> routing) {
    this.eventBus = eventBus;
    this.address = address;
    this.routing = routing;
  }

  static KnotxEngineHandler create(EventBus eventBus, String address,
      Map<String, RoutingEntry> routing) {
    return new KnotxEngineHandler(eventBus, address, routing);
  }

  @Override
  public void handle(RoutingContext context) {
    try {
      handleRoute(context, address, routing);
    } catch (Exception ex) {
      LOGGER.error("Something very unexpected happened", ex);
      context.fail(ex);
    }
  }

  private void handleRoute(final RoutingContext context, final String address,
      final Map<String, RoutingEntry> routing) {
    KnotContext knotContext = context.get("knotContext");

    eventBus.<KnotContext>sendObservable(address, knotContext)
        .map(Message::body)
        .doOnNext(ctx -> context.put("knotContext", ctx))
        .subscribe(
            ctx -> OptionalAction.of(ctx.transition())
                .ifPresent(on -> {
                  RoutingEntry entry = routing.get(on);
                  if (entry != null) {
                    handleRoute(context, entry.address(), entry.onTransition());
                  } else {
                    LOGGER.trace(
                        "No on criteria defined in routing for {} transition received from {}", on,
                        address);
                    // last knot can return default transition
                    context.put("knotContext", ctx);
                    context.next();
                  }
                })
                .ifNotPresent(() -> {
                  context.put("knotContext", ctx);
                  context.next();
                }),
            error -> {
              LOGGER.error("Error happened while communicating with {} engine", error, address);
              context.fail(error);
            }
        );
  }

}
