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
package io.knotx.server;

import org.apache.commons.lang3.StringUtils;

import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.reactivex.proxy.KnotProxy;
import io.knotx.server.configuration.KnotxServerOptions;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KnotxGatewayContextHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxGatewayContextHandler.class);

  private Vertx vertx;
  private KnotxServerOptions configuration;
  private String address;
  private Map<String, KnotProxy> proxies;

  private KnotxGatewayContextHandler(Vertx vertx, KnotxServerOptions configuration,
      String address) {
    this.vertx = vertx;
    this.configuration = configuration;
    this.address = address;
    this.proxies = new HashMap<>();
  }

  static KnotxGatewayContextHandler create(Vertx vertx, KnotxServerOptions configuration,
      String address) {
    return new KnotxGatewayContextHandler(vertx, configuration, address);
  }

  @Override
  public void handle(RoutingContext context) {
    KnotContext knotContext = context.get(KnotContext.KEY);

    String bodyAsString = context.getBodyAsString();
    if (StringUtils.isNotBlank(bodyAsString)) {
      knotContext.setFragments(Collections.singletonList(Fragment.raw(bodyAsString)));
    }

    LOGGER.debug("CustomFlow: Routing the traffic to '{}'", address);

    proxies.computeIfAbsent(address,
        adr -> KnotProxy.createProxyWithOptions(vertx, adr, configuration.getDeliveryOptions()))
        .rxProcess(knotContext)
        .doOnSuccess(ctx -> context.put(KnotContext.KEY, ctx))
        .subscribe(
            ctx -> {
              context.put(KnotContext.KEY, ctx);
              context.next();
            },
            error -> {
              LOGGER.error("Error happened while communicating with {} engine", error, address);
              context.fail(error);
            }
        );
  }
}
