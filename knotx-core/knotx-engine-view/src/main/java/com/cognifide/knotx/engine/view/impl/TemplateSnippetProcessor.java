/*
 * Knot.x - Reactive microservice assembler - View Engine Verticle
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
package com.cognifide.knotx.engine.view.impl;

import com.cognifide.knotx.dataobjects.EngineRequest;
import com.cognifide.knotx.engine.view.ViewEngineConfiguration;
import com.cognifide.knotx.engine.view.parser.HtmlFragment;
import com.cognifide.knotx.engine.view.service.ServiceEngine;
import com.cognifide.knotx.engine.view.service.ServiceEntry;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutionException;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import rx.Observable;

class TemplateSnippetProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateSnippetProcessor.class);

  private static final String START_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- start compiled snippet -->";

  private static final String END_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- end compiled snippet -->";

  private final ServiceEngine serviceEngine;

  private final boolean templateDebug;


  TemplateSnippetProcessor(EventBus eventBus, ViewEngineConfiguration configuration) {
    this.serviceEngine = new ServiceEngine(eventBus, configuration);
    this.templateDebug = configuration.templateDebug();
  }

  Observable<String> processSnippet(final HtmlFragment fragment, EngineRequest request) {
    LOGGER.debug("Processing Handlebars snippet {}", fragment.getContent());
    return Observable.just(fragment)
        .flatMap(HtmlFragment::getServices)
        .map(serviceEngine::mergeWithConfiguration)
        .doOnNext(this::traceService)
        .flatMap(serviceEntry ->
            fetchServiceData(serviceEntry, request)
                .map(serviceEntry::getResultWithNamespaceAsKey))
        .reduce(new JsonObject(), JsonObject::mergeIn)
        .map(results -> applyData(fragment, results))
        .defaultIfEmpty(fragment.getContent());
  }

  private Observable<JsonObject> fetchServiceData(ServiceEntry service, EngineRequest request) {
    LOGGER.debug("Fetching data from service {} {}", service.getAddress(), service.getParams());
    try {
      return request.getCache().get(service.getCacheKey(), () -> serviceEngine.doServiceCall(service, request).cache());
    } catch (ExecutionException e) {
      LOGGER.fatal("Unable to get service data {}", e);
      return Observable.error(e);
    }
  }

  private String applyData(final HtmlFragment snippet, JsonObject serviceResult) {
    LOGGER.trace("Applying data to snippet {}", snippet);
    return startComment() +
        snippet.getContentWithContext(serviceResult) +
        endComment();
  }


  private String startComment() {
    return snippetComment(START_WEBSERVICE_CALL_DEBUG_MARKER);
  }

  private String endComment() {
    return snippetComment(END_WEBSERVICE_CALL_DEBUG_MARKER);
  }

  private String snippetComment(String commentTemplate) {
    String debugLine = StringUtils.EMPTY;
    if (templateDebug) {
      debugLine = commentTemplate;
    }
    return debugLine;
  }

  private void traceService(ServiceEntry serviceEntry) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Found service call definition: {} {}", serviceEntry.getAddress(), serviceEntry.getParams());
    }
  }

}
