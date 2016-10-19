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

import com.cognifide.knotx.dataobjects.EngineRequest;
import com.cognifide.knotx.dataobjects.EngineResponse;
import com.cognifide.knotx.dataobjects.HttpResponseWrapper;
import com.cognifide.knotx.util.OptionalAction;

import java.util.Map;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;

public class KnotxEngineHandler implements Handler<RoutingContext> {

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

  public static KnotxEngineHandler create(EventBus eventBus, String address, Map<String, RoutingEntry> routing, KnotxServerConfiguration configuration) {
    return new KnotxEngineHandler(eventBus, address, routing, configuration);
  }

  @Override
  public void handle(RoutingContext context) {
    if (context.get("repoResponse") != null && context.get("originalRequest") != null) {
      handleRoute(context, address, routing);
    } else {
      LOGGER.error("Something very unexpected happened - 'repoReponse' is {}, 'originalRequest' is {}. This should not happen",
          context.get("repoResponse") ? "present" : "missing",
          context.get("originalRequest") ? "present" : "missing");
      context.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    }
  }

  private void handleRoute(final RoutingContext context, final String address, final Map<String, RoutingEntry> routing) {
    HttpResponseWrapper repoResponse = context.get("repoResponse");

    //FIXME: Passing request/responses (body + headers) between engine calls.
    eventBus.<JsonObject>sendObservable(address, requestEngine(context))
        .map(msg -> new EngineResponse(msg.body()))
        .subscribe(
            engineResponse -> OptionalAction.of(engineResponse.getTransition())
                .ifPresent(on -> {
                  RoutingEntry entry = routing.get(on);
                  handleRoute(context, entry.address(), entry.onTransition());
                })
                .ifNotPresent(() -> {
                  if (engineResponse.isSuccess()) {
                    writeHeaders(context.response(), repoResponse.headers().add("Content-Length", contentLength(engineResponse)));
                    context.response().setStatusCode(HttpResponseStatus.OK.code()).end(engineResponse.getHtml());
                  } else {
                    context.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                  }
                }),
            error -> {
              LOGGER.error("Error happened while communicating with {} engine", error, address);
              context.fail(error);
            }
        );
  }

  private JsonObject requestEngine(final RoutingContext context) {
    return new EngineRequest().setRequest(context.get("originalRequest"))
        .setTemplate(((HttpResponseWrapper)context.get("repoResponse")).body().toString())
        .toJson();
  }

  private void writeHeaders(HttpServerResponse response, MultiMap headers) {
    headers.names().stream()
        .filter(this::headerFilter)
        .forEach(name -> response.putHeader(name, headers.get(name)));
  }

  private Boolean headerFilter(String name) {
    return configuration.allowedResponseHeaders().contains(name.toLowerCase());
  }

  private String contentLength(EngineResponse engineResponse) {
    return Integer.toString(engineResponse.getHtml().length());
  }
}
