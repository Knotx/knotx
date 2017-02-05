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

import io.knotx.util.JsonObjectUtil;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.VerticleFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class KnotxModuleVerticleFactory implements VerticleFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxModuleVerticleFactory.class);
  private static final String CONFIG_KEY = "config";
  private static final String OPTIONS_KEY = "options";

  @Override
  public boolean requiresResolve() {
    return true;
  }

  @Override
  public void resolve(String id, DeploymentOptions deploymentOptions, ClassLoader classLoader,
      Future<String> resolution) {
    String identifier = VerticleFactory.removePrefix(id);
    String descriptorFile = identifier + ".json";
    try {
      JsonObject descriptor = readDescriptor(classLoader, descriptorFile);
      String main = readVerticleMainClass(descriptor, descriptorFile);

      // Any options specified in the module config will override anything specified at deployment time
      // Options and Config specified in knotx starter JSON will override those configurations
      JsonObject depOptions = deploymentOptions.toJson();
      JsonObject depConfig = depOptions.getJsonObject(CONFIG_KEY, new JsonObject());

      JsonObject knotOptions = descriptor.getJsonObject(OPTIONS_KEY, new JsonObject());
      JsonObject knotConfig = knotOptions.getJsonObject(CONFIG_KEY, new JsonObject());
      depOptions.mergeIn(knotOptions);
      knotConfig = JsonObjectUtil.deepMerge(knotConfig, depConfig);

      // Any options or config provided by system properties will override anything specified
      // at deployment time and on starter Json config
      overrideConfigWithSystemProperties(identifier, descriptor, depOptions, knotConfig);

      depOptions.put(CONFIG_KEY, knotConfig);
      deploymentOptions.fromJson(depOptions);
      resolution.complete(main);
    } catch (Exception e) {
      resolution.fail(e);
    }
  }

  private void overrideConfigWithSystemProperties(String identifier, JsonObject descriptor,
      JsonObject depOptions, JsonObject knotConfig) {
    try {
      SystemPropsConfiguration systemPropsConfiguration = new SystemPropsConfiguration(
          identifier);
      if (!systemPropsConfiguration.envConfig().isEmpty()) {
        JsonObject updatedDescriptor = systemPropsConfiguration.updateJsonObject(descriptor);
        JsonObject updatedKnotOptions = updatedDescriptor
            .getJsonObject(OPTIONS_KEY, new JsonObject());
        JsonObject updatedKnotConfig = updatedKnotOptions
            .getJsonObject(CONFIG_KEY, new JsonObject());
        depOptions.mergeIn(updatedKnotOptions);
        knotConfig.mergeIn(updatedKnotConfig);
      }
    } catch (IllegalArgumentException ex) {
      LOGGER.warn("Unable to parse given system properties due to exception", ex);
    }
  }

  @Override
  public String prefix() {
    return "knotx";
  }

  private String readVerticleMainClass(JsonObject descriptor, String descriptorFile) {
    String main = descriptor.getString("main");
    if (main == null) {
      throw new IllegalArgumentException(descriptorFile + " does not contain a main field");
    }
    return main;
  }

  @Override
  public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
    throw new IllegalStateException("Shouldn't be called");
  }

  private JsonObject readDescriptor(ClassLoader classLoader, String descriptorFile)
      throws IOException {
    JsonObject descriptor;
    try (InputStream is = classLoader.getResourceAsStream(descriptorFile)) {
      if (is == null) {
        throw new IllegalArgumentException(
            "Cannot find module descriptor file " + descriptorFile + " on classpath");
      }
      try (Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A")) {
        String conf = scanner.next();
        descriptor = new JsonObject(conf);
      } catch (NoSuchElementException e) {
        throw new IllegalArgumentException(descriptorFile + " is empty", e);
      } catch (DecodeException e) {
        throw new IllegalArgumentException(descriptorFile + " contains invalid json", e);
      }
    }

    return descriptor;
  }

}
