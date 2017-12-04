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

import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.reactivex.proxy.RepositoryConnectorProxy;
import io.knotx.server.configuration.KnotxServerConfiguration;
import io.knotx.server.configuration.RepositoryEntry;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.Optional;

public class KnotxRepositoryHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxRepositoryHandler.class);

  private Vertx vertx;

  private KnotxServerConfiguration configuration;

  private KnotxRepositoryHandler(Vertx vertx, KnotxServerConfiguration configuration) {
    this.vertx = vertx;
    this.configuration = configuration;
  }

  static KnotxRepositoryHandler create(Vertx vertx, KnotxServerConfiguration configuration) {
    return new KnotxRepositoryHandler(vertx, configuration);
  }

  @Override
  public void handle(RoutingContext context) {
    final Optional<RepositoryEntry> repositoryEntry = configuration
        .getDefaultFlow().repositoryForPath(context.request().path());
    final KnotContext knotContext = toKnotContext(context);

    if (repositoryEntry.isPresent()) {
      RepositoryConnectorProxy
          .createProxyWithOptions(vertx, repositoryEntry.get().address(), configuration.getDeliveryOptions())
          .rxProcess(knotContext.getClientRequest())
          .doOnSuccess(this::traceMessage)
          .subscribe(
              repoResponse -> {
                if (isSuccessResponse(repoResponse)) {
                  if (repositoryEntry.get().doProcessing()) {
                    knotContext.setClientResponse(repoResponse);
                    context.put("knotContext", knotContext);
                    context.next();
                  } else {
                    writeHeaders(context.response(), repoResponse.getHeaders());
                    context.response().setStatusCode(repoResponse.getStatusCode())
                        .end(Buffer.newInstance(repoResponse.getBody()));
                  }
                } else if (isErrorResponse(repoResponse)) {
                  context.fail(repoResponse.getStatusCode());
                } else {
                  writeHeaders(context.response(),
                      repoResponse.getHeaders().add("Content-Length", "0"));
                  context.response().setStatusCode(repoResponse.getStatusCode()).end();
                }
              },
              context::fail
          );

    } else {
      context.fail(HttpResponseStatus.NOT_FOUND.code());
    }
  }

  private boolean isSuccessResponse(ClientResponse repoResponse) {
    return HttpResponseStatus.OK.code() == repoResponse.getStatusCode();
  }

  private boolean isErrorResponse(ClientResponse repoResponse) {
    return HttpResponseStatus.INTERNAL_SERVER_ERROR.code() == repoResponse.getStatusCode() ||
        HttpResponseStatus.NOT_FOUND.code() == repoResponse.getStatusCode();
  }

  private KnotContext toKnotContext(RoutingContext context) {
    return new KnotContext().setClientRequest(new ClientRequest(context.request()));
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
