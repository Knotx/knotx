/*
 * Knot.x - Reactive microservice assembler - View Knot
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
package com.cognifide.knotx.knot.view;

import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.knot.api.AbstractKnot;
import com.cognifide.knotx.knot.view.impl.TemplateEngine;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class ViewKnotVerticle extends AbstractKnot<ViewKnotConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ViewKnotVerticle.class);

  private TemplateEngine templateEngine;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    templateEngine = new TemplateEngine(this.vertx.eventBus(), configuration);
  }

  @Override
  public void start() throws Exception {
    LOGGER.debug("Starting <{}>", this.getClass().getName());

    vertx.eventBus().<KnotContext>consumer(configuration.getAddress()).handler(
        message -> Observable.just(message)
            .doOnNext(this::traceMessage)
            .flatMap(msg -> {
                  KnotContext inputContext = msg.body();
                  return templateEngine.process(inputContext)
                      .map(renderedData -> createSuccessResponse(inputContext, renderedData))
                      .onErrorReturn(error -> {
                        return processError(inputContext, error);
                      });
                }
            ).subscribe(
                message::reply,
                error -> message.reply(processError(message.body(), error))
            )
    );
  }

  @Override
  protected ViewKnotConfiguration initConfiguration(JsonObject config) {
    return new ViewKnotConfiguration(config);
  }

  @Override
  protected void handle(Message result, Handler handler) {
    //Not used in this knot
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    LOGGER.error("Error happened during Template processing", error);
    ClientResponse errorResponse = new ClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);

    return new KnotContext()
        .setClientRequest(inputContext.clientRequest())
        .setClientResponse(errorResponse);
  }

  @Override
  protected ViewKnotConfiguration initConfiguration(JsonObject config) {
    return new ViewKnotConfiguration(config);
  }

  @Override
  protected void handle(Message result, Handler handler) {
    //Not used in this knot
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

}
