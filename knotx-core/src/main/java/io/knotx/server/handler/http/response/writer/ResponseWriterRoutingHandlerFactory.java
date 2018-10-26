/*
 * Copyright (C) 2018 Cognifide Limited
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
package io.knotx.server.handler.http.response.writer;

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.server.handler.api.RoutingHandlerFactory;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResponseWriterRoutingHandlerFactory implements RoutingHandlerFactory {

  private static final Logger LOG = LoggerFactory.getLogger(ResponseWriterRoutingHandlerFactory.class);

  @Override
  public String getName() {
    return "writerHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    return routingContext -> {
      Set<String> allowedResponseHeaders = getAllowedHeaders(config);

      KnotContext knotContext = routingContext.get(KnotContext.KEY);
      sendResponse(routingContext, allowedResponseHeaders, knotContext.getClientResponse());
    };
  }

  private Set<String> getAllowedHeaders(JsonObject config) {
    List configList = config.getJsonArray("allowedResponseHeaders").getList();
    Set<String> result = new HashSet<>(configList.size());
    configList.forEach(value -> result.add(((String) value).toLowerCase()));
    return result;
  }

  private void sendResponse(RoutingContext context, Set<String> allowedResponseHeaders,
      ClientResponse clientResponse) {
    HttpServerResponse httpResponse = context.response();
    writeHeaders(httpResponse, allowedResponseHeaders, clientResponse);
    httpResponse.setStatusCode(clientResponse.getStatusCode());

    LOG.info("!!! HTTP response [{}]", httpResponse.getStatusCode());

    if (isOkClientResponse(clientResponse)) {
      httpResponse.end(Buffer.newInstance(clientResponse.getBody()));
    } else {
      httpResponse.end();
    }
  }

  private boolean isOkClientResponse(ClientResponse clientResponse) {
    return clientResponse.getStatusCode() == HttpResponseStatus.OK.code();
  }

  private void writeHeaders(final HttpServerResponse response, Set<String> allowedResponseHeaders,
      final ClientResponse clientResponse) {
    clientResponse.getHeaders().names().stream()
        .filter(header -> headerFilter(allowedResponseHeaders, header))
        .forEach(
            name ->
                clientResponse.getHeaders()
                    .getAll(name)
                    .forEach(value -> response.headers().add(name, value))
        );
    response.headers().remove(HttpHeaders.CONTENT_LENGTH.toString());
  }

  private Boolean headerFilter(Set<String> allowedResponseHeaders, String name) {
    return allowedResponseHeaders.contains(name.toLowerCase());
  }

}
