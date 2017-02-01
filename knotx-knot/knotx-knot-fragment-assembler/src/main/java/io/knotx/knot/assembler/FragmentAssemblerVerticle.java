/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.knot.assembler;

import io.knotx.knot.assembler.impl.FragmentAssemblerKnotProxyImpl;
import io.knotx.proxy.KnotProxy;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ProxyHelper;
import java.io.IOException;
import java.net.URISyntaxException;

public class FragmentAssemblerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(FragmentAssemblerVerticle.class);

  private MessageConsumer<JsonObject> consumer;

  private FragmentAssemblerConfiguration configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new FragmentAssemblerConfiguration(config());
  }

  @Override
  public void start() throws IOException, URISyntaxException {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());

    KnotProxy assemblerModule = new FragmentAssemblerKnotProxyImpl(config());

    //register the service proxy on event bus
    consumer = ProxyHelper
        .registerService(KnotProxy.class, vertx, assemblerModule, configuration.address());
  }

  @Override
  public void stop() throws Exception {
    ProxyHelper.unregisterService(consumer);
  }

}
