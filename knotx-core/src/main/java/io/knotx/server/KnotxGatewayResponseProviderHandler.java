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
import io.knotx.reactivex.proxy.KnotProxy;
import io.knotx.server.configuration.KnotxServerOptions;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;

public class KnotxGatewayResponseProviderHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxAssemblerHandler.class);

  private KnotProxy responseProviderProxy;

  private KnotxServerOptions configuration;

  private KnotxGatewayResponseProviderHandler(Vertx vertx, KnotxServerOptions configuration) {
    this.configuration = configuration;
    this.responseProviderProxy = KnotProxy
        .createProxyWithOptions(vertx, configuration.getCustomFlow().getResponseProvider(),
            configuration.getDeliveryOptions());
  }

  static KnotxGatewayResponseProviderHandler create(Vertx vertx, KnotxServerOptions configuration) {
    return new KnotxGatewayResponseProviderHandler(vertx, configuration);
  }

  @Override
  public void handle(RoutingContext context) {
    KnotContext knotContext = context.get(KnotContext.KEY);

    if (isOkClientResponse(knotContext.getClientResponse())) {
      responseProviderProxy.rxProcess(knotContext)
          .doOnSuccess(this::traceMessage)
          .subscribe(
              ctx -> {
                if (isOkClientResponse(ctx.getClientResponse())) {
                  sendResponse(context, ctx.getClientResponse());
                } else {
                  context.fail(ctx.getClientResponse().getStatusCode());
                }
              },
              error -> {
                LOGGER.error("Error {} happened while communicating with `{}` engine", error,
                    configuration.getCustomFlow().getResponseProvider());
                context.fail(error);
              }
          );
    } else {
      sendResponse(context, knotContext.getClientResponse());
    }
  }

  private boolean isOkClientResponse(ClientResponse clientResponse) {
    return clientResponse.getStatusCode() == HttpResponseStatus.OK.code();
  }

  private void sendResponse(final RoutingContext context, final ClientResponse clientResponse) {
    HttpServerResponse httpResponse = context.response();
    writeHeaders(context.response(), clientResponse);

    httpResponse.setStatusCode(clientResponse.getStatusCode());

    if (isOkClientResponse(clientResponse)) {
      httpResponse.end(Buffer.newInstance(clientResponse.getBody()));
    } else {
      httpResponse.end();
    }
  }

  private void writeHeaders(final HttpServerResponse response,
      final ClientResponse clientResponse) {
    clientResponse.getHeaders().names().stream()
        .filter(this::headerFilter)
        .forEach(
            name ->
                clientResponse.getHeaders()
                    .getAll(name)
                    .forEach(value -> response.headers().add(name, value))
        );

    response.headers().remove(HttpHeaders.CONTENT_LENGTH.toString());
  }

  private Boolean headerFilter(String name) {
    return configuration.getAllowedResponseHeaders().contains(name.toLowerCase());
  }

  private void traceMessage(KnotContext ctx) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <response-provider> with value <{}>", ctx.toJson().encode());
    }
  }
}
