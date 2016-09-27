/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
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
package com.cognifide.knotx.engine;

import com.cognifide.knotx.api.TemplateEngineRequest;
import com.cognifide.knotx.api.TemplateEngineResponse;
import com.cognifide.knotx.engine.impl.TemplateEngine;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpClient;
import rx.Observable;

public class TemplateEngineVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngineVerticle.class);

  private TemplateEngine templateEngine;

  private TemplateEngineConfiguration configuration;

  private String serviceName;

  private HttpClient httpClient;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    JsonObject config = config().getJsonObject("config");
    this.serviceName = config.getString("service.name");

    configuration = new TemplateEngineConfiguration(config);

    final JsonObject clientOptions = configuration.getClientOptions();
    httpClient = clientOptions.isEmpty() ?
        this.vertx.createHttpClient() : this.vertx.createHttpClient(new HttpClientOptions(clientOptions));
    templateEngine = new TemplateEngine(httpClient, configuration);

  }

  @Override
  public void start() throws Exception {
    LOGGER.debug("Registered <{}>", this.getClass().getSimpleName());

    EventBus eventBus = vertx.eventBus();
    Observable<Message<JsonObject>> messageObservable = eventBus.<JsonObject>consumer(serviceName).toObservable();

    messageObservable
        .doOnNext(this::traceMessage)
        .subscribe(
            msg -> templateEngine.process(givenTemplate(msg))
                .subscribe(
                    result -> msg.reply(TemplateEngineResponse.success(result).toJsonObject()),
                    error -> {
                      LOGGER.error("Error happened", error);
                      msg.reply(TemplateEngineResponse.error(error.getMessage()).toJsonObject());
                    }
                )
        );
  }

  @Override
  public void stop() throws Exception {
    LOGGER.debug("Stopping TemplateEngineVerticle and stopping http client");
    httpClient.close();
  }

  private TemplateEngineRequest givenTemplate(Message<JsonObject> msg) {
    return new TemplateEngineRequest(msg.body());
  }

  private void traceMessage(Message<JsonObject> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body().encodePrettily());
    }
  }
}
