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

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.reactivex.proxy.RepositoryConnectorProxy;
import io.knotx.server.configuration.RepositoryEntry;
import io.knotx.server.configuration.KnotxServerOptions;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KnotxRepositoryHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxRepositoryHandler.class);

  private Vertx vertx;

  private KnotxServerOptions configuration;

  private Map<String, RepositoryConnectorProxy> proxies;

  private KnotxRepositoryHandler(Vertx vertx, KnotxServerOptions configuration) {
    this.vertx = vertx;
    this.configuration = configuration;
    this.proxies = new HashMap<>();
  }

  static KnotxRepositoryHandler create(Vertx vertx, KnotxServerOptions configuration) {
    return new KnotxRepositoryHandler(vertx, configuration);
  }

  @Override
  public void handle(RoutingContext context) {
    final Optional<RepositoryEntry> repositoryEntry = configuration
        .getDefaultFlow().repositoryForPath(context.request().path());
    final KnotContext knotContext = context.get(KnotContext.KEY);

    if (repositoryEntry.isPresent()) {
      proxies.computeIfAbsent( repositoryEntry.get().getAddress(),adr -> RepositoryConnectorProxy.createProxyWithOptions(vertx, adr, configuration.getDeliveryOptions()))
          .rxProcess(knotContext.getClientRequest())
          .doOnSuccess(this::traceMessage)
          .subscribe(
              repoResponse -> handleRepositoryResponse(repoResponse, context, repositoryEntry.get(),
                  knotContext),
              context::fail
          );

    } else {
      context.fail(HttpResponseStatus.NOT_FOUND.code());
    }
  }

  void handleRepositoryResponse(ClientResponse repoResponse, RoutingContext context,
      RepositoryEntry repositoryEntry, KnotContext knotContext) {
    if (isSuccessResponse(repoResponse)) {
      if (repositoryEntry.isDoProcessing()) {
        knotContext.setClientResponse(repoResponse);
        context.put(KnotContext.KEY, knotContext);
        context.next();
      } else {
        endResponse(repoResponse, context);
      }
    } else {
      endResponse(repoResponse, context);
    }
  }

  private void endResponse(ClientResponse repoResponse, RoutingContext context) {
    writeHeaders(context.response(), repoResponse.getHeaders());
    context.response().setStatusCode(repoResponse.getStatusCode());
    if (repoResponse.getBody() != null) {
      context.response().end(Buffer.newInstance(repoResponse.getBody()));
    } else {
      context.response().end();
    }
  }

  private boolean isSuccessResponse(ClientResponse repoResponse) {
    return HttpResponseStatus.OK.code() == repoResponse.getStatusCode();
  }

  private void traceMessage(ClientResponse message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <template-repository> with value <{}>", message.getBody());
    }
  }

  private void writeHeaders(HttpServerResponse response, MultiMap headers) {
    headers.names().stream()
        .filter(this::headerFilter)
        .forEach(
            name ->
                headers
                    .getAll(name)
                    .forEach(value -> response.putHeader(name, value))
        );
  }

  private Boolean headerFilter(String name) {
    return configuration.getAllowedResponseHeaders().contains(name.toLowerCase());
  }
}
