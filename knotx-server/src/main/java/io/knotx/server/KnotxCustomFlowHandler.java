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

import java.util.Map;

import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.rxjava.proxy.KnotProxy;
import io.knotx.server.configuration.RoutingEntry;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;

public class KnotxCustomFlowHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxCustomFlowHandler.class);
  private static final String KNOT_CONTEXT_KEY = "knotContext";

  private Vertx vertx;
  private String address;
  private Map<String, RoutingEntry> routing;
  private KnotProxy customFlowProxy;

  public KnotxCustomFlowHandler(Vertx vertx, String address, Map<String, RoutingEntry> routing) {
    this.vertx = vertx;
    this.address = address;
    this.routing = routing;
    this.customFlowProxy = KnotProxy.createProxy(vertx, address);
  }

  public static Handler<RoutingContext> create(Vertx vertx, String address, Map<String, RoutingEntry> routing) {
    return new KnotxCustomFlowHandler(vertx, address, routing);
  }

  @Override
  public void handle(RoutingContext context) {
    final KnotContext knotContext = new KnotContext().setClientRequest(new ClientRequest(context.request()));

    customFlowProxy
        .rxProcess(knotContext)
        .subscribe(
            ctx -> {
                context.put("knotContext", ctx);
                context.next();
            },
            error -> {
              LOGGER.error("Error happened while communicating with {} engine", error,
                  address);
              context.fail(error);
            }
        );
  }
  private void sendResponse(final RoutingContext context, final ClientResponse clientResponse) {
    HttpServerResponse httpResponse = context.response();
//    writeHeaders(context.response(), clientResponse);

    httpResponse.setStatusCode(clientResponse.getStatusCode());

    if (clientResponse.getStatusCode() == HttpResponseStatus.OK.code()) {
      httpResponse.end(Buffer.newInstance(clientResponse.getBody()));
    } else {
      httpResponse.end();
    }
  }


  private boolean isSuccessResponse(ClientResponse repoResponse) {
    return HttpResponseStatus.OK.code() == repoResponse.getStatusCode();
  }

  private boolean isErrorResponse(ClientResponse repoResponse) {
    return HttpResponseStatus.INTERNAL_SERVER_ERROR.code() == repoResponse.getStatusCode() ||
        HttpResponseStatus.NOT_FOUND.code() == repoResponse.getStatusCode();
  }
}
