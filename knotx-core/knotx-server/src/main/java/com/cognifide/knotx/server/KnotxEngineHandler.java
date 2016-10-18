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

import com.cognifide.knotx.dataobjects.HttpRequestWrapper;
import com.cognifide.knotx.dataobjects.HttpResponseWrapper;
import com.cognifide.knotx.dataobjects.RenderRequest;
import com.cognifide.knotx.dataobjects.RenderResponse;

import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;

public class KnotxEngineHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxEngineHandler.class);
  private EventBus eventBus;
  private String address;
  private KnotxServerConfiguration configuration;

  private KnotxEngineHandler(EventBus eventBus, String address, KnotxServerConfiguration configuration) {
    this.eventBus = eventBus;
    this.address = address;
    this.configuration = configuration;
  }

  public static KnotxEngineHandler create(EventBus eventBus, String address, KnotxServerConfiguration configuration) {
    return new KnotxEngineHandler(eventBus, address, configuration);
  }

  @Override
  public void handle(RoutingContext context) {
    Optional<HttpResponseWrapper> repoResponse = Optional.ofNullable(context.get("repoResponse"));
    Optional<HttpRequestWrapper> originalRequest = Optional.ofNullable(context.get("originalRequest"));

    if (repoResponse.isPresent() && originalRequest.isPresent()) {
      eventBus.<JsonObject>sendObservable(address, requestRendering(repoResponse.get().body(), originalRequest.get()))
          .map(msg -> new RenderResponse(msg.body()))
          .subscribe(
              engineResponse -> {
                if (engineResponse.isSuccess()) {
                  writeHeaders(context.response(), repoResponse.get().headers().add("Content-Length", contentLength(engineResponse)));
                  context.response().setStatusCode(HttpResponseStatus.OK.code()).end(engineResponse.getHtml());
                } else {
                  context.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                }
              },
              error -> {
                LOGGER.error("Error happened while communicating with {} engine", error, address);
                context.fail(error);
              }
          );
    } else {
      LOGGER.error("Something very unexpected happened - 'repoReponse' is {}, 'originalRequest' is {}. This should not happen",
          repoResponse.isPresent() ? "present" : "missing", originalRequest.isPresent() ? "present" : "missing");
      context.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    }
  }

  private JsonObject requestRendering(Buffer htmlBuffer, HttpRequestWrapper originalRequest) {
    return new RenderRequest().setRequest(originalRequest)
        .setTemplate(htmlBuffer.toString())
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

  private String contentLength(RenderResponse engineResponse) {
    return Integer.toString(engineResponse.getHtml().length());
  }
}
