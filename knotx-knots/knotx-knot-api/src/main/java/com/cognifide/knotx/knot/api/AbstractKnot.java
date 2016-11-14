/*
 * Knot.x - Reactive microservice assembler - Knot API
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
package com.cognifide.knotx.knot.api;

import com.cognifide.knotx.dataobjects.KnotContext;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

/**
 * Abstract class that should be root for all custom knots
 */
public abstract class AbstractKnot<C extends KnotConfiguration> extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKnot.class);

  protected C configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.configuration = initConfiguration(config());
  }

  @Override
  public void start() throws Exception {
    LOGGER.debug("Starting <{}>", this.getClass().getName());

    vertx.eventBus().<JsonObject>consumer(configuration.getAddress())
        .handler(message -> Observable.just(message)
            .doOnNext(this::traceMessage)
            .subscribe(
                result -> handle(result, message::reply),
                error -> {
                  LOGGER.error("Error occurred in " + this.getClass().getName() + ".", error);
                  message.reply(processError(new KnotContext(message.body()), error).toJson());
                }
            ));
  }

  protected abstract void handle(Message<JsonObject> result, Handler<JsonObject> handler);

  protected abstract KnotContext processError(KnotContext knotContext, Throwable error);

  protected abstract C initConfiguration(JsonObject config);


  protected void traceMessage(Message<JsonObject> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Processing message {}", message.body().encodePrettily());
    }
  }
}
