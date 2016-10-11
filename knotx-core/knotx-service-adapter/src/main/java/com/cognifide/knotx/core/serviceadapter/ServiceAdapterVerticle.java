/*
 * Knot.x - Sample App with Mock service
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
package com.cognifide.knotx.core.serviceadapter;


import com.cognifide.knotx.core.serviceadapter.http.HttpClientFacade;

import java.io.IOException;
import java.net.URISyntaxException;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class ServiceAdapterVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAdapterVerticle.class);

  private ServiceAdapterConfiguration configuration;

  private HttpClientFacade httpClientFacade;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.configuration = new ServiceAdapterConfiguration(config());
    this.httpClientFacade = new HttpClientFacade(this.vertx, configuration);
  }

  @Override
  public void start(Future<Void> fut) throws IOException, URISyntaxException {
    LOGGER.debug("Registered <{}>", this.getClass().getSimpleName());

    EventBus eventBus = vertx.eventBus();
    Observable<Message<JsonObject>> messageObservable = eventBus.<JsonObject>consumer(configuration.getAddress()).toObservable();

    messageObservable
            .doOnNext(this::traceMessage)
            .subscribe(
                    msg -> httpClientFacade.process(msg.body())
                            .subscribe(
                                    result -> msg.reply(ServiceAdapterResponse.success(result)),
                                    error -> {
                                      LOGGER.error("Error happened", error);
                                      msg.reply(ServiceAdapterResponse.error(error.getMessage()).toJsonObject());
                                    }
                            )
            );
  }

  @Override
  public void stop() throws Exception {
    httpClientFacade.close();
  }

  private void traceMessage(Message<JsonObject> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body().encodePrettily());
    }
  }

}
