/*
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

import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KnotxStarterVerticle extends AbstractVerticle {

  private static final String MODULES_ARRAY = "modules";
  private static final String CONFIG_OVERRIDE = "config";
  private static final String MODULE_OPTIONS = "options";
  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxStarterVerticle.class);

  private List<ModuleDeploymentId> deploymentIds;
  private ConfigRetriever configRetriever;

  @Override
  public void start(Future<Void> startFuture) {
    printLogo();

    if (config().getJsonObject("configRetrieverOptions") == null) {
      startFuture.fail("Missing 'configRetrieverOptions' in the main config file");
    }

    configRetriever = ConfigRetriever.create(vertx,
        new ConfigRetrieverOptions(
            config().getJsonObject("configRetrieverOptions", new JsonObject())));

    configRetriever.listen(conf -> {
      if (!deploymentIds.isEmpty()) {
        LOGGER.warn("Configuration changed - Re-deploying Knot.x");
        Observable.fromIterable(deploymentIds)
            .flatMap(item -> vertx.rxUndeploy(item.deploymentId).toObservable())
            .collect(() -> Lists.newArrayList(),
                (collector, result) -> collector.add(result))
            .subscribe(
                success -> {
                  LOGGER.warn("Knot.x STOPPED.");
                  deployVerticles(conf.getNewConfiguration(), null);
                },
                error -> {
                  LOGGER.error("Unable to undeploy verticles", error);
                  startFuture.fail(error);
                }
            );
      }
    });

    configRetriever.getConfig(ar -> {
      if (ar.succeeded()) {
        JsonObject configuration = ar.result();
        deployVerticles(configuration, startFuture);
      } else {
        LOGGER.fatal("Unable to start Knot.x", ar.cause());
        startFuture.fail(ar.cause());
      }
    });
  }

  private void deployVerticles(JsonObject config, Future<Void> completion) {
    LOGGER.info("STARTING Knot.x");
    Observable.fromIterable(config.getJsonArray(MODULES_ARRAY))
        .flatMap(module ->
            deployVerticle(module, config.getJsonObject(CONFIG_OVERRIDE, new JsonObject()))
        )
        .reduce(new ArrayList<ModuleDeploymentId>(), (accumulator, item) -> {
          accumulator.add(item);
          return accumulator;
        })
        .subscribe(
            ids -> {
              deploymentIds = Lists.newArrayList(ids);
              LOGGER.info("Knot.x STARTED {}", buildMessage(ids));
              if (completion != null) {
                completion.complete();
              }
            },
            error -> {
              LOGGER.error("Verticle could not be deployed", error);
              if (completion != null) {
                completion.fail(error);
              }
            }
        );
  }

  private Observable<ModuleDeploymentId> deployVerticle(final Object module, JsonObject config) {
    return vertx.rxDeployVerticle((String) module, getModuleOptions(config, (String) module))
        .map(deploymentID -> ModuleDeploymentId.of((String) module, deploymentID))
        .toObservable()
        .onErrorResumeNext(error -> {
          LOGGER.warn("Can't deploy {}", module, error);
          return Observable.empty();
        });
  }

  private DeploymentOptions getModuleOptions(JsonObject config,
      final String module) {
    DeploymentOptions deploymentOptions = new DeploymentOptions();
    if (config.containsKey(module)) {
      JsonObject moduleConfig = config.getJsonObject(module);
      if (moduleConfig.containsKey(MODULE_OPTIONS)) {
        deploymentOptions.fromJson(moduleConfig.getJsonObject(MODULE_OPTIONS));
      }
    }
    return deploymentOptions;
  }

  private String buildMessage(List<ModuleDeploymentId> ids) {
    return new StringBuilder(System.lineSeparator())
        .append(System.lineSeparator())
        .append(
            ids.stream()
                .map(item -> String.format("\t\tDeployed %s [%s]", item.name,
                    item.deploymentId))
                .collect(Collectors.joining(System.lineSeparator())))
        .append(System.lineSeparator())
        .toString();
  }

  private static class ModuleDeploymentId {

    private String name;
    private String deploymentId;

    public static ModuleDeploymentId of(String name, String deploymentId) {
      ModuleDeploymentId item = new ModuleDeploymentId();
      item.name = name;
      item.deploymentId = deploymentId;

      return item;
    }
  }

  private void printLogo() {
    System.out.println(
        "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    System.out.println(
        "@@                                  ,,,,,,,,,                                 @@");
    System.out.println(
        "@@                                *,,,,,,,,,,,*                               @@");
    System.out.println(
        "@@                              @@&,,,,,,,,,,,,,*                             @@");
    System.out.println(
        "@@                            @@@@@@,,,,,,,,,,,,,,*                           @@");
    System.out.println(
        "@@                          @@@@@@@@@,,,,,,,,,,,,,,,*                         @@");
    System.out.println(
        "@@                        @@@@@@@@@@@@/,,,,,,,,,,,,,,,*                       @@");
    System.out.println(
        "@@                      @@@@@@@@@@@@@@@#,,,,,,,,,,,,,,,,*                     @@");
    System.out.println(
        "@@                    &@@@@@@@@@@@@@@@@@&,,,,,,,,,,,,,,*@@#                   @@");
    System.out.println(
        "@@                  @@&,@@@@@@@@@@@@@@@@@@,,,,,,,,,,,,(@@@@@#                 @@");
    System.out.println(
        "@@                @@@@&,,%@@@@@@@@@@@@@@@@@*,,,,,,,,,%@@@@@@@@#               @@");
    System.out.println(
        "@@              @@@@@@&,,,*@@@@@@@@@@@@@@@@@(,,,,,,,@@@@@@@@@@@@#             @@");
    System.out.println(
        "@@            @@@@@@@@&,,,,,@@@@@@@@@@@@@@@@@&,,,,,@@@@@@@@@@@@@@@#           @@");
    System.out.println(
        "@@          @@@@@@@@@@&,,,,,,%@@@@@@@@@@@@@@@@@,,/@@@@@@@@@@@@@@@@@,*         @@");
    System.out.println(
        "@@        @@@@@@@@@@@@&,,,,,,,*@@@@@@@@@@@@@@@@@%@@@@@@@@@@@@@@@@#,,,,*       @@");
    System.out.println(
        "@@      ,@@@@@@@@@@@@@&,,,,,,,,,@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*,,,,,,,*     @@");
    System.out.println(
        "@@    ,,,@@@@@@@@@@@@@&,,,,,,,,,,#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@,,,,,,,,,,,*   @@");
    System.out.println(
        "@@  *,,,,#@@@@@@@@@@@@&,,,,,,,,,,,*@@@@@@@@@@@@@@@@@@@@@@@@@@(,,,,,,,,,,,,,,* @@");
    System.out.println(
        "@@ ,,,,,,*@@@@@@@@@@@@&,,,,,,,,,,,,,@@@@@@@@@@@@@@@@@@@@@@@@,,,,,,,,,,,,,,,,,*@@");
    System.out.println(
        "@@,,,,,,,,@@@@@@@@@@@@&,,,,,,,,,,,,,,(@@@@@@@@@@@@@@@@@@@@&,,,,,,,,,,,,,,,,,,,@@");
    System.out.println(
        "@@/,,,,,,,&@@@@@@@@@@@&,,,,,,,,,,,,,,,*@@@@@@@@@@@@@@@@@@@/,,,,,,,,,,,,,,,,,,,@@");
    System.out.println(
        "@@ *,,,,,,(@@@@@@@@@@@&,,,,,,,,,,,,,,#@@@@@@@@@@@@@@@@@@@@@&,,,,,,,,,,,,,,,,, @@");
    System.out.println(
        "@@   ,,,,,*@@@@@@@@@@@&,,,,,,,,,,,,,@@@@@@@@@@@@@@@@@@@@@@@@@,,,,,,,,,,,,,,*  @@");
    System.out.println(
        "@@     ,,,,@@@@@@@@@@@&,,,,,,,,,,,*@@@@@@@@@@@@@@@@@@@@@@@@@@@(,,,,,,,,,,*    @@");
    System.out.println(
        "@@       ,,,,,,,,,,,,,,,,,,,,,,,,%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@,,,,,,,*      @@");
    System.out.println(
        "@@         ,,&@@@@@@@%,,,,,,,,,,@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*,,,*        @@");
    System.out.println(
        "@@           @@@@@@@@@@*,,,,,,/@@@@@@@@@@@@@@@@@*@@@@@@@@@@@@@@@@@#*          @@");
    System.out.println(
        "@@             @@@@@@@@@,,,,,&@@@@@@@@@@@@@@@@#,,,@@@@@@@@@@@@@@@@            @@");
    System.out.println(
        "@@               @@@@@@@,,,,@@@@@@@@@@@@@@@@@*,,,,,%@@@@@@@@@@@@              @@");
    System.out.println(
        "@@                 @@@@*,,(@@@@@@@@@@@@@@@@@,,,,,,,,/@@@@@@@@@                @@");
    System.out.println(
        "@@                   @,,,&@@@@@@@@@@@@@@@@&,,,,,,,,,,,@@@@@@                  @@");
    System.out.println(
        "@@                     *@@@@@@@@@@@@@@@@@(,,,,,,,,,,,,,&@@                    @@");
    System.out.println(
        "@@                       @@@@@@@@@@@@@@@*,,,,,,,,,,,,,,*                      @@");
    System.out.println(
        "@@                         @@@@@@@@@@@@,,,,,,,,,,,,,,*                        @@");
    System.out.println(
        "@@                           @@@@@@@@%,,,,,,,,,,,,,*                          @@");
    System.out.println(
        "@@                            @@@@@/,,,,,,,,,,,,*                             @@");
    System.out.println(
        "@@                               @@,,,,,,,,,,,,*                              @@");
    System.out.println(
        "@@                                 *,,,,,,,,,*                                @@");
    System.out.println(
        "@@                                    ,,,*/                                   @@");
    System.out.println(
        "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    System.out.println();
  }
}
