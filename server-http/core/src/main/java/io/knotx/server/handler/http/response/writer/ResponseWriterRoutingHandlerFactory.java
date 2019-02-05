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

import io.knotx.server.api.context.ClientResponse;
import io.knotx.server.api.context.RequestContext;
import io.knotx.server.api.context.RequestEvent;
import io.knotx.server.api.handler.RoutingHandlerFactory;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//ToDo unit tests
public class ResponseWriterRoutingHandlerFactory implements RoutingHandlerFactory {

  @Override
  public String getName() {
    return "writerHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    return routingContext -> {
      Set<String> allowedResponseHeaders = getAllowedHeaders(config);

      RequestContext requestContext = routingContext.get(RequestContext.KEY);
      sendResponse(routingContext, allowedResponseHeaders, requestContext);
    };
  }

  private Set<String> getAllowedHeaders(JsonObject config) {
    List configList = config.getJsonArray("allowedResponseHeaders").getList();
    Set<String> result = new HashSet<>(configList.size());
    configList.forEach(value -> result.add(((String) value).toLowerCase()));
    return result;
  }

  private void sendResponse(RoutingContext context, Set<String> allowedResponseHeaders,
      RequestContext requestContext) {
    //FixMe should ClientResponse be part of RequestContext?
    ClientResponse clientResponse = new ClientResponse(); //requestContext.getClientResponse();
    HttpServerResponse httpResponse = context.response();
    writeHeaders(httpResponse, allowedResponseHeaders, clientResponse);
    httpResponse.setStatusCode(clientResponse.getStatusCode());
    if (requestContext.status().isFailed()) {
      httpResponse.end();
    } else {
      httpResponse.end(Buffer.newInstance(clientResponse.getBody()));
    }
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
