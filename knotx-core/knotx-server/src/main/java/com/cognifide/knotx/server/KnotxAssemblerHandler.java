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

import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;

public class KnotxAssemblerHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxAssemblerHandler.class);

  private EventBus eventBus;

  private KnotxServerConfiguration configuration;

  private KnotxAssemblerHandler(EventBus eventBus, KnotxServerConfiguration configuration) {
    this.eventBus = eventBus;
    this.configuration = configuration;
  }

  static KnotxAssemblerHandler create(EventBus eventBus, KnotxServerConfiguration configuration) {
    return new KnotxAssemblerHandler(eventBus, configuration);
  }


  @Override
  public void handle(RoutingContext context) {
    KnotContext knotContext = context.get("knotContext");

    eventBus.<KnotContext>sendObservable(configuration.assemblerAddress(), knotContext)
        .map(Message::body)
        .doOnNext(this::traceMessage)
        .subscribe(
            ctx -> {
              if (ctx.clientResponse().statusCode() == HttpResponseStatus.OK) {
                sendResponse(context, ctx);
              } else {
                context.fail(ctx.clientResponse().statusCode().code());
              }
            },
            error -> {
              LOGGER.error("Error happened while communicating with {} engine", error,
                  configuration.splitterAddress());
              context.fail(error);
            }
        );
  }

  private void sendResponse(final RoutingContext context, final KnotContext knotContext) {
    ClientResponse clientResponse = knotContext.clientResponse();

    if (clientResponse.statusCode() == HttpResponseStatus.OK) {
      writeContentLength(context.response(), clientResponse);
      writeHeaders(context.response(), clientResponse);
      context.response().setStatusCode(HttpResponseStatus.OK.code()).end(clientResponse.body());
    } else if (clientResponse.statusCode().code() == 500
        || clientResponse.statusCode().code() == 404) {
      context.fail(clientResponse.statusCode().code());
    } else {
      writeHeaders(context.response(), clientResponse);
      context.response().setStatusCode(clientResponse.statusCode().code())
          .end(clientResponse.body());
    }
  }

  private void writeHeaders(final HttpServerResponse response,
      final ClientResponse clientResponse) {
    clientResponse.headers().names().stream()
        .filter(this::headerFilter)
        .forEach(name -> response.putHeader(name, clientResponse.headers().get(name)));
  }

  private void writeContentLength(final HttpServerResponse response,
      final ClientResponse clientResponse) {
    response.putHeader(HttpHeaders.CONTENT_LENGTH.toString(),
        Integer.toString(clientResponse.body().length()));
  }

  private Boolean headerFilter(String name) {
    return configuration.allowedResponseHeaders().contains(name.toLowerCase());
  }

  private void traceMessage(KnotContext ctx) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <fragment-assembler> with value <{}>", ctx);
    }
  }
}
