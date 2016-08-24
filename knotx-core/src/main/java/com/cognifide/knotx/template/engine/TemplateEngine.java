/*
 * Knot.x - Reactive microservice assembler
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
package com.cognifide.knotx.template.engine;

import com.cognifide.knotx.api.TemplateEngineRequest;
import com.cognifide.knotx.template.TemplateEngineConfiguration;
import com.cognifide.knotx.template.service.ServiceEngine;
import com.github.jknack.handlebars.Handlebars;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;
import rx.schedulers.Schedulers;

public class TemplateEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngine.class);

    private static final String SNIPPET_TAG = "script[data-api-type=\"templating\"]";

    private static final String START_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- start snippet `%s` calls: %s -->";

    private static final String END_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- end snippet `%s` calls -->";

    private ServiceEngine serviceEngine;

    private Handlebars handlebars;

    private boolean templateDebug;

    private Vertx vertx;

    public TemplateEngine(Vertx vertx, TemplateEngineConfiguration configuration) {
        this.vertx = vertx;
        this.serviceEngine = new ServiceEngine(vertx, configuration);
        this.templateDebug = configuration.templateDebug();
        initHandlebars();
    }

    private void initHandlebars() {
        handlebars = new Handlebars();
        HandlebarsHelpers.register(handlebars);
    }

    public Observable<String> process(TemplateEngineRequest request) {
        final Document htmlDocument = Jsoup.parse(request.getTemplate().toString());

        List<Observable<Element>> snippetsPipeline = htmlDocument.select(SNIPPET_TAG).stream()
                .map(TemplateSnippet::raw)
                .map(snippet ->
                        Observable.just(snippet).subscribeOn(Schedulers.io())
                                .doOnNext(this::traceSnippet)
                                .flatMap(this::compileSnippetTemplate)
                                .flatMap(item -> processSnippet(item, request))
                ).collect(Collectors.toList());

        return Observable.zip(snippetsPipeline, args -> htmlDocument.html());
    }

    private Observable<Element> processSnippet(final TemplateSnippet snippet, TemplateEngineRequest request) {
        Map<String, Object> allServicesResults = new HashMap<>();
        return snippet.getServices()
                .doOnNext(serviceEntry -> LOGGER.trace("Call to service: {}", serviceEntry.getServiceUri()))
                .flatMap(serviceEngine::findServiceLocation)
                .flatMap(serviceItem -> serviceEngine.doServiceCall(serviceItem, request.getHeaders()), (serviceEntry, serviceResult) -> serviceEntry.setServiceResult(serviceResult))
                .collect(() -> allServicesResults, (resultCollection, serviceItem) -> {
                    if (StringUtils.isNotEmpty(serviceItem.getPlaceholderNamespace())) {
                        resultCollection.put(serviceItem.getPlaceholderNamespace(), serviceItem.getServiceResult());
                    } else {
                        resultCollection.putAll(serviceItem.getServiceResult());
                    }
                })
                .flatMap(resultCollection -> applyData(snippet, resultCollection));
    }

    private Observable<TemplateSnippet> compileSnippetTemplate(TemplateSnippet snippet) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(
                        snippet.setCompiledSnippet(
                                handlebars.compileInline(snippet.getSnippet().html())
                        )
                );
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private Observable<Element> applyData(TemplateSnippet snippet, Map<String, Object> serviceResult) {
        try {
            final String compiledContent = snippet.getCompiledSnippet().apply(serviceResult);
            final Element originalSnippet = snippet.getSnippet();
            LOGGER.trace("Applying: \n{} to \n{}\n and result is \n{}", serviceResult, originalSnippet, compiledContent);
            final StringBuilder snippetFinalMarkup = new StringBuilder();

            snippetFinalMarkup.append(startComment(snippet));
            snippetFinalMarkup.append(compiledContent);
            snippetFinalMarkup.append(endComment(snippet));

            final Element snippetFinalElement = originalSnippet.after(snippetFinalMarkup.toString());
            originalSnippet.remove();
            return Observable.just(snippetFinalElement);
        } catch (IOException e) {
            return Observable.error(e);
        }
    }

    private String startComment(TemplateSnippet snippet) {
        return snippetComment(snippet, START_WEBSERVICE_CALL_DEBUG_MARKER);
    }

    private String endComment(TemplateSnippet snippet) {
        return snippetComment(snippet, END_WEBSERVICE_CALL_DEBUG_MARKER);
    }

    private String snippetComment(TemplateSnippet snippet, String commentTemplate) {
        String debugLine = StringUtils.EMPTY;
        if (templateDebug) {
            debugLine = String.format(commentTemplate, snippet, snippet.getCalledServicesUri());
        }
        return debugLine;
    }

    private void traceSnippet(TemplateSnippet templateSnippet) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Processing snippet <{}>", templateSnippet.getSnippet().html());
        }
    }
}
