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
import io.knotx.knot.service.ServiceKnotOptions;
import io.knotx.knot.service.ServiceMetadata;
import io.knotx.reactivex.proxy.AdapterProxy;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class ServiceEngine {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEngine.class);

  private static final String RESULT_NAMESPACE_KEY = "_result";
  private static final String RESPONSE_NAMESPACE_KEY = "_response";

  private final ServiceKnotOptions options;

  private final Map<String, AdapterProxy> adapters;

  public ServiceEngine(Vertx vertx, ServiceKnotOptions options) {
    this.options = options;
    this.adapters = new HashMap<>();
    this.options.getServices().stream().forEach(
        service -> adapters.put(service.getAddress(),
            AdapterProxy.createProxyWithOptions(
                vertx,
                service.getAddress(),
                this.options.getDeliveryOptions())
        )
    );
  }

  public Single<JsonObject> doServiceCall(ServiceEntry serviceEntry, KnotContext knotContext) {
    AdapterRequest adapterRequest = new AdapterRequest()
        .setRequest(knotContext.getClientRequest())
        .setParams(serviceEntry.getParams());

    return adapters.get(serviceEntry.getAddress()).rxProcess(adapterRequest)
        .map(resp -> buildResultObject(adapterRequest, resp));
  }

  public ServiceEntry mergeWithConfiguration(final ServiceEntry serviceEntry) {
    Optional<ServiceMetadata> serviceMetadata = options.getServices()
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

  private JsonObject buildResultObject(AdapterRequest adapterRequest,
      AdapterResponse adapterResponse) {
    JsonObject object = new JsonObject();

    String rawData = adapterResponse.getResponse().getBody().toString().trim();

    if (rawData.charAt(0) == '[') {
      object.put(RESULT_NAMESPACE_KEY, new JsonArray(rawData));
    } else if (rawData.charAt(0) == '{') {
      object.put(RESULT_NAMESPACE_KEY, new JsonObject(rawData));
    } else {
      LOGGER.error("Result of [{} {}] neither Json Array nor Json Object: [{}]",
          adapterRequest.getRequest().getMethod(), adapterRequest.getRequest().getPath(),
          StringUtils.abbreviate(rawData, 15));
    }
    object.put(RESPONSE_NAMESPACE_KEY, new JsonObject()
        .put("statusCode", Integer.toString(adapterResponse.getResponse().getStatusCode())));
    return object;
  }
}
