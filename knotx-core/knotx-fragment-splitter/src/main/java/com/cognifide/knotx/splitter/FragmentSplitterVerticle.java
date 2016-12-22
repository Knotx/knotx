/*
 * Knot.x - Reactive microservice assembler - HTML Fragment Splitter
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
package com.cognifide.knotx.splitter;

import com.cognifide.knotx.dataobjects.KnotContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class FragmentSplitterVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(FragmentSplitterVerticle.class);

  private FragmentSplitterConfiguration configuration;

  private FragmentSplitter splitter;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.configuration = new FragmentSplitterConfiguration(config());
    this.splitter = new HtmlFragmentSplitter();
  }

  @Override
  public void start() throws IOException, URISyntaxException {
    LOGGER.debug("Starting <{}>", this.getClass().getName());
    EventBus eventBus = vertx.eventBus();

    eventBus.<KnotContext>consumer(configuration.getAddress()).handler(
        message -> Observable.just(message)
            .doOnNext(this::traceMessage)
            .subscribe(
                response -> {
                  KnotContext context = response.body();
                  context.setFragments(splitter.split(context.clientResponse().body().toString()));
                  context.clientResponse().setStatusCode(HttpResponseStatus.OK).clearBody();
                  response.reply(context);
                },
                error -> {
                  LOGGER.error("Exception happened during HTML splitting.", error);
                  message.reply(processError(message.body(), error));
                }
            )
    );
  }

  private KnotContext processError(KnotContext context, Throwable error) {
    HttpResponseStatus statusCode;
    if (error instanceof NoSuchElementException) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    context.clientResponse().setStatusCode(statusCode);
    return context;
  }

  private void traceMessage(Message<KnotContext> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body());
    }
  }
}
