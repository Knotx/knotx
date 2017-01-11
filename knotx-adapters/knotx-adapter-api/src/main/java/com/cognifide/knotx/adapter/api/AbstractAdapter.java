/*
 * Knot.x - Reactive microservice assembler - Adapter API
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
package com.cognifide.knotx.adapter.api;


import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.AdapterResponse;
import com.cognifide.knotx.dataobjects.ClientResponse;

import java.io.IOException;
import java.net.URISyntaxException;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public abstract class AbstractAdapter<C extends AdapterConfiguration> extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAdapter.class);

  protected C configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.configuration = initConfiguration(config());
  }

  @Override
  public void start() throws IOException, URISyntaxException {
    LOGGER.debug("Registered <{}>", this.getClass().getSimpleName());

    Observable<Message<AdapterRequest>> observable = vertx.eventBus().<AdapterRequest>consumer(configuration.getAddress()).toObservable();

    observable
        .doOnNext(this::traceMessage)
        .subscribe(
            msg -> processMessage(msg.body())
                .subscribe(
                    msg::reply,
                    error -> {
                      LOGGER.error("Error happened", error);
                      msg.reply(getErrorResponse(error.getMessage()));
                    }
                )
        );
  }

  protected abstract C initConfiguration(JsonObject config);

  protected abstract Observable<AdapterResponse> processMessage(AdapterRequest message);

  private AdapterResponse getErrorResponse(String message) {
    return new AdapterResponse().setResponse(new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .setBody(Buffer.buffer(message)));
  }

  private void traceMessage(Message<AdapterRequest> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body());
    }
  }

}
