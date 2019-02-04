/*
 * Copyright (C) 2019 Cognifide Limited
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
import io.knotx.reactivex.knotengine.api.KnotProxy;
import io.knotx.server.api.context.FragmentsContext;
import io.reactivex.SingleObserver;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

class KnotEngine {

  private final Logger LOGGER = LoggerFactory.getLogger(KnotEngine.class);

  private final Vertx vertx;
  private DeliveryOptions deliveryOptions;
  private final Map<String, KnotProxy> proxies;


  KnotEngine(Vertx vertx, DeliveryOptions deliveryOptions) {
    this.vertx = vertx;
    this.deliveryOptions = deliveryOptions;
    this.proxies = new HashMap<>();
  }

  void handleRoute(final RoutingContext context, RoutingEntry route,
      OnCompleteObserver<SnippetFragmentsContext> onComplete) {
    handleRoute(context, route.getAddress(), route.getOnTransition(), onComplete);
  }

  void handleRoute(final RoutingContext context, final String address,
      final Map<String, RoutingEntry> routing, OnCompleteObserver<SnippetFragmentsContext> onComplete) {
    FragmentsContext fragmentsContext = context.get(FragmentsContext.KEY);
    SnippetFragmentsContext snippetFragmentsContext = new SnippetFragmentsContext(
        fragmentsContext);

    proxies.computeIfAbsent(address,
        adr -> KnotProxy.createProxyWithOptions(vertx, adr, deliveryOptions))
        .rxProcess(snippetFragmentsContext)
        .doOnSuccess(ctx -> context.put(FragmentsContext.KEY, ctx.getDelegate()))
        .subscribe(ctx -> {
              if (StringUtils.isNotBlank(ctx.getTransition())) {
                doTransition(context, ctx, routing, address, onComplete);
              } else {
                doEndProcessing(context, ctx, address, onComplete);
              }
            },
            error -> {
              LOGGER.error("Error happened while communicating with {} knot", error, address);
              onComplete.onError(error);
            }
        );
  }

  private void doTransition(RoutingContext context, SnippetFragmentsContext ctx,
      final Map<String, RoutingEntry> routing, String currentAddress,
      OnCompleteObserver<SnippetFragmentsContext> onComplete) {
    RoutingEntry entry = routing.get(ctx.getTransition());
    if (entry != null) {
      handleRoute(context, entry.getAddress(), entry.getOnTransition(), onComplete);
    } else {
      LOGGER.debug(
          "Received transition '{}' from '{}'. No further routing available for the transition. Go to the response generation.",
          ctx.getTransition(), currentAddress);
      // last knot can return default transition
      context.put(FragmentsContext.KEY, ctx.getDelegate());
      onComplete.onSuccess(ctx);
    }
  }

  private void doEndProcessing(RoutingContext context, SnippetFragmentsContext ctx,
      String currentAddress, OnCompleteObserver<SnippetFragmentsContext> onComplete) {
    LOGGER.debug("Request processing finished by {} Knot. Go to the response generation",
        currentAddress);
    context.put(FragmentsContext.KEY, ctx.getDelegate());
    onComplete.onSuccess(ctx);
  }

}
