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
 * Describes a physical details of HTTP service endpoint the ServiceAdapter will use.
 */
@DataObject(generateConverter = true, publicConverter = false)
public class ActionAdapterMetadata {

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
  public ActionAdapterMetadata() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public ActionAdapterMetadata(ActionAdapterMetadata other) {
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
  public ActionAdapterMetadata(JsonObject json) {
    init();
    ActionAdapterMetadataConverter.fromJson(json, this);
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
    ActionAdapterMetadataConverter.toJson(this, json);
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
  public ActionAdapterMetadata setName(String name) {
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
  public ActionAdapterMetadata setAddress(String address) {
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
  public ActionAdapterMetadata setParams(JsonObject params) {
    this.params = params;
    return this;
  }

  public List<String> getAllowedRequestHeaders() {
    return allowedRequestHeaders;
  }

  public ActionAdapterMetadata setAllowedRequestHeaders(List<String> allowedRequestHeaders) {
    this.allowedRequestHeaders = allowedRequestHeaders;
    allowedRequestHeadersPatterns = allowedRequestHeaders.stream().map(Pattern::compile)
        .collect(Collectors.toList());
    return this;
  }

  public List<String> getAllowedResponseHeaders() {
    return allowedResponseHeaders;
  }

  public ActionAdapterMetadata setAllowedResponseHeaders(List<String> allowedResponseHeaders) {
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
    ActionAdapterMetadata that = (ActionAdapterMetadata) o;
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
