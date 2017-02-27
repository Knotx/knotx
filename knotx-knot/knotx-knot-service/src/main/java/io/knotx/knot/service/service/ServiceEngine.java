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
package io.knotx.knot.service.service;

import io.knotx.dataobjects.AdapterRequest;
import io.knotx.dataobjects.AdapterResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.knot.service.ServiceKnotConfiguration;
import io.knotx.rxjava.proxy.AdapterProxy;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import java.util.Optional;
import rx.Single;

public class ServiceEngine {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEngine.class);

  private static final String RESULT_NAMESPACE_KEY = "_result";
  private static final String RESPONSE_NAMESPACE_KEY = "_response";

  private final ServiceKnotConfiguration configuration;

  private final Vertx vertx;

  public ServiceEngine(Vertx vertx, ServiceKnotConfiguration serviceConfiguration) {
    this.vertx = vertx;
    this.configuration = serviceConfiguration;
  }

  public Single<JsonObject> doServiceCall(ServiceEntry serviceEntry, KnotContext knotContext) {
    AdapterRequest adapterRequest = new AdapterRequest()
        .setRequest(knotContext.getClientRequest())
        .setParams(serviceEntry.getParams());

    AdapterProxy serviceProxy = AdapterProxy.createProxy(vertx, serviceEntry.getAddress());

    return serviceProxy.rxProcess(adapterRequest).map(this::buildResultObject);
  }

  public ServiceEntry mergeWithConfiguration(final ServiceEntry serviceEntry) {
    Optional<ServiceKnotConfiguration.ServiceMetadata> serviceMetadata = configuration.getServices()
        .stream()
        .filter(service -> serviceEntry.getName().matches(service.getName()))
        .findFirst();

    return serviceMetadata.map(
        metadata ->
            new ServiceEntry(serviceEntry)
                .setAddress(metadata.getAddress())
                .mergeParams(metadata.getParams())
                .setCacheKey(metadata.getCacheKey()))
        .orElseThrow(() -> {
          LOGGER.error("Missing service configuration for: {}", serviceEntry.getName());
          return new IllegalStateException("Missing service configuration");
        });
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
    object.put(RESPONSE_NAMESPACE_KEY, new JsonObject()
        .put("statusCode", Integer.toString(adapterResponse.getResponse().getStatusCode())));
    return object;
  }
}
