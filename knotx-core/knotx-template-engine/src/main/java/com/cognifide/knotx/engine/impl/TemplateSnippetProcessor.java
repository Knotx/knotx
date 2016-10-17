/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
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
package com.cognifide.knotx.engine.impl;

import com.cognifide.knotx.dataobjects.RenderRequest;
import com.cognifide.knotx.engine.TemplateEngineConfiguration;
import com.cognifide.knotx.engine.parser.HtmlFragment;
import com.cognifide.knotx.engine.service.ServiceEngine;
import com.cognifide.knotx.engine.service.ServiceEntry;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutionException;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.http.HttpClient;
import rx.Observable;

public class TemplateSnippetProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateSnippetProcessor.class);

  private static final String START_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- start compiled snippet -->";

  private static final String END_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- end compiled snippet -->";

  private final ServiceEngine serviceEngine;

  private final boolean templateDebug;


  public TemplateSnippetProcessor(EventBus eventBus, TemplateEngineConfiguration configuration) {
    this.serviceEngine = new ServiceEngine(eventBus, configuration);
    this.templateDebug = configuration.templateDebug();
  }

  public Observable<String> processSnippet(final HtmlFragment fragment, RenderRequest request) {
    LOGGER.debug("Processing Handlebars snippet {}", fragment.getContent());
    return Observable.just(fragment)
        .flatMap(HtmlFragment::getServices)
//        .filter(serviceEntry -> serviceEntry.canServeRequest(fragment, request))
        .doOnNext(this::traceService)
        .map(serviceEngine::findServiceLocation)
        .flatMap(serviceEntry ->
            fetchServiceData(serviceEntry, request)
                .map(serviceEntry::getResultWithNamespaceAsKey))
        .reduce(new JsonObject(), JsonObject::mergeIn)
        .map(results -> applyData(fragment, results))
        .defaultIfEmpty(fragment.getContent());
  }

  public Observable<JsonObject> fetchServiceData(ServiceEntry service, RenderRequest request) {
    LOGGER.debug("Fetching data from service {} {}", service.getAddress(), service.getPayload());
    try {
      return request.getCache().get(service.getCacheKey(), () -> serviceEngine.doServiceCall(service, request).cache());
    } catch (ExecutionException e) {
      LOGGER.fatal("Unable to get service data {}", e);
      return Observable.error(e);
    }
  }

  private String applyData(final HtmlFragment snippet, JsonObject serviceResult) {
    LOGGER.trace("Applying data to snippet {}", snippet);
    final StringBuilder result = new StringBuilder();

    result.append(startComment());
    result.append(snippet.getContentWithContext(serviceResult));
    result.append(endComment());

    return result.toString();
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
      LOGGER.trace("Found service call definition: {} {}", serviceEntry.getAddress(), serviceEntry.getPayload());
    }
  }

}
