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
package io.knotx.launcher;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import org.apache.commons.lang3.tuple.Pair;
import rx.Observable;

public class KnotxStarterVerticle extends AbstractVerticle {

  public static final String CONFIG_OVERRIDE = "config";
  public static final String MODULE_OPTIONS = "options";
  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxStarterVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Observable.from(config().getJsonArray("modules"))
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

  private Observable<Pair<String, String>> deployVerticle(final Object module) {
    return vertx.deployVerticleObservable((String) module, getModuleOptions((String) module))
        .map(deploymentID -> Pair.of((String) module, deploymentID));
  }

  private DeploymentOptions getModuleOptions(final String module) {
    DeploymentOptions deploymentOptions = new DeploymentOptions();
    if (config().containsKey(CONFIG_OVERRIDE) && config().getJsonObject(CONFIG_OVERRIDE)
        .containsKey(module)) {
      JsonObject moduleConfig = config().getJsonObject(CONFIG_OVERRIDE).getJsonObject(module);
      if (moduleConfig.containsKey(MODULE_OPTIONS)) {
        deploymentOptions.fromJson(moduleConfig.getJsonObject(MODULE_OPTIONS));
      }
    }
    return deploymentOptions;
  }

  private Observable.Transformer<Pair<String, String>, String> joinDeployments() {
    return observable ->
        observable.reduce(new StringBuilder(System.lineSeparator()).append(System.lineSeparator()),
            this::collectDeployment)
            .map(StringBuilder::toString);
  }

  private StringBuilder collectDeployment(StringBuilder accumulator,
      Pair<String, String> deploymentId) {
    return accumulator
        .append(
            String.format("\t\tDeployed %s [%s]", deploymentId.getRight(), deploymentId.getLeft()))
        .append(System.lineSeparator());
  }
}
