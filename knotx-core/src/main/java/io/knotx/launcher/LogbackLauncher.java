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

import io.vertx.core.Launcher;
import io.vertx.core.impl.launcher.commands.ExecUtils;
import io.vertx.core.json.JsonObject;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class LogbackLauncher extends Launcher {

  /**
   * Error code used when the knotx configuration is missing or it's empty
   */
  public static final int KNOTX_MISSING_OR_EMPTY_CONFIGURATION_EXIT_CODE = 30;

  public static void main(String[] args){
    try {
      System.setOut(new PrintStream("/Users/marcin.czeczko/Cognifide/Projects/knotx/knotx-example/knotx-example-app/stdout.log"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    System.setProperty("vertx.logger-delegate-factory-class-name",
        "io.vertx.core.logging.SLF4JLogDelegateFactory");
    new LogbackLauncher().dispatch(args);
  }

  @Override
  public void afterConfigParsed(JsonObject config) {
    if (config.isEmpty()) {
      ExecUtils.exit(KNOTX_MISSING_OR_EMPTY_CONFIGURATION_EXIT_CODE);
    }
  }
}
