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
import io.netty.handler.codec.http.HttpStatusClass;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.MultiMap;
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
    traceRequest(requestContext);
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

  private void traceRequest(RequestContext requestContext) {
    if(LOGGER.isTraceEnabled()) {
      LOGGER.trace("Request history: {}", requestContext.toJson().encodePrettily());
    }
  }

  private boolean isOk(RequestContext requestContext) {
    return !requestContext.status().isFailed();
  }

  private void end(RoutingContext context, RequestContext requestContext) {
    final ClientResponse clientResponse = requestContext.getClientResponse();
    final ServerResponse serverResponse = new ServerResponse()
        .withBody(clientResponse.getBody())
        .withHeaders(clientResponse.getHeaders())
        .withStatusCode(clientResponse.getStatusCode());
    serverResponse.end(context);
  }

  private void fail(RoutingContext context, RequestContext requestContext) {
    HttpServerResponse httpResponse = context.response();

    httpResponse.setStatusCode(requestContext.getClientResponse().getStatusCode());
    httpResponse.end();
  }

  private void handleFatal(RoutingContext context, RequestContext requestContext, Exception e) {
    LOGGER.error("Fatal error", e);
    HttpServerResponse httpResponse = context.response();
    httpResponse.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    httpResponse.end();
  }

  private Boolean headerFilter(Set<String> allowedResponseHeaders, String name) {
    return allowedResponseHeaders.contains(name.toLowerCase());
  }

  private class ServerResponse {
    private int statusCode;
    private MultiMap headers;
    private Buffer body;

    ServerResponse withStatusCode(int statusCode) {
      final HttpStatusClass httpStatusClass = HttpStatusClass.valueOf(statusCode);
      if (httpStatusClass != HttpStatusClass.UNKNOWN) {
        this.statusCode = statusCode;
      } else {
        this.statusCode = HttpResponseStatus.BAD_REQUEST.code();
      }
      return this;
    }

    ServerResponse withHeaders(MultiMap headers) {
      if (headers != null) {
        this.headers = headers;
      } else {
        this.headers = MultiMap.caseInsensitiveMultiMap();
      }
      return this;
    }

    ServerResponse withBody(Buffer body) {
      this.body = body;
      return this;
    }

    void end(RoutingContext context) {
      HttpServerResponse httpResponse = context.response();
      writeHeaders(httpResponse, allowedResponseHeaders);
      httpResponse.setStatusCode(statusCode);
      if (body != null) {
        httpResponse.end(io.vertx.reactivex.core.buffer.Buffer.newInstance(body));
      } else {
        httpResponse.end();
      }
    }

    private void writeHeaders(final HttpServerResponse response, Set<String> allowedResponseHeaders) {
      headers.names().stream()
          .filter(header -> headerFilter(allowedResponseHeaders, header))
          .forEach(
              name ->
                  headers
                      .getAll(name)
                      .forEach(value -> response.headers().add(name, value))
          );
      response.headers().remove(HttpHeaders.CONTENT_LENGTH.toString());
    }

  }
}

