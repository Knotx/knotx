/*
 * Copyright (C) 2019 Cognifide Limited
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
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.Set;

//ToDo unit tests
class ResponseWriterHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseWriterHandler.class);

  private final Set<String> allowedResponseHeaders;

  ResponseWriterHandler(Set<String> allowedResponseHeaders) {
    this.allowedResponseHeaders = allowedResponseHeaders;
  }

  @Override
  public void handle(RoutingContext context) {
    RequestContext requestContext = context.get(RequestContext.KEY);
    try {
      if (isOk(requestContext)) {
        end(context, requestContext);
      } else {
        fail(context, requestContext);
      }
    } catch (Exception e) {
      handleFatal(context, requestContext, e);
    }
  }

  private boolean isOk(RequestContext requestContext) {
    return !requestContext.status().isFailed();
  }

  private void end(RoutingContext context, RequestContext requestContext) {
    HttpServerResponse httpResponse = context.response();

    ClientResponse clientResponse = extractAssemblerResult(requestContext);
    writeHeaders(httpResponse, allowedResponseHeaders, clientResponse);
    httpResponse.setStatusCode(clientResponse.getStatusCode());
    httpResponse.end(Buffer.newInstance(clientResponse.getBody()));
  }

  //FixMe should ClientResponse be part of RequestContext?
  private ClientResponse extractAssemblerResult(RequestContext requestContext) {
    return new ClientResponse(
        requestContext.getRequestEvent().getPayload().getJsonObject("assemblerResult"));
  }

  private void fail(RoutingContext context, RequestContext requestContext) {
    HttpServerResponse httpResponse = context.response();
    //ToDo maybe more fancy logic here?
    httpResponse.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    httpResponse.end();
  }

  private void handleFatal(RoutingContext context, RequestContext requestContext, Exception e) {
    LOGGER.error("Fatal error", e);
    HttpServerResponse httpResponse = context.response();
    httpResponse.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    httpResponse.end();
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

