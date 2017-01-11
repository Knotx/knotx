/*
 * Knot.x - Reactive microservice assembler - Service Knot
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
package com.cognifide.knotx.knot.service.service;

import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.AdapterResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.knot.service.ServiceKnotConfiguration;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import java.util.Optional;
import rx.Observable;

public class ServiceEngine {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEngine.class);

  private static final String RESULT_NAMESPACE_KEY = "_result";
  private static final String RESPONSE_NAMESPACE_KEY = "_response";

  private final ServiceKnotConfiguration configuration;

  private final EventBus eventBus;

  public ServiceEngine(EventBus eventBus, ServiceKnotConfiguration serviceConfiguration) {
    this.eventBus = eventBus;
    this.configuration = serviceConfiguration;
  }

  public Observable<JsonObject> doServiceCall(ServiceEntry serviceEntry, KnotContext knotContext) {
    AdapterRequest adapterRequest = new AdapterRequest()
        .setRequest(knotContext.getClientRequest())
        .setParams(serviceEntry.getParams());

    return eventBus.<AdapterResponse>sendObservable(serviceEntry.getAddress(), adapterRequest)
        .map(Message::body)
        .map(this::buildResultObject);
  }

  public ServiceEntry mergeWithConfiguration(final ServiceEntry serviceEntry) {
    Optional<ServiceKnotConfiguration.ServiceMetadata> serviceMetadata = configuration.getServices().stream()
        .filter(service -> serviceEntry.getName().matches(service.getName()))
        .findFirst();

    if (serviceMetadata.isPresent()) {
      return serviceMetadata.map(metadata ->
          new ServiceEntry(serviceEntry)
              .setAddress(metadata.getAddress())
              .mergeParams(metadata.getParams())
              .setCacheKey(metadata.getCacheKey()))
          .get();
    } else {
      LOGGER.error("Missing service configuration for: {}", serviceEntry.getName());
      throw new IllegalStateException("Missing service configuration");
    }
  }

  private JsonObject buildResultObject(AdapterResponse adapterResponse) {
    JsonObject object = new JsonObject();

    String rawData = adapterResponse.getResponse().getBody().toString().trim();

    if (rawData.charAt(0) == '[') {
      object.put(RESULT_NAMESPACE_KEY, new JsonArray(rawData));
    } else if (rawData.charAt(0) == '{') {
      object.put(RESULT_NAMESPACE_KEY, new JsonObject(rawData));
    } else {
      throw new DecodeException("Result is neither Json Array nor Json Object");
    }
    object.put(RESPONSE_NAMESPACE_KEY, new JsonObject().put("statusCode", Integer.toString(adapterResponse.getResponse().getStatusCode())));
    return object;
  }
}
