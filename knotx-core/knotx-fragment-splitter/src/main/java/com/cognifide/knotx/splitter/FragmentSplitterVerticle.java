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

import com.cognifide.knotx.fragments.Fragment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
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

    Observable<Message<String>> messageObservable = eventBus.<String>consumer(configuration.getAddress()).toObservable();
    messageObservable
        .doOnNext(this::traceMessage)
        .subscribe(
            msg -> this.split(msg.body())
                .flatMap(this::emitFragments)
                .map(Fragment::toJson)
                .reduce(new JsonArray(), JsonArray::add)
                .subscribe(
                    msg::reply,
                    error -> {
                      LOGGER.error("Error happened", error);
                      msg.reply(error.getMessage());
                    }
                )
        );
  }

  private Observable<List<Fragment>> split(final String template) {
    return vertx.executeBlockingObservable(handle ->
        handle.complete(splitter.split(template))
    );
  }

  private Observable<Fragment> emitFragments(List<Fragment> fragments) {
    return Observable.from(fragments);
  }

  private void traceMessage(Message<String> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body());
    }
  }
}
