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
package io.knotx.example.blockingadapter;

import io.knotx.proxy.AdapterProxy;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

public class ExampleBlockingAdapter extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleBlockingAdapter.class);

  private MessageConsumer<JsonObject> consumer;

  private ExampleBlockingAdapterConfiguration conf;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    conf = new ExampleBlockingAdapterConfiguration(config());
  }

  @Override
  public void start() throws Exception {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    //register the service proxy on event bus
    consumer = ProxyHelper
        .registerService(AdapterProxy.class, getVertx(),
            new ExampleBlockingAdapterProxy(getVertx(), conf.getProtocol(), conf.getDomain(), conf.getPort()),
            conf.getAddress());
  }

  @Override
  public void stop() throws Exception {
    LOGGER.info("Stopping <{}>", this.getClass().getSimpleName());
    ProxyHelper.unregisterService(consumer);
  }
}
