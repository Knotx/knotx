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
import io.vertx.ext.web.handler.LoggerFormat;

@DataObject(generateConverter = true, publicConverter = false)
public class AccessLogOptions {

  private static final boolean DEFAULT_LOGGER_IMMEDIATE = false;
  private static final LoggerFormat DEFAULT_LOGGER_FORMAT = LoggerFormat.DEFAULT;

  private boolean enabled;
  private boolean immediate;
  private LoggerFormat format;

  /**
   * Default constructor
   */
  public AccessLogOptions() {
    init();
  }

  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public AccessLogOptions(AccessLogOptions other) {
    this.enabled = other.enabled;
    this.immediate = other.immediate;
    this.format = other.format;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public AccessLogOptions(JsonObject json) {
    init();
    AccessLogOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    AccessLogOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    enabled = false;
    immediate = DEFAULT_LOGGER_IMMEDIATE;
    format = DEFAULT_LOGGER_FORMAT;
  }

  /**
   * @return is access log is enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Sets if logging access to the Knot.x HTTP server is enabled or not. Default is enabled ('true')
   *
   * @param enabled true if logging access log is enabled
   * @return reference to this, so the API can be used fluently
   */
  public AccessLogOptions setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * @return is access log is collected after request arrives (true), or after sending response (false)
   */
  public boolean isImmediate() {
    return immediate;
  }

  /**
   * Configure when to collect access log. True if after request arrives, false after sending response.
   *
   * @param immediate log access after request arrives (true), or after response sent (false)
   * @return reference to this, so the API can be used fluently
   */
  public AccessLogOptions setImmediate(boolean immediate) {
    this.immediate = immediate;
    return this;
  }

  /**
   * @return format of the access log
   */
  public LoggerFormat getFormat() {
    return format;
  }

  /**
   * Set format of access log. Default is <strong>LoggerFormat.DEFAULT</strong>.
   * Available values are:
   * <ul>
   *   <li>DEFAULT
   *      <i>remote-client</i> - - [<i>timestamp</i>] "<i>method</i> <i>uri</i> <i>version</i>" <i>status</i> <i>content-length</i> "<i>referrer</i>" "<i>user-agent</i>
   *   </li>
   *   <li>SHORT
   *      <i>remote-client</i> - <i>method</i> <i>uri</i> <i>version</i> <i>status</i> <i>content-length</i> <i>duration</i> ms
   *   </li>
   *   <li>TINY
   *      <i>method</i> <i>uri</i> <i>status</i> - <i>content-length</i> <i>duration</i>
   *   </li>
   * </ul>
   *
   * @param format format of the access log
   * @return reference to this, so the API can be used fluently
   */
  public AccessLogOptions setFormat(LoggerFormat format) {
    this.format = format;
    return this;
  }
}
