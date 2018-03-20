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
package io.knotx.adapter.common.configuration;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Describes a physical details of HTTP service endpoint the ServiceAdapter will use.
 */
@DataObject(generateConverter = true, publicConverter = false)
public class ServiceSettings {

  private String path;
  private String domain;
  private int port;
  private Set<String> allowedRequestHeaders;
  private JsonObject queryParams;
  private JsonObject additionalHeaders;

  private List<Pattern> allowedRequestHeadersPatterns;

  /**
   * Default constructor
   */
  public ServiceSettings() {
    //empty constructor
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public ServiceSettings(ServiceSettings other) {
    this.path = other.path;
    this.domain = other.domain;
    this.port = other.port;
    this.allowedRequestHeaders = new HashSet<>(other.allowedRequestHeaders);
    this.allowedRequestHeadersPatterns = new ArrayList<>(other.allowedRequestHeadersPatterns);
    this.queryParams = other.queryParams.copy();
    this.additionalHeaders = other.additionalHeaders.copy();
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public ServiceSettings(JsonObject json) {
    this();
    ServiceSettingsConverter.fromJson(json, this);
    if (allowedRequestHeaders != null) {
      allowedRequestHeadersPatterns = allowedRequestHeaders.stream()
          .map(expr -> Pattern.compile(expr)).collect(Collectors.toList());
    }
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ServiceSettingsConverter.toJson(this, json);
    return json;
  }

  public String getPath() {
    return path;
  }

  public ServiceSettings setPath(String path) {
    this.path = path;
    return this;
  }

  /**
   * @return a domain of the external service
   */
  public String getDomain() {
    return domain;
  }

  /**
   * Set the {@code domain} of the external service
   *
   * @return a reference to this, so the API can be used fluently
   */
  public ServiceSettings setDomain(String domain) {
    this.domain = domain;
    return this;
  }

  /**
   * @return HTTP port of the external service
   */
  public int getPort() {
    return port;
  }

  /**
   * Sets the HTTP {@code port} the external service
   *
   * @param port - HTTP port
   * @return a reference to this, so the API can be used fluently
   */
  public ServiceSettings setPort(int port) {
    this.port = port;
    return this;
  }

  /**
   * @return Set of allowed request headers that should be passed-through to the service
   */
  public Set<String> getAllowedRequestHeaders() {
    return allowedRequestHeaders;
  }

  /**
   * Sets the allowed requests headers that should be send to the service.
   * The selected headers from the original client HTTP request are being send.
   *
   * @param allowedRequestHeaders set of Strings with header names
   * @return a reference to this, so the API can be used fluently
   */
  public ServiceSettings setAllowedRequestHeaders(Set<String> allowedRequestHeaders) {
    this.allowedRequestHeaders = allowedRequestHeaders;
    allowedRequestHeadersPatterns = allowedRequestHeaders.stream()
        .map(expr -> Pattern.compile(expr)).collect(Collectors.toList());
    return this;
  }

  /**
   * @return a JsonObject with additional query parameters
   */
  public JsonObject getQueryParams() {
    return queryParams;
  }

  /**
   * Set the additional request query parameters to be send in each request
   *
   * @return a reference to this, so the API can be used fluently
   */
  public ServiceSettings setQueryParams(JsonObject queryParams) {
    this.queryParams = queryParams;
    return this;
  }

  /**
   * @return a Json Object with additional headers and it's values
   */
  public JsonObject getAdditionalHeaders() {
    return additionalHeaders;
  }

  /**
   * Set the additional request headers (and values) to be send in each request
   *
   * @return a reference to this, so the API can be used fluently
   */
  public ServiceSettings setAdditionalHeaders(JsonObject additionalHeaders) {
    this.additionalHeaders = additionalHeaders;
    return this;
  }

  @GenIgnore
  public List<Pattern> getAllowedRequestHeadersPatterns() {
    return allowedRequestHeadersPatterns;
  }

  @GenIgnore
  public ServiceSettings setAllowedRequestHeaderPatterns(List<Pattern> allowedRequestHeaderPatterns) {
    this.allowedRequestHeadersPatterns = allowedRequestHeaderPatterns;
    return this;
  }
}
