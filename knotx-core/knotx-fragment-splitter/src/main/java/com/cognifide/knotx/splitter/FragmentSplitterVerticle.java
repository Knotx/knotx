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

import com.cognifide.knotx.common.BaseMicroserviceVerticle;
import com.cognifide.knotx.splitter.impl.FragmentSplitterConfiguration;
import com.cognifide.knotx.splitter.impl.FragmentSplitterServiceImpl;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ProxyHelper;

public class FragmentSplitterVerticle extends BaseMicroserviceVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(FragmentSplitterVerticle.class);

  private FragmentSplitterService splitterService;
  private FragmentSplitterConfiguration configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.configuration = new FragmentSplitterConfiguration(config());
  }

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    //create the microservice instance
    this.splitterService = new FragmentSplitterServiceImpl(vertx);
    //register the service proxy on event bus
    ProxyHelper.registerService(FragmentSplitterService.class, (Vertx) vertx, splitterService, configuration.getAddress());

    //publish the service in the discovery infrastructure
    publishEventBusService(FragmentSplitterService.SERVICE_NAME, configuration.getAddress(), FragmentSplitterService.class.toString(),
        new JsonObject())
        .setHandler(future.completer());
  }

  //  @Override
//  public void start() throws IOException, URISyntaxException {
//    LOGGER.debug("Starting <{}>", this.getClass().getName());
//    EventBus eventBus = vertx.eventBus();
//
//    eventBus.<KnotContext>consumer(configuration.getAddress()).handler(
//        message -> Observable.just(message)
//            .doOnNext(this::traceMessage)
//            .subscribe(
//                response -> {
//                  KnotContext context = response.body();
//                  context.setFragments(splitter.split(context.clientResponse().body().toString()));
//                  context.clientResponse().setStatusCode(HttpResponseStatus.OK).clearBody();
//                  response.reply(context);
//                },
//                error -> {
//                  LOGGER.error("Exception happened during HTML splitting.", error);
//                  message.reply(processError(message.body(), error));
//                }
//            )
//    );
//  }
}
