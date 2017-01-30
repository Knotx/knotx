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
package io.knotx.server;

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.rxjava.proxy.KnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;

public class KnotxAssemblerHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxAssemblerHandler.class);

  private KnotProxy assembler;

  private KnotxServerConfiguration configuration;

  private KnotxAssemblerHandler(Vertx vertx, KnotxServerConfiguration configuration) {
    this.configuration = configuration;
    this.assembler = KnotProxy.createProxy(vertx, configuration.assemblerAddress());
  }

  static KnotxAssemblerHandler create(Vertx vertx, KnotxServerConfiguration configuration) {
    return new KnotxAssemblerHandler(vertx, configuration);
  }

  @Override
  public void handle(RoutingContext context) {
    KnotContext knotContext = context.get("knotContext");

    if (knotContext.getClientResponse().getStatusCode() != HttpResponseStatus.OK.code()) {
      HttpServerResponse httpResponse = context.response();
      writeHeaders(httpResponse, knotContext.getClientResponse());
      httpResponse.setStatusCode(knotContext.getClientResponse().getStatusCode()).end();
    } else {
      assembler.processObservable(knotContext)
          .doOnNext(this::traceMessage)
          .subscribe(
              ctx -> {
                if (ctx.getClientResponse().getStatusCode() == HttpResponseStatus.OK.code()) {
                  sendResponse(context, ctx);
                } else {
                  context.fail(ctx.getClientResponse().getStatusCode());
                }
              },
              error -> {
                LOGGER.error("Error happened while communicating with {} engine", error,
                    configuration.splitterAddress());
                context.fail(error);
              }
          );
    }
  }

  private void sendResponse(final RoutingContext context, final KnotContext knotContext) {
    ClientResponse clientResponse = knotContext.getClientResponse();
    writeHeaders(context.response(), clientResponse);

    context.response().setStatusCode(clientResponse.getStatusCode())
        .end(Buffer.newInstance(clientResponse.getBody()));
  }

  private void writeHeaders(final HttpServerResponse response,
      final ClientResponse clientResponse) {
    clientResponse.getHeaders().names().stream()
        .filter(this::headerFilter)
        .forEach(
            name ->
                clientResponse.getHeaders()
                    .getAll(name)
                    .forEach(value -> response.putHeader(name, value))
        );

    response.headers().remove(HttpHeaders.CONTENT_LENGTH.toString());
  }

  private Boolean headerFilter(String name) {
    return configuration.allowedResponseHeaders().contains(name.toLowerCase());
  }

  private void traceMessage(KnotContext ctx) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <fragment-assembler> with value <{}>", ctx.toJson().encode());
    }
  }
}
