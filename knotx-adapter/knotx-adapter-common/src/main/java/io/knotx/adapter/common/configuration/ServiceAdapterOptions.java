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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.knotx.configuration.CustomRequestHeader;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import java.util.ArrayList;
import java.util.List;

/**
 * Options describing how an ServiceAdapter will make connections with external HTTP services.
 */
@DataObject(generateConverter = true, publicConverter = false)
public class ServiceAdapterOptions {

  public final static String DEFAULT_ADDRESS = "knotx.adapter.service.http";

  public final static int DEFAULT_WEBCLIENT_MAX_POOL_SIZE = 1000;

  public final static boolean DEFAULT_WEBCLIENT_KEEP_ALIVE = false;

  public final static List<ServiceSettings> DEFAULT_SERVICES =
      Lists.newArrayList(
          new ServiceSettings().setPath("/service/mock/.*")
              .setDomain("localhost")
              .setPort(3000)
              .setAllowedRequestHeaders(Sets.newHashSet("Accept*", "Content-*", "X-*")),
          new ServiceSettings()
              .setPath("/service/.*")
              .setDomain("localhost")
              .setPort(8080)
              .setAllowedRequestHeaders(Sets.newHashSet("Accept*", "Content-*", "X-*"))
      );

  private String address;
  private WebClientOptions clientOptions;
  private List<ServiceSettings> services;
  private CustomRequestHeader customRequestHeader;

  /**
   * Default constructor
   */
  public ServiceAdapterOptions() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public ServiceAdapterOptions(ServiceAdapterOptions other) {
    this.address = other.address;
    this.clientOptions = new WebClientOptions(other.clientOptions);
    this.services = new ArrayList<>(other.services);
    this.customRequestHeader = new CustomRequestHeader(other.customRequestHeader);
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public ServiceAdapterOptions(JsonObject json) {
    init();
    ServiceAdapterOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ServiceAdapterOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    address = DEFAULT_ADDRESS;
    clientOptions = new WebClientOptions().setMaxPoolSize(DEFAULT_WEBCLIENT_MAX_POOL_SIZE)
        .setKeepAlive(DEFAULT_WEBCLIENT_KEEP_ALIVE);
    services = DEFAULT_SERVICES;
    customRequestHeader = new CustomRequestHeader();
  }

  /**
   * @return event bus address the service adapter is registered on
   */
  public String getAddress() {
    return address;
  }

  /**
   * The {@code address} on event bus that the service adapter is listening for requests from {@link
   * ServiceKnotVerticle}
   *
   * @param address an event bus address
   * @return a reference to this, so the API can be used fluently
   */
  public ServiceAdapterOptions setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * @return {@link io.vertx.ext.web.client.WebClientOptions} of the Http Client used bv the ServiceAdapter to communicate
   * with external services.
   */
  public WebClientOptions getClientOptions() {
    return clientOptions;
  }

  /**
   * Set the {@link io.vertx.ext.web.client.WebClientOptions} of the {@link io.vertx.rxjava.ext.web.client.WebClient} used
   * by the ServiceAdapter to communicate with external services.
   */
  public ServiceAdapterOptions setClientOptions(WebClientOptions clientOptions) {
    this.clientOptions = clientOptions;
    return this;
  }

  /**
   * @return a list of {@link ServiceSettings} describing all service endpoints the ServiceAdapter
   * need to communicate
   */
  public List<ServiceSettings> getServices() {
    return services;
  }

  /**
   * Set the list of {@link ServiceSettings} that ServiceAdapter is able to communicate with
   *
   * @param services the {@link ServiceSettings} objects representing each service endpoint
   * @return a reference to this, so the API can be used fluently
   */
  public ServiceAdapterOptions setServices(
      List<ServiceSettings> services) {
    this.services = services;
    return this;
  }

  /**
   * @return a Custom Header to be sent in every request to the services
   */
  public CustomRequestHeader getCustomRequestHeader() {
    return customRequestHeader;
  }

  /**
   * Set the header (name & value) to be sent in every request to the services
   *
   * @param customRequestHeader the header name & value
   * @return a reference to this, so the API can be used fluently
   */
  public ServiceAdapterOptions setCustomRequestHeader(
      CustomRequestHeader customRequestHeader) {
    this.customRequestHeader = customRequestHeader;
    return this;
  }
}
