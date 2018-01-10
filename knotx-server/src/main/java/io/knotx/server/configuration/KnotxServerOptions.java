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

import static io.knotx.server.KnotxServerVerticle.KNOTX_PORT_PROP_NAME;

import com.google.common.collect.Sets;
import io.knotx.configuration.CustomHttpHeader;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@DataObject(generateConverter = true, publicConverter = false)
public class KnotxServerOptions {

  /**
   * The default value for Knot.x HTTP Server port = the value of the system property "knotx.port" or 8092
   */
  public final static int DEFAULT_HTTP_PORT = Integer.getInteger(KNOTX_PORT_PROP_NAME, 8092);
  /**
   * Default name of the custom response header
   */
  public final static String DEFAULT_CUSTOM_RESPONSE_HEADER_NAME = "X-Server";

  /**
   * Default File Upload Limit = -1 (unlimited)
   */
  public final static long DEFAULT_UPLOAD_LIMIT = -1;

  /**
   * Default file upload folder =
   */
  private static final String DEFAULT_UPLOAD_DIRECTORY = BodyHandler.DEFAULT_UPLOADS_DIRECTORY;

  /**
   * Default flag if to show the exceptions on error pages = true
   */
  private static final boolean DEFAULT_DISPLAY_EXCEPTIONS = true;

  /**
   * Default response headers returned by KnotxServer to the client
   */
  private static final Set<String> DEFAULT_RESPONSE_HEADERS = Sets.newHashSet(
      "Access-Control-Allow-Origin",
      "Allow",
      "Cache-Control",
      "Content-Disposition",
      "Content-Encoding",
      "Content-Language",
      "Content-Location",
      "Content-MD5",
      "Content-Range",
      "Content-Type",
      "Content-Length",
      "Content-Security-Policy",
      "Date",
      "Edge-Control",
      "ETag",
      "Expires",
      "Last-Modified",
      "Location",
      "Pragma",
      "Proxy-Authenticate",
      "Server",
      "Set-Cookie",
      "Status",
      "Surrogate-Control",
      "Vary",
      "Via",
      "X-Frame-Options",
      "X-XSS-Protection",
      "X-Content-Type-Options",
      "X-UA-Compatible",
      "X-Request-ID",
      "X-Server"
  );

  private Long fileUploadLimit;
  private String fileUploadDirectory;
  private boolean displayExceptionDetails;
  private HttpServerOptions serverOptions;
  private DeliveryOptions deliveryOptions;
  private KnotxCSRFOptions csrfConfig;
  private KnotxFlowSettings defaultFlow;
  private KnotxFlowSettings customFlow;
  private Set<String> allowedResponseHeaders;
  private CustomHttpHeader customResponseHeader;

  /**
   * Default constructor
   */
  public KnotxServerOptions() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public KnotxServerOptions(KnotxServerOptions other) {
    this.fileUploadLimit = other.fileUploadLimit;
    this.fileUploadDirectory = other.fileUploadDirectory;
    this.displayExceptionDetails = other.displayExceptionDetails;
    this.allowedResponseHeaders = new HashSet<>(other.allowedResponseHeaders);
    this.serverOptions = new HttpServerOptions(other.serverOptions);
    this.deliveryOptions = new DeliveryOptions(other.deliveryOptions);
    this.customResponseHeader = new CustomHttpHeader(other.customResponseHeader);
    this.csrfConfig = new KnotxCSRFOptions(other.csrfConfig);
    this.defaultFlow = new KnotxFlowSettings(other.defaultFlow);
    this.customFlow = new KnotxFlowSettings(other.customFlow);
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public KnotxServerOptions(JsonObject json) {
    init();
    KnotxServerOptionsConverter.fromJson(json, this);
    allowedResponseHeaders = allowedResponseHeaders.stream().map(String::toLowerCase)
        .collect(Collectors.toSet());
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    KnotxServerOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    fileUploadLimit = DEFAULT_UPLOAD_LIMIT;
    fileUploadDirectory = DEFAULT_UPLOAD_DIRECTORY;
    displayExceptionDetails = DEFAULT_DISPLAY_EXCEPTIONS;
    allowedResponseHeaders = DEFAULT_RESPONSE_HEADERS;
    allowedResponseHeaders = allowedResponseHeaders.stream().map(String::toLowerCase)
        .collect(Collectors.toSet());

    serverOptions = new HttpServerOptions()
        .setPort(DEFAULT_HTTP_PORT)
        .setKeyStoreOptions(new JksOptions());
    deliveryOptions = new DeliveryOptions();
    customResponseHeader = new CustomHttpHeader().setName(DEFAULT_CUSTOM_RESPONSE_HEADER_NAME);
    csrfConfig = new KnotxCSRFOptions();
    defaultFlow = new KnotxFlowSettings();
    customFlow = null;
  }

  /**
   * @return limit of the file size when uploading
   */
  public Long getFileUploadLimit() {
    return fileUploadLimit;
  }

  /**
   * Set the file upload limit in bytes
   *
   * @param fileUploadLimit size limit in bytes
   * @return reference to this, so the API can be used fluently
   */
  public KnotxServerOptions setFileUploadLimit(Long fileUploadLimit) {
    this.fileUploadLimit = fileUploadLimit;
    return this;
  }

  /**
   * @return file upload directory
   */
  public String getFileUploadDirectory() {
    return fileUploadDirectory;
  }

  /**
   * Set the location on Knot.x environment when uploaded files will be stored. These must be an absolute path.
   *
   * @param fileUploadDirectory file upload directory
   * @return reference to this, so the API can be used fluently
   */
  public KnotxServerOptions setFileUploadDirectory(String fileUploadDirectory) {
    this.fileUploadDirectory = fileUploadDirectory;
    return this;
  }

  /**
   * @return whether to display or not exceptions on error pages
   */
  public boolean isDisplayExceptionDetails() {
    return displayExceptionDetails;
  }

  /**
   * Set whether to display or not the exception on error pages
   *
   * @param displayExceptionDetails displays exceptions on error pages if true
   * @return reference to this, so the API can be used fluently
   */
  public KnotxServerOptions setDisplayExceptionDetails(boolean displayExceptionDetails) {
    this.displayExceptionDetails = displayExceptionDetails;
    return this;
  }

  /**
   * @return {@link io.vertx.core.http.HttpServerOptions}
   */
  public HttpServerOptions getServerOptions() {
    return serverOptions;
  }

  /**
   * Set the HTTP Server options
   *
   * @param serverOptions {@link io.vertx.core.http.HttpServerOptions} object
   * @return reference to this, so the API can be used fluently
   */
  public KnotxServerOptions setServerOptions(HttpServerOptions serverOptions) {
    this.serverOptions = serverOptions;
    return this;
  }

  /**
   * @return a {@link io.vertx.core.eventbus.DeliveryOptions}
   */
  public DeliveryOptions getDeliveryOptions() {
    return deliveryOptions;
  }

  /**
   * Set the Event Bus Delivery options used to communicate with Knot's
   *
   * @param deliveryOptions {@link io.vertx.core.eventbus.DeliveryOptions} object
   * @return reference to this, so the API can be used fluently
   */
  public KnotxServerOptions setDeliveryOptions(DeliveryOptions deliveryOptions) {
    this.deliveryOptions = deliveryOptions;
    return this;
  }

  /**
   * @return {@link KnotxCSRFOptions}
   */
  public KnotxCSRFOptions getCsrfConfig() {
    return csrfConfig;
  }

  /**
   * Set the CSRF configuration of the Knot.x server
   *
   * @param csrfConfig {@link KnotxCSRFOptions} object
   * @return reference to this, so the API can be used fluently
   */
  public KnotxServerOptions setCsrfConfig(KnotxCSRFOptions csrfConfig) {
    this.csrfConfig = csrfConfig;
    return this;
  }

  /**
   * @return a {@link KnotxFlowSettings} for a default flow
   */
  public KnotxFlowSettings getDefaultFlow() {
    return defaultFlow;
  }

  /**
   * Set the Default flow configuration
   *
   * @param defaultFlow a configuration of default flow
   * @return reference to this, so the API can be used fluently
   */
  public KnotxServerOptions setDefaultFlow(KnotxFlowSettings defaultFlow) {
    this.defaultFlow = defaultFlow;
    return this;
  }

  /**
   * @return {@link KnotxFlowSettings} of the custom flow
   */
  public KnotxFlowSettings getCustomFlow() {
    return customFlow;
  }

  /**
   * Set the Custom Flow configuration
   *
   * @param customFlow a configuration og the custom flow
   * @return reference to this, so the API can be used fluently
   */
  public KnotxServerOptions setCustomFlow(KnotxFlowSettings customFlow) {
    this.customFlow = customFlow;
    return this;
  }

  /**
   * @return Set of response headers that Knot.x can return
   */
  public Set<String> getAllowedResponseHeaders() {
    return allowedResponseHeaders;
  }

  /**
   * Set the set of response headers that can be returned by the Knot.x server
   *
   * @param allowedResponseHeaders a set of response headers
   * @return reference to this, so the API can be used fluently
   */
  public KnotxServerOptions setAllowedResponseHeaders(
      Set<String> allowedResponseHeaders) {
    this.allowedResponseHeaders = allowedResponseHeaders.stream().map(String::toLowerCase)
        .collect(Collectors.toSet());
    return this;
  }

  /**
   * @return a configuration of custom response header returned by the Knot.x server
   */
  public CustomHttpHeader getCustomResponseHeader() {
    return customResponseHeader;
  }

  /**
   * Set the custom response header returned by the Knot.x
   *
   * @param customResponseHeader a {@link KnotxServerOptions} object
   * @return reference to this, so the API can be used fluently
   */
  public KnotxServerOptions setCustomResponseHeader(
      CustomHttpHeader customResponseHeader) {
    this.customResponseHeader = customResponseHeader;
    return this;
  }
}
