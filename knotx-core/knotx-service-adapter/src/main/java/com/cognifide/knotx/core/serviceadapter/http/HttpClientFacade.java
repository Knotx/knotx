/*
 * Knot.x - Sample App with Mock service
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.knotx.core.serviceadapter.http;

import com.cognifide.knotx.core.serviceadapter.ServiceAdapterConfiguration;

import java.util.List;
import java.util.Map;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import rx.Observable;

public class HttpClientFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientFacade.class);

  private final List<ServiceAdapterConfiguration.ServiceMetadata> services;

  private final HttpClient httpClient;

  public HttpClientFacade(Vertx vertx, ServiceAdapterConfiguration configuration) {
    final JsonObject clientOptions = configuration.getClientOptions();
    this.services = configuration.getServices();
    this.httpClient = clientOptions.isEmpty() ?
            vertx.createHttpClient() : vertx.createHttpClient(new HttpClientOptions(clientOptions));
  }

  public Observable<Map<String, Object>> process(JsonObject payload) {
    return null;
  }

  public void close() {
    LOGGER.info("HttpClient is closing.");
    httpClient.close();
    LOGGER.info("HttpClient closed.");
  }

}
