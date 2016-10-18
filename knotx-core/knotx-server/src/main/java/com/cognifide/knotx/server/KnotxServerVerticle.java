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

import com.cognifide.knotx.dataobjects.HttpRequestWrapper;
import com.cognifide.knotx.dataobjects.HttpResponseWrapper;
import com.cognifide.knotx.dataobjects.RenderRequest;
import com.cognifide.knotx.dataobjects.RenderResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerResponse;

public class KnotxServerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxServerVerticle.class);

  private KnotxServerConfiguration configuration;

  private HttpServer httpServer;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new KnotxServerConfiguration(config());
  }

  @Override
  public void start(Future<Void> fut) throws IOException, URISyntaxException {
    LOGGER.debug("Starting <{}>", this.getClass().getName());
    httpServer = vertx.createHttpServer();
    EventBus eventBus = vertx.eventBus();

    httpServer.requestHandler(
        request -> {
          final Optional<String> repositoryAddress = configuration.repositoryForPath(request.path());
          final HttpRequestWrapper originalRequestWrapper = new HttpRequestWrapper(request);

          if (repositoryAddress.isPresent()) {
            request.setExpectMultipart(true);
            request.endHandler(aVoid -> eventBus.<JsonObject>sendObservable(repositoryAddress.get(), originalRequestWrapper.toJson())
                .doOnNext(this::traceMessage)
                .map(msg -> new HttpResponseWrapper(msg.body()))
                .subscribe(
                    repoResponse -> {
                      if (repoResponse.statusCode() == HttpResponseStatus.OK) {
                        eventBus.<JsonObject>sendObservable("TODO", requestRendering(repoResponse.body(), originalRequestWrapper))
                            .map(msg -> new RenderResponse(msg.body()))
                            .subscribe(
                                engineResponse -> {
                                  if (engineResponse.isSuccess()) {
                                    writeHeaders(request.response(), repoResponse.headers().add("Content-Length", contentLength(engineResponse)));
                                    request.response().setStatusCode(HttpResponseStatus.OK.code()).end(engineResponse.getHtml());
                                  } else {
                                    request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                                  }
                                },
                                error -> {
                                  LOGGER.error("Error happened", error);
                                  request.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                                }
                            );
                      } else {
                        writeHeaders(request.response(), repoResponse.headers().add("content-length", "0"));
                        request.response().setStatusCode(repoResponse.statusCode().code()).end();
                      }
                    },
                    error -> LOGGER.error("Error: ", error)
                ));
          } else {
            request.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code()).end();
          }
        }
    ).listen(
        configuration.httpPort(),
        result -> {
          if (result.succeeded()) {
            LOGGER.info("Knot.x HTTP Server started. Listening on port {}", configuration.httpPort());
            fut.complete();
          } else {
            LOGGER.error("Unable to start Knot.x HTTP Server.", result.cause());
            fut.fail(result.cause());
          }
        });
  }

  private String contentLength(RenderResponse engineResponse) {
    return Integer.toString(engineResponse.getHtml().length());
  }

  private void writeHeaders(HttpServerResponse response, MultiMap headers) {
    headers.names().stream()
        .filter(this::headerFilter)
        .forEach(name -> response.putHeader(name, headers.get(name)));
  }

  private Boolean headerFilter(String name) {
    return configuration.allowedResponseHeaders().contains(name.toLowerCase());
  }

  private JsonObject requestRendering(Buffer htmlBuffer, HttpRequestWrapper originalRequest) {
    return new RenderRequest().setRequest(originalRequest)
        .setTemplate(htmlBuffer.toString())
        .toJson();
  }

  private void traceMessage(Message<JsonObject> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <template-repository> with value <{}>", message.body().encodePrettily());
    }
  }
}
