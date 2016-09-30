/*
 * Knot.x - Reactive microservice assembler - Launcher
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

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public final class DeploymentHelper {

  private DeploymentHelper() {
    //Hidden constructors
  }

  public static void deployVerticle(Vertx vertx, String verticleClass, JsonObject configuration) {
    DeploymentOptions deploymentOptions = new DeploymentOptions();
    Integer numberOfInstances = configuration.getInteger("number.of.instances");
    if (numberOfInstances != null) {
      deploymentOptions.setInstances(numberOfInstances);
    }
    deploymentOptions.setConfig(configuration);
    vertx.deployVerticle(verticleClass, deploymentOptions);
  }
}
