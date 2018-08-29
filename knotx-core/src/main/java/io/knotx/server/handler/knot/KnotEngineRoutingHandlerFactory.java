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
package io.knotx.server.handler.knot;

import io.knotx.dataobjects.KnotContext;
import io.knotx.reactivex.proxy.KnotProxy;
import io.knotx.server.configuration.RoutingEntry;
import io.knotx.server.handler.api.RoutingHandlerFactory;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class KnotEngineRoutingHandlerFactory implements RoutingHandlerFactory {

  @Override
  public String getName() {
    return "knotEngineHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    return new KnotxEngineHandler(vertx, config);
  }

  private class KnotxEngineHandler implements Handler<RoutingContext> {

    private final Logger LOGGER = LoggerFactory.getLogger(KnotxEngineHandler.class);

    private Vertx vertx;
    private DeliveryOptions deliveryOptions;
    private String address;
    private Map<String, RoutingEntry> routing;
    private Map<String, KnotProxy> proxies;

    private KnotxEngineHandler(Vertx vertx, JsonObject configuration) {
      this.vertx = vertx;
      this.address = configuration.getJsonObject("routing").getString("proxyAddress");
      this.deliveryOptions =
          configuration.getJsonObject("deliveryOptions") != null ? new DeliveryOptions(
              configuration.getJsonObject("deliveryOptions")) : new DeliveryOptions();
      this.routing = new RoutingEntry(configuration.getJsonObject("routing")).getOnTransition();
      this.proxies = new HashMap<>();
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
      KnotContext knotContext = context.get(KnotContext.KEY);

      proxies.computeIfAbsent(address,
          adr -> KnotProxy.createProxyWithOptions(vertx, adr, new DeliveryOptions(deliveryOptions)))
          .rxProcess(knotContext)
          .doOnSuccess(ctx -> context.put(KnotContext.KEY, ctx))
          .subscribe(ctx -> {
                if (StringUtils.isNotBlank(ctx.getTransition())) {
                  doTransition(context, ctx, routing);
                } else {
                  doEndProcessing(context, ctx);
                }
              },
              error -> {
                LOGGER.error("Error happened while communicating with {} engine", error, address);
                context.fail(error);
              }
          );
    }

    private void doTransition(RoutingContext context, KnotContext ctx,
        final Map<String, RoutingEntry> routing) {
      RoutingEntry entry = routing.get(ctx.getTransition());
      if (entry != null) {
        handleRoute(context, entry.getAddress(), entry.getOnTransition());
      } else {
        LOGGER.debug(
            "Received transition '{}' from '{}'. No further routing available for the transition. Go to the response generation.",
            ctx.getTransition(), address);
        // last knot can return default transition
        context.put(KnotContext.KEY, ctx);
        context.next();
      }
    }

    private void doEndProcessing(RoutingContext context, KnotContext ctx) {
      LOGGER.debug("Request processing finished by {} Knot. Go to the response generation",
          address);
      context.put(KnotContext.KEY, ctx);
      context.next();
    }


  }
}
