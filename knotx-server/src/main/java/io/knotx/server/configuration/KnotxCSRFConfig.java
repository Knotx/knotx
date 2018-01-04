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
import io.vertx.ext.web.handler.CSRFHandler;


public class KnotxCSRFConfig {

  private static final long DEFAULT_TIMEOUT = 30 * 60 * 1000; // 30 minutes

  private static final String DEFAULT_SECRET = "a31DDUam$D{h{x5*ah2j0,EQhkA`Su";

  private String cookieName;

  private String cookiePath;

  private String headerName;

  private String secret;

  private long timeout;

  public KnotxCSRFConfig(JsonObject config) {
    secret = config.getString("secret", DEFAULT_SECRET);
    cookieName = config.getString("cookieName", CSRFHandler.DEFAULT_HEADER_NAME);
    cookiePath = config.getString("cookiePath", CSRFHandler.DEFAULT_COOKIE_PATH);
    headerName = config.getString("headerName", CSRFHandler.DEFAULT_HEADER_NAME);
    timeout = config.getLong("timeout", DEFAULT_TIMEOUT);
  }

  public String getCookieName() {
    return cookieName;
  }

  public String getCookiePath() {
    return cookiePath;
  }

  public String getHeaderName() {
    return headerName;
  }

  public String getSecret() {
    return secret;
  }

  public long getTimeout() {
    return timeout;
  }
}
