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
package io.knotx.server.configuration;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.LoggerFormat;

public class AccessLogConfig {

  private static final boolean DEFAULT_ENABLED = true;
  private static final boolean DEFAULT_LOGGER_IMMEDIATE = false;
  private static final String DEFAULT_LOGGER_FORMAT = LoggerFormat.DEFAULT.toString();

  private boolean enabled;
  private boolean immediate;
  private LoggerFormat format;

  public AccessLogConfig(JsonObject config) {
    enabled = config.getBoolean("enabled", DEFAULT_ENABLED);
    immediate = config.getBoolean("immediate", DEFAULT_LOGGER_IMMEDIATE);
    format = LoggerFormat
        .valueOf(config.getString("format", DEFAULT_LOGGER_FORMAT).toUpperCase());
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isImmediate() {
    return immediate;
  }

  public LoggerFormat getFormat() {
    return format;
  }
}
