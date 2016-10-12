/*
 * Knot.x - Reactive microservice assembler - API
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

import com.cognifide.knotx.ConfigReader;
import com.cognifide.knotx.TestKnotxStarter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.eventbus.EventBus;

@RunWith(VertxUnitRunner.class)
public class CoreServiceAdapterTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(CoreServiceAdapterTest.class);

  private final static String ADAPTER_ADDRESS = "knotx.core.service-adapter";

  private ConfigReader config = new ConfigReader("test-service-adapter.json");

  private RunTestOnContext vertx = new RunTestOnContext();

  private TestKnotxStarter knotx = new TestKnotxStarter(vertx, config);

  @Rule
  public RuleChain chain = RuleChain.outerRule(config).around(vertx).around(knotx);

  @Test
  public void sampleTest(TestContext context) {
    EventBus eventBus = knotx.vertx().eventBus();
    JsonObject message = new JsonObject().put("test", "message");
    Async async = context.async();

    eventBus.<JsonObject>send(ADAPTER_ADDRESS, message, ar -> {
      if (ar.succeeded()) {
        LOGGER.info("Got message {}", ar.result().body().encodePrettily());
        context.assertEquals(message, ar.result().body());
      } else {
        context.fail(ar.cause());
      }
      async.complete();
    });
  }

}
