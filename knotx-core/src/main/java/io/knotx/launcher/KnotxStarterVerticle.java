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
import io.knotx.Version;
import io.knotx.launcher.ModuleDescriptor.DeploymentState;
import io.reactivex.Observable;
import io.reactivex.Single;
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
  private List<ModuleDescriptor> deployedModules;
  private ConfigRetriever configRetriever;

  @Override
  public void start(Future<Void> startFuture) {
    printLogo();

    try {
      JsonObject configOptions = getConfigRetrieverOptions(config());

      configRetriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions(configOptions));
      configRetriever.listen(conf -> {
        if (!deployedModules.isEmpty()) {
          LOGGER.warn("Configuration changed - Re-deploying Knot.x");
          Observable.fromIterable(deployedModules)
              .flatMap(item -> vertx.rxUndeploy(item.getDeploymentId()).toObservable())
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
    } catch (BadKnotxConfigurationException ex) {
      startFuture.fail(ex);
    }
  }

  private JsonObject getConfigRetrieverOptions(JsonObject config) {
    JsonObject configOptions = null;
    if (config().getJsonObject("configRetrieverOptions") != null) {
      configOptions = config.getJsonObject("configRetrieverOptions");
      configOptions.getJsonArray("stores").stream()
          .map(item -> (JsonObject) item)
          .forEach(store -> store.getJsonObject("config")
              .put("path", resolveConfigPath(store.getJsonObject("config").getString("path"))));

    } else {
      throw new BadKnotxConfigurationException(
          "Missing 'configRetrieverOptions' in the main config file");
    }
    return configOptions;
  }

  private String resolveConfigPath(String path) {
    String resolvedPath = path;

    if (path.startsWith("${KNOTX_HOME}")) {
      String home = System.getProperty("knotx.home");
      if (home == null) {
        home = System.getenv("KNOTX_HOME");
        if (home == null) {
          throw new BadKnotxConfigurationException("Unable to resolve ${KNOTX_HOME} for " + path
              + ". System property 'knotx.home', or environment variable 'KNOTX_HOME' are not set");
        }
      }
      if (home != null) {
        resolvedPath = path.replace("${KNOTX_HOME}", home);
      }
    }

    return resolvedPath;
  }

  private void deployVerticles(JsonObject config, Future<Void> completion) {
    LOGGER.info("STARTING Knot.x {} @ {}", Version.getVersion(), Version.getBuildTime());
    Observable.fromIterable(config.getJsonArray(MODULES_ARRAY))
        .cast(String.class)
        .map(ModuleDescriptor::parse)
        .flatMap(item -> deployVerticle(config, item))
        .reduce(new ArrayList<ModuleDescriptor>(), (accumulator, item) -> {
          accumulator.add(item);
          return accumulator;
        })
        .subscribe(
            deployments -> {
              deployedModules = Lists.newArrayList(deployments);
              LOGGER.info("Knot.x STARTED {}", buildMessage());
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

  private Observable<ModuleDescriptor> deployVerticle(final JsonObject config,
      final ModuleDescriptor module) {
    return vertx
        .rxDeployVerticle(module.getName(), getModuleOptions(config, module.getAlias()))
        .map(deployId ->
            new ModuleDescriptor(module)
                .setDeploymentId(deployId)
                .setState(DeploymentState.SUCCESS))
        .doOnError(error ->
            LOGGER.error("Can't deploy {}: {}", module.toDescriptorLine(), error))
        .onErrorResumeNext((err) ->
            Single.just(new ModuleDescriptor(module).setState(DeploymentState.FAILED)))
        .toObservable();
  }

  private DeploymentOptions getModuleOptions(final JsonObject config, final String module) {
    DeploymentOptions deploymentOptions = new DeploymentOptions();
    if (config.containsKey(CONFIG_OVERRIDE)) {
      if (config.getJsonObject(CONFIG_OVERRIDE).containsKey(module)) {
        JsonObject moduleConfig = config.getJsonObject(CONFIG_OVERRIDE).getJsonObject(module);
        if (moduleConfig.containsKey(MODULE_OPTIONS)) {
          deploymentOptions.fromJson(moduleConfig.getJsonObject(MODULE_OPTIONS));
        } else {
          LOGGER.warn(
              "Module '{}' has config, but missing 'options' object. "
                  + "Default configuration is to be used", module);
        }
      } else {
        LOGGER.warn("Module '{}' if not configured in the config file. Used default configuration",
            module);
      }
    }
    return deploymentOptions;
  }

  private String buildMessage() {
    return new StringBuilder(System.lineSeparator())
        .append(
            deployedModules.stream()
                .map(item -> String
                    .format("\t\t%s %s [%s]", item.getState(), item.toDescriptorLine(),
                        item.getDeploymentId()))
                .collect(Collectors.joining(System.lineSeparator())))
        .append(System.lineSeparator())
        .toString();
  }

  private void printLogo() {
    // @formatter:off
    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    System.out.println("@@                                  ,,,,,,,,,                                 @@");
    System.out.println("@@                                *,,,,,,,,,,,*                               @@");
    System.out.println("@@                              @@&,,,,,,,,,,,,,*                             @@");
    System.out.println("@@                            @@@@@@,,,,,,,,,,,,,,*                           @@");
    System.out.println("@@                          @@@@@@@@@,,,,,,,,,,,,,,,*                         @@");
    System.out.println("@@                        @@@@@@@@@@@@/,,,,,,,,,,,,,,,*                       @@");
    System.out.println("@@                      @@@@@@@@@@@@@@@#,,,,,,,,,,,,,,,,*                     @@");
    System.out.println("@@                    &@@@@@@@@@@@@@@@@@&,,,,,,,,,,,,,,*@@#                   @@");
    System.out.println("@@                  @@&,@@@@@@@@@@@@@@@@@@,,,,,,,,,,,,(@@@@@#                 @@");
    System.out.println("@@                @@@@&,,%@@@@@@@@@@@@@@@@@*,,,,,,,,,%@@@@@@@@#               @@");
    System.out.println("@@              @@@@@@&,,,*@@@@@@@@@@@@@@@@@(,,,,,,,@@@@@@@@@@@@#             @@");
    System.out.println("@@            @@@@@@@@&,,,,,@@@@@@@@@@@@@@@@@&,,,,,@@@@@@@@@@@@@@@#           @@");
    System.out.println("@@          @@@@@@@@@@&,,,,,,%@@@@@@@@@@@@@@@@@,,/@@@@@@@@@@@@@@@@@,*         @@");
    System.out.println("@@        @@@@@@@@@@@@&,,,,,,,*@@@@@@@@@@@@@@@@@%@@@@@@@@@@@@@@@@#,,,,*       @@");
    System.out.println("@@      ,@@@@@@@@@@@@@&,,,,,,,,,@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*,,,,,,,*     @@");
    System.out.println("@@    ,,,@@@@@@@@@@@@@&,,,,,,,,,,#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@,,,,,,,,,,,*   @@");
    System.out.println("@@  *,,,,#@@@@@@@@@@@@&,,,,,,,,,,,*@@@@@@@@@@@@@@@@@@@@@@@@@@(,,,,,,,,,,,,,,* @@");
    System.out.println("@@ ,,,,,,*@@@@@@@@@@@@&,,,,,,,,,,,,,@@@@@@@@@@@@@@@@@@@@@@@@,,,,,,,,,,,,,,,,,*@@");
    System.out.println("@@,,,,,,,,@@@@@@@@@@@@&,,,,,,,,,,,,,,(@@@@@@@@@@@@@@@@@@@@&,,,,,,,,,,,,,,,,,,,@@");
    System.out.println("@@/,,,,,,,&@@@@@@@@@@@&,,,,,,,,,,,,,,,*@@@@@@@@@@@@@@@@@@@/,,,,,,,,,,,,,,,,,,,@@");
    System.out.println("@@ *,,,,,,(@@@@@@@@@@@&,,,,,,,,,,,,,,#@@@@@@@@@@@@@@@@@@@@@&,,,,,,,,,,,,,,,,, @@");
    System.out.println("@@   ,,,,,*@@@@@@@@@@@&,,,,,,,,,,,,,@@@@@@@@@@@@@@@@@@@@@@@@@,,,,,,,,,,,,,,*  @@");
    System.out.println("@@     ,,,,@@@@@@@@@@@&,,,,,,,,,,,*@@@@@@@@@@@@@@@@@@@@@@@@@@@(,,,,,,,,,,*    @@");
    System.out.println("@@       ,,,,,,,,,,,,,,,,,,,,,,,,%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@,,,,,,,*      @@");
    System.out.println("@@         ,,&@@@@@@@%,,,,,,,,,,@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*,,,*        @@");
    System.out.println("@@           @@@@@@@@@@*,,,,,,/@@@@@@@@@@@@@@@@@*@@@@@@@@@@@@@@@@@#*          @@");
    System.out.println("@@             @@@@@@@@@,,,,,&@@@@@@@@@@@@@@@@#,,,@@@@@@@@@@@@@@@@            @@");
    System.out.println("@@               @@@@@@@,,,,@@@@@@@@@@@@@@@@@*,,,,,%@@@@@@@@@@@@              @@");
    System.out.println("@@                 @@@@*,,(@@@@@@@@@@@@@@@@@,,,,,,,,/@@@@@@@@@                @@");
    System.out.println("@@                   @,,,&@@@@@@@@@@@@@@@@&,,,,,,,,,,,@@@@@@                  @@");
    System.out.println("@@                     *@@@@@@@@@@@@@@@@@(,,,,,,,,,,,,,&@@                    @@");
    System.out.println("@@                       @@@@@@@@@@@@@@@*,,,,,,,,,,,,,,*                      @@");
    System.out.println("@@                         @@@@@@@@@@@@,,,,,,,,,,,,,,*                        @@");
    System.out.println("@@                           @@@@@@@@%,,,,,,,,,,,,,*                          @@");
    System.out.println("@@                            @@@@@/,,,,,,,,,,,,*                             @@");
    System.out.println("@@                               @@,,,,,,,,,,,,*                              @@");
    System.out.println("@@                                 *,,,,,,,,,*                                @@");
    System.out.println("@@                                    ,,,*/                                   @@");
    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    System.out.println();
    // @formatter:on
  }
}
