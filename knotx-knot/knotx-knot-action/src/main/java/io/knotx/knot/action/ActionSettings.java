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
package io.knotx.knot.action;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Describes a physical details of HTTP service endpoint that consumes form submitions
 * from AdapterServiceKnot.
 */
@DataObject(generateConverter = true, publicConverter = false)
public class ActionSettings {

  private String name;

  private String address;

  private JsonObject params;

  private List<String> allowedRequestHeaders;

  private List<String> allowedResponseHeaders;

  private List<Pattern> allowedRequestHeadersPatterns;

  private List<Pattern> allowedResponseHeadersPatterns;

  /**
   * Default constructor
   */
  public ActionSettings() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public ActionSettings(ActionSettings other) {
    this.name = other.name;
    this.address = other.address;
    this.params = other.params.copy();
    this.allowedRequestHeaders = new ArrayList<>(other.allowedRequestHeaders);
    this.allowedResponseHeaders = new ArrayList<>(other.allowedResponseHeaders);
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public ActionSettings(JsonObject json) {
    init();
    ActionSettingsConverter.fromJson(json, this);
    allowedRequestHeadersPatterns = allowedRequestHeaders.stream().map(Pattern::compile)
        .collect(Collectors.toList());
    allowedResponseHeadersPatterns = allowedResponseHeaders.stream().map(Pattern::compile)
        .collect(Collectors.toList());
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ActionSettingsConverter.toJson(this, json);
    return json;
  }

  private void init() {

  }


  /**
   * @return a name of the service
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of the service the will be used on html snippet level.
   *
   * @param name name of the service
   * @return a reference to this, so the API can be used fluently
   */
  public ActionSettings setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return EB address of the service adapter
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the EB address of the service adapter
   *
   * @param address EB address of the service adapter
   * @return a reference to this, so the API can be used fluently
   */
  public ActionSettings setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * @return a JsonObject with service parameters
   */
  public JsonObject getParams() {
    return params;
  }

  /**
   * Set the service parameters to be consumed by the adapter.
   *
   * @param params JsonObject with the additional parameters
   * @return a reference to this, so the API can be used fluently
   */
  public ActionSettings setParams(JsonObject params) {
    this.params = params;
    return this;
  }

  /**
   * A list of allowed request header names
   *
   * @return a reference to this, so the API can be used fluently
   */
  public List<String> getAllowedRequestHeaders() {
    return allowedRequestHeaders;
  }

  /**
   * Sets list of HTTP client request headers that are allowed to be passed to Adapter. No request headers are allowed if not set.
   *
   * @param allowedRequestHeaders list of header names
   * @return a reference to this, so the API can be used fluently
   */
  public ActionSettings setAllowedRequestHeaders(List<String> allowedRequestHeaders) {
    this.allowedRequestHeaders = allowedRequestHeaders;
    allowedRequestHeadersPatterns = allowedRequestHeaders.stream().map(Pattern::compile)
        .collect(Collectors.toList());
    return this;
  }

  /**
   * @return a list of allowed response header names
   */
  public List<String> getAllowedResponseHeaders() {
    return allowedResponseHeaders;
  }

  /**
   * Sets list of HTTP response headers that are allowed to be sent in a client response. No response headers are allowed if not set.
   *
   * @param allowedResponseHeaders list of header names
   * @return a reference to this, so the API can be used fluently
   */
  public ActionSettings setAllowedResponseHeaders(List<String> allowedResponseHeaders) {
    this.allowedResponseHeaders = allowedResponseHeaders;
    allowedResponseHeadersPatterns = allowedResponseHeaders.stream().map(Pattern::compile)
        .collect(Collectors.toList());
    return this;
  }

  @GenIgnore
  public List<Pattern> getAllowedRequestHeadersPatterns() {
    return allowedRequestHeadersPatterns;
  }

  @GenIgnore
  public List<Pattern> getAllowedResponseHeadersPatterns() {
    return allowedResponseHeadersPatterns;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ActionSettings that = (ActionSettings) o;
    return Objects.equal(name, that.name) &&
        Objects.equal(address, that.address) &&
        Objects.equal(params, that.params) &&
        Objects.equal(allowedRequestHeaders, that.allowedRequestHeaders) &&
        Objects.equal(allowedResponseHeaders, that.allowedResponseHeaders);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, address, params, allowedRequestHeaders, allowedResponseHeaders);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", name)
        .add("address", address)
        .add("params", params)
        .add("allowedRequestHeaders", allowedRequestHeaders)
        .add("allowedResponseHeaders", allowedResponseHeaders)
        .toString();
  }
}
