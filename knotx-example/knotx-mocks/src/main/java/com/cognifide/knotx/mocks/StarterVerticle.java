/*
 * Knot.x - Mocked services for sample app
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
package com.cognifide.knotx.mocks;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class StarterVerticle extends AbstractVerticle {

  @Override
  public void start() {
    Vertx vertx = Vertx.vertx();

    vertx.deployVerticle(new MockRemoteRepositoryVerticle(), new DeploymentOptions().setConfig(config().getJsonObject("mockRepo")));
    vertx.deployVerticle(new MockServiceVerticle(), new DeploymentOptions().setConfig(config().getJsonObject("mockService")));
  }

}
