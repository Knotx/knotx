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

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.Set;
import java.util.stream.Collectors;

public class KnotxServerConfiguration {

  //Default limit=-1 - Unlimited size
  private final static long DEFAULT_UPLOAD_LIMIT = -1;

  private static final Long DEFAULT_REQUEST_BUFFER_SIZE = 100L;

  private final Long fileUploadLimit;

  private boolean displayExceptionDetails;

  private Set<String> allowedResponseHeaders;

  private KnotxFlowConfiguration defaultFlow;

  private KnotxFlowConfiguration customFlow;

  private String fileUploadDirectory;

  private JsonObject serverOptions;

  private DeliveryOptions deliveryOptions;

  private JsonObject customResponseHeader;

  private KnotxCSRFConfig csrfConfig;

  private AccessLogConfig accessLogConfig;

  private Long requestsBufferSize;

  public KnotxServerConfiguration(JsonObject config) {
    displayExceptionDetails = config.getBoolean("displayExceptionDetails", false);

    customResponseHeader = config
        .getJsonObject("customResponseHeader", new JsonObject());

    allowedResponseHeaders = config.getJsonArray("allowedResponseHeaders").stream()
        .map(item -> ((String) item).toLowerCase())
        .collect(Collectors.toSet());

    defaultFlow = new KnotxFlowConfiguration(config.getJsonObject("defaultFlow"));
    customFlow = new KnotxFlowConfiguration(config.getJsonObject("customFlow"));
    fileUploadDirectory = config
        .getString("fileUploadDirectory", BodyHandler.DEFAULT_UPLOADS_DIRECTORY);
    fileUploadLimit = config.getLong("fileUploadLimit", DEFAULT_UPLOAD_LIMIT);
    serverOptions = config.getJsonObject("serverOptions", new JsonObject());
    deliveryOptions =
        config.containsKey("deliveryOptions") ? new DeliveryOptions(
            config.getJsonObject("deliveryOptions"))
            : new DeliveryOptions();
    csrfConfig = new KnotxCSRFConfig(config.getJsonObject("csrf", new JsonObject()));

    accessLogConfig = new AccessLogConfig(config.getJsonObject("accessLog", new JsonObject()));
    requestsBufferSize = config.getLong("requestsBufferSize", DEFAULT_REQUEST_BUFFER_SIZE);
  }

  public boolean displayExceptionDetails() {
    return displayExceptionDetails;
  }

  public JsonObject getCustomResponseHeader() {
    return customResponseHeader;
  }

  public Set<String> getAllowedResponseHeaders() {
    return allowedResponseHeaders;
  }

  public KnotxFlowConfiguration getDefaultFlow() {
    return defaultFlow;
  }

  public KnotxFlowConfiguration getCustomFlow() {
    return customFlow;
  }

  public String getFileUploadDirectory() {
    return fileUploadDirectory;
  }

  public JsonObject getServerOptions() {
    return serverOptions;
  }

  public DeliveryOptions getDeliveryOptions() {
    return deliveryOptions;
  }

  public KnotxCSRFConfig getCsrfConfig() {
    return csrfConfig;
  }

  public Long getFileUploadLimit() {
    return fileUploadLimit;
  }

  public AccessLogConfig getAccessLogConfig() {
    return accessLogConfig;
  }

  public Long getRequestsBufferSize() {
    return requestsBufferSize;
  }
}
