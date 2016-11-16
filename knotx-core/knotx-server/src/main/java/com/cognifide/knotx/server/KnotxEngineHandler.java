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
import com.cognifide.knotx.util.OptionalAction;

import java.util.Map;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;

class KnotxEngineHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxEngineHandler.class);
  private EventBus eventBus;
  private String address;
  private Map<String, RoutingEntry> routing;
  private KnotxServerConfiguration configuration;

  private KnotxEngineHandler(EventBus eventBus, String address, Map<String, RoutingEntry> routing, KnotxServerConfiguration configuration) {
    this.eventBus = eventBus;
    this.address = address;
    this.routing = routing;
    this.configuration = configuration;
  }

  static KnotxEngineHandler create(EventBus eventBus, String address, Map<String, RoutingEntry> routing, KnotxServerConfiguration configuration) {
    return new KnotxEngineHandler(eventBus, address, routing, configuration);
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

  private void handleRoute(final RoutingContext context, final String address, final Map<String, RoutingEntry> routing) {
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
                    LOGGER.error("No on criteria defined in routing for {} transition received from {}", on, address);
                    context.fail(500);
                  }
                })
                .ifNotPresent(() -> sendResponse(context, ctx)),
            error -> {
              LOGGER.error("Error happened while communicating with {} engine", error, address);
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
    } else if (clientResponse.statusCode().code() == 500 || clientResponse.statusCode().code() == 404) {
      context.fail(clientResponse.statusCode().code());
    } else {
      writeHeaders(context.response(), clientResponse);
      context.response().setStatusCode(clientResponse.statusCode().code()).end(clientResponse.body());
    }
  }

  private void writeHeaders(final HttpServerResponse response, final ClientResponse clientResponse) {
    clientResponse.headers().names().stream()
        .filter(this::headerFilter)
        .forEach(name -> response.putHeader(name, clientResponse.headers().get(name)));
  }

  private void writeContentLength(final HttpServerResponse response, final ClientResponse clientResponse) {
    response.putHeader(HttpHeaders.CONTENT_LENGTH.toString(),
        Integer.toString(clientResponse.body().length()));
  }

  private Boolean headerFilter(String name) {
    return configuration.allowedResponseHeaders().contains(name.toLowerCase());
  }

}
