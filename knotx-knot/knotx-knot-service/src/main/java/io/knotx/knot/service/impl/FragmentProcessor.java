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
package io.knotx.knot.service.impl;

import io.knotx.dataobjects.KnotContext;
import io.knotx.knot.service.ServiceKnotConfiguration;
import io.knotx.knot.service.service.ServiceEngine;
import io.knotx.knot.service.service.ServiceEntry;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import java.util.concurrent.ExecutionException;
import rx.Observable;

public class FragmentProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(FragmentProcessor.class);

  private final ServiceEngine serviceEngine;

  public FragmentProcessor(Vertx vertx, ServiceKnotConfiguration configuration) {
    this.serviceEngine = new ServiceEngine(vertx, configuration);
  }

  public Observable<FragmentContext> processSnippet(final FragmentContext fragmentContext,
      KnotContext request) {
    LOGGER.debug("Processing Handlebars snippet {}", fragmentContext.fragment());
    return Observable.just(fragmentContext)
        .flatMap(FragmentContext::services)
        .map(serviceEngine::mergeWithConfiguration)
        .doOnNext(this::traceService)
        .flatMap(serviceEntry ->
            fetchServiceData(serviceEntry, request)
                .map(serviceEntry::getResultWithNamespaceAsKey))
        .reduce(new JsonObject(), JsonObject::mergeIn)
        .map(results -> applyData(fragmentContext, results));
  }

  private Observable<JsonObject> fetchServiceData(ServiceEntry service, KnotContext request) {
    LOGGER.debug("Fetching data from service {} {}", service.getAddress(), service.getParams());
    try {
      return request.getCache()
          .get(service.getCacheKey(), () -> serviceEngine.doServiceCall(service, request).cache());
    } catch (ExecutionException e) {
      LOGGER.fatal("Unable to get service data {}", e);
      return Observable.error(e);
    }
  }

  private FragmentContext applyData(final FragmentContext fragmentContext,
      JsonObject serviceResult) {
    LOGGER.trace("Applying data to snippet {}", fragmentContext);
    fragmentContext.fragment().context().mergeIn(serviceResult);
    return fragmentContext;
  }

  private void traceService(ServiceEntry serviceEntry) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Found service call definition: {} {}", serviceEntry.getAddress(),
          serviceEntry.getParams());
    }
  }

}
