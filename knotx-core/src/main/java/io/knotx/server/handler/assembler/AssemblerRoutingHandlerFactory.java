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
package io.knotx.server.handler.assembler;

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.reactivex.proxy.KnotProxy;
import io.knotx.server.handler.api.RoutingHandlerFactory;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;

public class AssemblerRoutingHandlerFactory implements RoutingHandlerFactory {

  @Override
  public String getName() {
    return "assemblerHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    return new AssemblerHandler(vertx, config);
  }

  private class AssemblerHandler implements Handler<RoutingContext> {

    private final Logger LOGGER = LoggerFactory.getLogger(AssemblerHandler.class);

    private KnotProxy assembler;

    private JsonObject configuration;

    private AssemblerHandler(Vertx vertx, JsonObject configuration) {
      this.configuration = configuration;
      JsonObject deliveryOptions = configuration.getJsonObject("deliveryOptions");
      this.assembler = KnotProxy
          .createProxyWithOptions(vertx, configuration.getString("proxyAddress"),
              deliveryOptions != null ? new DeliveryOptions(deliveryOptions)
                  : new DeliveryOptions());
    }

    @Override
    public void handle(RoutingContext context) {
      KnotContext knotContext = context.get(KnotContext.KEY);

      if (isOkClientResponse(knotContext.getClientResponse())) {
        assembler.rxProcess(knotContext)
            .doOnSuccess(this::traceMessage)
            .subscribe(
                ctx -> {
                  context.put(KnotContext.KEY, ctx);
                  context.next();
                },
                error -> {
                  LOGGER.error("Error happened while communicating with {} engine", error,
                      configuration.getString("proxyAddress"));
                  context.fail(error);
                }
            );
      } else {
        context.next();
      }
    }

    private boolean isOkClientResponse(ClientResponse clientResponse) {
      return clientResponse.getStatusCode() == HttpResponseStatus.OK.code();
    }

    private void traceMessage(KnotContext ctx) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER
            .trace("Got message from <fragment-assembler> with value <{}>", ctx.toJson().encode());
      }
    }
  }
}
