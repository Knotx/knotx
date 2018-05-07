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
package io.knotx.adapter.common.http;

import com.google.common.collect.Lists;
import io.knotx.http.StringToPatternFunction;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.stream.Collectors;

public class HttpAdapterConfiguration {

  private JsonObject customRequestHeader;

  private String address;

  private List<ServiceMetadata> services;

  private JsonObject clientOptions;

  public HttpAdapterConfiguration() {
    customRequestHeader = new JsonObject();
    services = Lists.newArrayList();
    clientOptions = new HttpClientOptions().toJson();
  }

  public HttpAdapterConfiguration(JsonObject config) {
    address = config.getString("address");
    services = config.getJsonArray("services").stream()
        .map(item -> (JsonObject) item)
        .map(item -> {
          ServiceMetadata metadata = new ServiceMetadata();
          metadata.setPath(item.getString("path"));
          metadata.setDomain(item.getString("domain"));
          metadata.setPort(item.getInteger("port"));
          metadata.setAllowedRequestHeaderPatterns(item
              .getJsonArray("allowedRequestHeaders", new JsonArray()).stream()
              .map(object -> (String) object)
              .map(StringToPatternFunction.getInstance())
              .collect(Collectors.toList())
          );

          return metadata;
        }).collect(Collectors.toList());
    clientOptions = config.getJsonObject("clientOptions", new JsonObject());
    customRequestHeader = config.getJsonObject("customRequestHeader", new JsonObject());
  }

  public HttpAdapterConfiguration setAddress(String address) {
    this.address = address;
    return this;
  }

  public HttpAdapterConfiguration setServices(List<ServiceMetadata> services) {
    this.services = Lists.newArrayList(services);
    return this;
  }

  public HttpAdapterConfiguration setClientOptions(JsonObject clientOptions) {
    this.clientOptions = clientOptions.copy();
    return this;
  }

  public HttpAdapterConfiguration setCustomRequestHeader(JsonObject customRequestHeader) {
    this.customRequestHeader = customRequestHeader.copy();
    return this;
  }

  public JsonObject getClientOptions() {
    return clientOptions;
  }

  public JsonObject getCustomRequestHeader() {
    return customRequestHeader;
  }

  public List<ServiceMetadata> getServices() {
    return services;
  }

  public String getAddress() {
    return address;
  }
}
