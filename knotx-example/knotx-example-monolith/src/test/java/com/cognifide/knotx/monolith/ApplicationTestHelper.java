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
package com.cognifide.knotx.monolith;

import com.cognifide.knotx.KnotxStarterVerticle;
import com.cognifide.knotx.engine.AbstractKnotxConfigurationTest;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.rxjava.core.Vertx;

public class ApplicationTestHelper extends AbstractKnotxConfigurationTest {

  public static Vertx vertx;

  public static int knotxPort;

  public static String knotxDomain = "localhost";

  public static void startKnotx() throws Exception {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    vertx = Vertx.vertx();

    JsonObject configuration = readJson("knotx-example-monolith.json");
    knotxPort = configuration.getJsonObject("verticles").getJsonObject("com.cognifide.knotx.server.KnotxServerVerticle").getJsonObject("config").getInteger("http.port");

    vertx.deployVerticle(KnotxStarterVerticle.class.getName(), new DeploymentOptions().setConfig(configuration));
  }

  public static void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

}
