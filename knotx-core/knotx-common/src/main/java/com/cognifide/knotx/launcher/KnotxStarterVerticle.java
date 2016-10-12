/*
 * Knot.x - Reactive microservice assembler - Standalone Knot.x
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
package com.cognifide.knotx.launcher;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

public class KnotxStarterVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxStarterVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Observable.from(config().getJsonObject("verticles"))
        .flatMap(this::deployVerticle)
        .compose(joinDeployments())
        .subscribe(
            message -> {
              LOGGER.info("Knot.x STARTED {}", message);
              startFuture.complete();
            },
            error -> {
              LOGGER.error("Verticle could not be deployed {}", error);
              startFuture.fail(error);
            }
        );
  }

  private Observable<Pair<String, String>> deployVerticle(Map.Entry<String, Object> verticleConf) {
    return vertx.deployVerticleObservable(verticleConf.getKey(), new DeploymentOptions((JsonObject) verticleConf.getValue()))
        .map(deploymentID -> Pair.of(verticleConf.getKey(), deploymentID));
  }

  private Observable.Transformer<Pair<String, String>, String> joinDeployments() {
    return observable ->
        observable.reduce(new StringBuilder(System.lineSeparator()).append(System.lineSeparator()), this::collectDeployment)
            .map(StringBuilder::toString);
  }

  private StringBuilder collectDeployment(StringBuilder accumulator, Pair<String, String> deploymentId) {
    return accumulator
        .append(String.format("\t\tDeployed %s [%s]", deploymentId.getRight(), deploymentId.getLeft()))
        .append(System.lineSeparator());
  }
}
