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

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.CSRFHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

@DataObject(generateConverter = true, publicConverter = false)
public class KnotxCSRFOptions {

  /**
   * Default timeout of the CSRF Token = 30 minues
   */
  private static final long DEFAULT_TIMEOUT = 30 * 60 * 1000; // 30 minutes

  private String cookieName;
  private String cookiePath;
  private String headerName;
  private String secret;
  private long timeout;

  /**
   * Default constructor
   */
  public KnotxCSRFOptions() {
    init();
  }

  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public KnotxCSRFOptions(KnotxCSRFOptions other) {
    this.cookieName = other.cookieName;
    this.cookiePath = other.cookiePath;
    this.headerName = other.headerName;
    this.secret = other.secret;
    this.timeout = other.timeout;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public KnotxCSRFOptions(JsonObject json) {
    init();
    KnotxCSRFOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    KnotxCSRFOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    secret = RandomStringUtils.random(20);
    cookieName = CSRFHandler.DEFAULT_COOKIE_NAME;
    cookiePath = CSRFHandler.DEFAULT_COOKIE_PATH;
    headerName = CSRFHandler.DEFAULT_HEADER_NAME;
    timeout = DEFAULT_TIMEOUT;
  }

  /**
   * @return name of the CSRF cookie
   */
  public String getCookieName() {
    return cookieName;
  }

  /**
   * Set the name of the CSRF cookie
   *
   * @param cookieName name of the cookie
   * @return reference to this, so the API can be used fluently
   */
  public KnotxCSRFOptions setCookieName(String cookieName) {
    this.cookieName = cookieName;
    return this;
  }

  /**
   * @return path of the CSRF cookie
   */
  public String getCookiePath() {
    return cookiePath;
  }

  /**
   * Set of the path of the CSRF cookie
   *
   * @param cookiePath path of the cookie, e.g. '/'
   * @return reference to this, so the API can be used fluently
   */
  public KnotxCSRFOptions setCookiePath(String cookiePath) {
    this.cookiePath = cookiePath;
    return this;
  }

  /**
   * @return CSRF token header name
   */
  public String getHeaderName() {
    return headerName;
  }

  /**
   * Set the CSRF token header name
   *
   * @param headerName header name
   * @return reference to this, so the API can be used fluently
   */
  public KnotxCSRFOptions setHeaderName(String headerName) {
    this.headerName = headerName;
    return this;
  }

  /**
   * @return secret used to generate CSRF token
   */
  public String getSecret() {
    return secret;
  }

  /**
   * Set the secret used to generate CSRF token
   *
   * @param secret a secret String
   * @return reference to this, so the API can be used fluently
   */
  public KnotxCSRFOptions setSecret(String secret) {
    this.secret = secret;
    return this;
  }

  /**
   * @return timeout of the CSRF token
   */
  public long getTimeout() {
    return timeout;
  }

  /**
   * Set the timeout of the CSRF token
   *
   * @param timeout a token timeout
   * @return reference to this, so the API can be used fluently
   */
  public KnotxCSRFOptions setTimeout(long timeout) {
    this.timeout = timeout;
    return this;
  }
}
