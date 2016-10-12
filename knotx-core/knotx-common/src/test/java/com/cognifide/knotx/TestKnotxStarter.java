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
package com.cognifide.knotx;

import com.cognifide.knotx.launcher.KnotxStarterVerticle;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.rxjava.core.Vertx;

public class TestKnotxStarter implements TestRule {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestKnotxStarter.class);

  private final ConfigReader config;
  private RunTestOnContext vertxContext;

  private Vertx vertx;

  public TestKnotxStarter(RunTestOnContext vertxContext, ConfigReader config) {
    this.vertxContext = vertxContext;
    this.config = config;
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        vertx = Vertx.newInstance(vertxContext.vertx());

        CountDownLatch latch = new CountDownLatch(1);
        Future<String> deployFuture = Future.future();

        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        vertx.deployVerticle(KnotxStarterVerticle.class.getName(), new DeploymentOptions().setConfig(config.getConfig()), ar -> {
          if (ar.succeeded()) {
            deployFuture.complete();
          } else {
            deployFuture.fail(ar.cause());
          }
          latch.countDown();
        });
        latch.await(3, TimeUnit.SECONDS);

        if (deployFuture.failed()) {
          throw deployFuture.cause();
        }

        base.evaluate();
      }
    };
  }

  public Vertx vertx() {
    return vertx;
  }
}
