/*
 * Knot.x - Reactive microservice assembler - View Engine Verticle
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
package com.cognifide.knotx.engine.view;

import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.engine.view.impl.TemplateEngine;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class ViewEngineVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ViewEngineVerticle.class);

  private TemplateEngine templateEngine;

  private ViewEngineConfiguration configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new ViewEngineConfiguration(config());
    templateEngine = new TemplateEngine(this.vertx.eventBus(), configuration);
  }

  @Override
  public void start() throws Exception {
    LOGGER.debug("Starting <{}>", this.getClass().getName());

    vertx.eventBus().<JsonObject>consumer(configuration.getAddress()).handler(
        message -> Observable.just(message)
            .doOnNext(this::traceMessage)
            .flatMap(msg -> {
                  KnotContext inputContext = new KnotContext(msg.body());
                  return templateEngine.process(inputContext)
                      .map(renderedData -> createSuccessResponse(inputContext, renderedData).toJson())
                      .onErrorReturn(error -> {
                        LOGGER.error("Error happened during Template processing", error);
                        return createErrorResponse(inputContext).toJson();
                      });
                }
            ).subscribe(
                message::reply,
                error -> message.reply(createErrorResponse(new KnotContext(message.body())).toJson())
            )
    );
  }

  private KnotContext createSuccessResponse(KnotContext inputContext, String renderedContent) {
    ClientResponse clientResponse = inputContext.clientResponse();
    MultiMap headers = clientResponse.headers();
    headers.set(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(renderedContent.length()));
    clientResponse.setBody(Buffer.buffer(renderedContent)).setHeaders(headers);

    return new KnotContext()
        .setClientRequest(inputContext.clientRequest())
        .setClientResponse(clientResponse);
  }

  private KnotContext createErrorResponse(KnotContext inputContext) {
    ClientResponse errorResponse = new ClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);

    return new KnotContext()
        .setClientRequest(inputContext.clientRequest())
        .setClientResponse(errorResponse);
  }

  private void traceMessage(Message<JsonObject> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Processing message {}", message.body().encodePrettily());
    }
  }
}
