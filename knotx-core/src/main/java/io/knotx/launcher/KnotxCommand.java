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
import io.vertx.core.Vertx;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.impl.launcher.VertxLifecycleHooks;
import io.vertx.core.impl.launcher.commands.BareCommand;
import io.vertx.core.impl.launcher.commands.ExecUtils;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.launcher.ExecutionContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

@Name("run-knotx")
@Summary("Runs a Knot.x instance")
public class KnotxCommand extends BareCommand {

  /**
   * Error code used when the knotx configuration is missing or it's empty
   */
  private static final int KNOTX_MISSING_OR_EMPTY_CONFIGURATION_EXIT_CODE = 30;

  private static final String DEFAULT_LOADER_FILE = "bootstrap.json";


  private DeploymentOptions deploymentOptions;

  private String config;

  private boolean ha;
  private boolean cluster;

  private static final String KNOTX_STARTER_VERTICLE = KnotxStarterVerticle.class.getName();


  /**
   * Enables / disables the high-availability.
   *
   * @param ha whether or not to enable the HA.
   */
  @Option(longName = "ha", acceptValue = false, flag = true)
  @Description(
      "If specified the Knot.x will be deployed as a high availability (HA) deployment. This means it can "
          +
          "fail over to any other nodes in the cluster started with the same HA group.")
  public void setHighAvailability(boolean ha) {
    this.ha = ha;
  }

  /**
   * Enables / disables the clustering.
   *
   * @param cluster whether or not to start Knot.x in clustered mode.
   */
  @Option(longName = "cluster", acceptValue = false, flag = true)
  @Description(
      "If specified then the Knot.x instance will form a cluster with any other Knotx.x instances on the "
          +
          "network.")
  public void setCluster(boolean cluster) {
    this.cluster = cluster;
  }

  /**
   * The path to the Knot.x configuration loader json file
   *
   * @param path the path to the confing loader json
   */
  @Option(longName = "conf", argName = "config")
  @Description("Specifies path to the config loader json Knot.x requires to start. " +
      "If not specified a '" + DEFAULT_LOADER_FILE + "' in the class path is to be used.")
  public void setConfig(String path) {
    if (path != null) {
      this.config = path;
    } else {
      this.config = null;
    }
  }

  /**
   * Validates the command line parameters.
   *
   * @param context - the execution context
   * @throws CLIException - validation failed
   */
  @Override
  public void setUp(ExecutionContext context) throws CLIException {
    super.setUp(context);
  }

  /**
   * @return whether the {@code cluster} option or the {@code ha} option are enabled.
   */
  @Override
  public boolean isClustered() {
    return cluster || ha;
  }

  @Override
  public boolean getHA() {
    return ha;
  }

  /**
   * Starts vert.x and deploy the verticle.
   */
  @Override
  public void run() {
    JsonObject conf = getConfiguration();
    if (conf == null) {
      ExecUtils.exit(KNOTX_MISSING_OR_EMPTY_CONFIGURATION_EXIT_CODE);
    }

    afterConfigParsed(conf);

    super.run(this::afterStoppingVertx);
    if (vertx == null) {
      // Already logged.
      ExecUtils.exitBecauseOfVertxInitializationIssue();
    }

    if (vertx instanceof VertxInternal) {
      ((VertxInternal) vertx).addCloseHook(completionHandler -> {
        try {
          beforeStoppingVertx(vertx);
          completionHandler.handle(Future.succeededFuture());
        } catch (Exception e) {
          completionHandler.handle(Future.failedFuture(e));
        }
      });
    }

    deploymentOptions = new DeploymentOptions();
    configureFromSystemProperties(deploymentOptions, DEPLOYMENT_OPTIONS_PROP_PREFIX);
    deploymentOptions.setConfig(conf).setHa(ha);
    beforeDeployingVerticle(deploymentOptions);
    deploy();
  }

  protected void deploy() {
    deploy(KNOTX_STARTER_VERTICLE, vertx, deploymentOptions, res -> {
      if (res.failed()) {
        res.cause().printStackTrace();
        handleDeployFailed(res.cause());
      }
    });
  }

  private void handleDeployFailed(Throwable cause) {
    if (executionContext.main() instanceof VertxLifecycleHooks) {
      ((VertxLifecycleHooks) executionContext.main())
          .handleDeployFailed(vertx, KNOTX_STARTER_VERTICLE, deploymentOptions, cause);
    } else {
      ExecUtils.exitBecauseOfVertxDeploymentIssue();
    }
  }

  protected JsonObject getConfiguration() {
    //If the configuration wasn't provided in the command line
    //search in the classpath
    JsonObject conf;

    if (config == null) {
      URL bootstrap = getClass().getClassLoader().getResource(DEFAULT_LOADER_FILE);
      if (bootstrap != null) {
        log.info("Loading configuration from classpath: {}.", bootstrap.toExternalForm());
        try {
          conf = scanConfig(bootstrap.openStream());
        } catch (IOException e) {
          e.printStackTrace();
          return null;
        }
      } else {
        log.error("The '{}' not found on the classpath", DEFAULT_LOADER_FILE);
        return null;
      }
    } else {
      try {
        File configFile = new File(config);
        log.info("Loading configuration from {}", configFile.toURI());
        conf = scanConfig(new FileInputStream(configFile));
      } catch (FileNotFoundException e) {
        log.error("The -conf option does not point to an existing file");
        e.printStackTrace();
        return null;
      }
    }
    return conf;
  }

  private JsonObject scanConfig(InputStream is) {
    JsonObject conf;
    try (Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A")) {
      String sconf = scanner.next();
      try {
        conf = new JsonObject(sconf);
      } catch (DecodeException e) {
        log.error("Configuration file " + sconf + " does not contain a valid JSON object");
        return null;
      }
    }
    return conf;
  }

  protected void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
    final Object main = executionContext.main();
    if (main instanceof VertxLifecycleHooks) {
      ((VertxLifecycleHooks) main).beforeDeployingVerticle(deploymentOptions);
    }
  }

  protected void afterConfigParsed(JsonObject config) {
    final Object main = executionContext.main();
    if (main instanceof VertxLifecycleHooks) {
      ((VertxLifecycleHooks) main).afterConfigParsed(config);
    }
  }

  protected void beforeStoppingVertx(Vertx vertx) {
    final Object main = executionContext.main();
    if (main instanceof VertxLifecycleHooks) {
      ((VertxLifecycleHooks) main).beforeStoppingVertx(vertx);
    }
  }

  protected void afterStoppingVertx() {
    final Object main = executionContext.main();
    if (main instanceof VertxLifecycleHooks) {
      ((VertxLifecycleHooks) main).afterStoppingVertx();
    }
  }
}
