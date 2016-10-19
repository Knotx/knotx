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

import com.cognifide.knotx.dataobjects.EngineRequest;
import com.cognifide.knotx.dataobjects.EngineResponse;
import com.cognifide.knotx.engine.view.impl.TemplateEngine;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
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

    EventBus eventBus = vertx.eventBus();
    Observable<Message<JsonObject>> messageObservable = eventBus.<JsonObject>consumer(configuration.getAddress()).toObservable();
    messageObservable
        .doOnNext(this::traceMessage)
        .flatMap(msg -> templateEngine.process(new EngineRequest(msg.body()))
            .map(renderedData -> EngineResponse.success(renderedData).toJsonObject())
            .onErrorReturn(error -> {
              LOGGER.error("Error happened during Template processing", error);
              return EngineResponse.error(error.getMessage()).toJsonObject();
            })
            .doOnNext(msg::reply)
        ).subscribe();

  }

  private void traceMessage(Message<JsonObject> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body().encodePrettily());
    }
  }
}
