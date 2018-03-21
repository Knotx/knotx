/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.splitter;

import io.knotx.proxy.KnotProxy;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceBinder;

public class FragmentSplitterVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(FragmentSplitterVerticle.class);

  private FragmentSplitterOptions options;

  private MessageConsumer<JsonObject> consumer;

  private ServiceBinder serviceBinder;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.options = new FragmentSplitterOptions(config());
  }

  @Override
  public void start() {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());

    //register the service proxy on event bus
    serviceBinder = new ServiceBinder(getVertx());
    consumer = serviceBinder
        .setAddress(options.getAddress())
        .register(KnotProxy.class, new FragmentSplitterKnotProxyImpl(options));
  }

  @Override
  public void stop() {
    serviceBinder.unregister(consumer);
  }
}
