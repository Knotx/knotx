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

import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;

import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;

public class KnotxRepositoryHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxRepositoryHandler.class);

  private EventBus eventBus;

  private KnotxServerConfiguration configuration;

  private KnotxRepositoryHandler(EventBus eventBus, KnotxServerConfiguration configuration) {
    this.eventBus = eventBus;
    this.configuration = configuration;
  }

  public static KnotxRepositoryHandler create(EventBus eventBus, KnotxServerConfiguration configuration) {
    return new KnotxRepositoryHandler(eventBus, configuration);
  }

  @Override
  public void handle(RoutingContext context) {
    final Optional<KnotxServerConfiguration.RepositoryEntry> repositoryEntry = configuration.repositoryForPath(context.request().path());
    final KnotContext knotContext = toKnotContext(context);

    if (repositoryEntry.isPresent()) {
      eventBus.<JsonObject>sendObservable(repositoryEntry.get().address(), knotContext.clientRequest().toJson())
          .doOnNext(this::traceMessage)
          .map(msg -> new ClientResponse(msg.body()))
          .subscribe(
              repoResponse -> {
                if (isSuccessResponse(repoResponse)) {
                  if (repositoryEntry.get().doProcessing()) {
                    knotContext.setClientResponse(repoResponse);
                    context.put("knotContext", knotContext);
                    context.next();
                  }
                  writeHeaders(context.response(), repoResponse.headers());
                  context.response().setStatusCode(repoResponse.statusCode().code()).end(repoResponse.body());
                } else if (isErrorResponse(repoResponse)) {
                  context.fail(repoResponse.statusCode().code());
                } else {
                  writeHeaders(context.response(), repoResponse.headers().add("Content-Length", "0"));
                  context.response().setStatusCode(repoResponse.statusCode().code()).end();
                }
              },
              error -> context.fail(error)
          );

    } else {
      context.fail(HttpResponseStatus.NOT_FOUND.code());
    }
  }

  private boolean isSuccessResponse(ClientResponse repoResponse) {
    return HttpResponseStatus.OK.equals(repoResponse.statusCode());
  }

  private boolean isErrorResponse(ClientResponse repoResponse) {
    return HttpResponseStatus.INTERNAL_SERVER_ERROR.equals(repoResponse.statusCode()) || HttpResponseStatus.NOT_FOUND.equals(repoResponse.statusCode());
  }

  private KnotContext toKnotContext(RoutingContext context) {
    return new KnotContext().setClientRequest(new ClientRequest(context.request()));
  }

  private void traceMessage(Message<JsonObject> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <template-repository> with value <{}>", message.body().encodePrettily());
    }
  }

  private void writeHeaders(HttpServerResponse response, MultiMap headers) {
    headers.names().stream()
        .filter(this::headerFilter)
        .forEach(name -> response.putHeader(name, headers.get(name)));
  }

  private Boolean headerFilter(String name) {
    return configuration.allowedResponseHeaders().contains(name.toLowerCase());
  }
}
