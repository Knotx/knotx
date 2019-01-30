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

import io.vertx.core.spi.launcher.DefaultCommandFactory;

/**
 * Factory to create the {@code run-knotx} command.
 */
public class KnotxCommandFactory extends DefaultCommandFactory<KnotxCommand> {

  /**
   * Creates a new instance of {@link KnotxCommandFactory}.
   */
  public KnotxCommandFactory() {
    super(KnotxCommand.class);
  }
}
