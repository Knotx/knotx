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
package io.knotx.junit.rule;

import io.knotx.junit.util.FileReader;
import io.knotx.launcher.KnotxStarterVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.impl.Helper;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.rxjava.core.Vertx;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TestVertxDeployer implements TestRule {

  private RunTestOnContext vertxContext;

  public TestVertxDeployer(RunTestOnContext vertxContext) {
    this.vertxContext = vertxContext;
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        KnotxConfiguration knotxConfig = description.getAnnotation(KnotxConfiguration.class);
        if (knotxConfig == null || knotxConfig.value().isEmpty()) {
          throw new IllegalArgumentException(
              "Missing @KnotxConfiguration annotation with the path to configuration JSON");
        }
        Vertx vertx = Vertx.newInstance(vertxContext.vertx());

        CompletableFuture<String> toComplete = new CompletableFuture<>();

        vertx.deployVerticle(KnotxStarterVerticle.class.getName(),
            new DeploymentOptions().setConfig(readJson(knotxConfig.value())), ar -> {
              if (ar.succeeded()) {
                toComplete.complete("Success");
              } else {
                toComplete.completeExceptionally(ar.cause());
              }
            });

        try {
          toComplete.get();
        } catch (ExecutionException ignore) {
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          Helper.uncheckedThrow(e);
        }

        base.evaluate();
      }
    };
  }

  private JsonObject readJson(String path) throws IOException {
    return new JsonObject(FileReader.readText(path));
  }
}
