/*
 * Knot.x - Reactive microservice assembler - View Knot
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
package com.cognifide.knotx.knot.view.service;

import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.AdapterResponse;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.knot.view.ViewKnotConfiguration;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class ServiceEngine {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEngine.class);
  private static final String RESULT_NAMESPACE_KEY = "_result";
  private static final String RESPONSE_NAMESPACE_KEY = "_response";

  private final ViewKnotConfiguration configuration;

  private final EventBus eventBus;

  public ServiceEngine(EventBus eventBus, ViewKnotConfiguration serviceConfiguration) {
    this.eventBus = eventBus;
    this.configuration = serviceConfiguration;
  }

  public Observable<JsonObject> doServiceCall(ServiceEntry serviceEntry,
                                              KnotContext knotContext) {
    AdapterRequest adapterRequest = new AdapterRequest()
        .setRequest(knotContext.clientRequest())
        .setParams(serviceEntry.getParams());

    return eventBus.<AdapterResponse>sendObservable(serviceEntry.getAddress(), adapterRequest)
        .map(Message::body)
        .map(this::buildResultObject);
  }

  public ServiceEntry mergeWithConfiguration(final ServiceEntry serviceEntry) {
    return configuration.getServices().stream()
        .filter(service -> serviceEntry.getName().matches(service.getName()))
        .findFirst().map(metadata ->
            serviceEntry.setAddress(metadata.getAddress())
                .mergeParams(metadata.getParams())
                .overrideCacheKey(metadata.getCacheKey())
        )
        .get();
  }

  private JsonObject buildResultObject(AdapterResponse adapterResponse) {
    JsonObject object = new JsonObject();

    String rawData = adapterResponse.response().body().toString().trim();

    if (rawData.charAt(0) == '[') {
      object.put(RESULT_NAMESPACE_KEY, new JsonArray(rawData));
    } else if (rawData.charAt(0) == '{') {
      object.put(RESULT_NAMESPACE_KEY, new JsonObject(rawData));
    } else {
      throw new DecodeException("Result is neither Json Array nor Json Object");
    }
    object.put(RESPONSE_NAMESPACE_KEY, new JsonObject().put("statusCode", adapterResponse.response().statusCode().codeAsText()));
    return object;
  }

  private void traceServiceCall(Buffer results, ServiceEntry entry) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Service call returned <{}> <{}> <{}>", results.toString(), entry.getAddress(), entry.getParams());
    }
  }
}
