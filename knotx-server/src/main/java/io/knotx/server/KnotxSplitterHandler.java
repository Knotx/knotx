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

import io.knotx.dataobjects.KnotContext;
import io.knotx.reactivex.proxy.KnotProxy;
import io.knotx.server.configuration.KnotxServerOptions;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;

public class KnotxSplitterHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxSplitterHandler.class);

  private KnotProxy splitter;

  private KnotxServerOptions configuration;

  private KnotxSplitterHandler(Vertx vertx, KnotxServerOptions configuration) {
    this.splitter = KnotProxy
        .createProxyWithOptions(vertx, configuration.getDefaultFlow().getSplitter(),
            configuration.getDeliveryOptions());
    this.configuration = configuration;
  }

  public static KnotxSplitterHandler create(Vertx vertx, KnotxServerOptions configuration) {
    return new KnotxSplitterHandler(vertx, configuration);
  }

  @Override
  public void handle(RoutingContext context) {
    KnotContext knotContext = context.get(KnotContext.KEY);

    splitter.rxProcess(knotContext)
        .doOnSuccess(this::traceMessage)
        .subscribe(
            ctx -> {
              if (ctx.getClientResponse().getStatusCode() == HttpResponseStatus.OK.code()) {
                context.put(KnotContext.KEY, ctx);
                context.next();
              } else {
                context.fail(ctx.getClientResponse().getStatusCode());
              }
            },
            error -> {
              LOGGER.error("Error happened while communicating with {} engine", error,
                  configuration.getDefaultFlow().getSplitter());
              context.fail(error);
            }
        );
  }

  private void traceMessage(KnotContext ctx) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <fragment-splitter> with value <{}>", ctx);
    }
  }

}
