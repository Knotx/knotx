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
import com.cognifide.knotx.template.service.ServiceEngine;
import com.github.jknack.handlebars.Handlebars;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.http.HttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

@Component
public class TemplateEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngine.class);

    private static final String SNIPPET_TAG = "script[data-api-type=\"templating\"]";

    private static final String START_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- start snippet `%s` calls: %s -->";

    private static final String END_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- end snippet `%s` calls -->";

    @Autowired
    private ServiceEngine serviceEngine;

    @Autowired
    private Handlebars handlebars;

    @Value("${template.debug}")
    private boolean templateDebug;

    //FIXME: better way of passing http client is required - maybe nano service as verticle
    public void setHttpClient(HttpClient httpClient) {
        serviceEngine.setHttpClient(httpClient);
    }

    public Observable<String> process(TemplateEngineRequest request) {
        final Document htmlDocument = Jsoup.parse(request.getTemplate().toString());

        List<Observable<Element>> snippetsPipeline = htmlDocument.select(SNIPPET_TAG).stream()
                .map(TemplateSnippet::raw)
                .map(snippet ->
                        Observable.just(snippet).subscribeOn(Schedulers.io())
                                .doOnNext(this::traceSnippet)
                                .flatMap(this::compileSnippetTemplate)
                                .flatMap(this::processSnippet)
                ).collect(Collectors.toList());

        return Observable.zip(snippetsPipeline, args -> htmlDocument.html());
    }

    private Observable<Element> processSnippet(final TemplateSnippet snippet) {
        return snippet.getServices()
                .doOnNext(serviceEntry -> LOGGER.trace("Call to service: {0}", serviceEntry.getServiceUri()))
                .flatMap(serviceEngine::findServiceLocation)
                .flatMap(serviceEngine::doServiceCall, (service, serviceResult) -> service.setResult(serviceResult))
                .flatMap(serviceEntry -> applyData(snippet, serviceEntry.getServiceResult()));
    }

    private Observable<TemplateSnippet> compileSnippetTemplate(TemplateSnippet snippet) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(
                        snippet.setCompiledSnippet(
                                handlebars.compileInline(
                                        snippet.getSnippet().html()
                                )
                        )
                );
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private Observable<Element> applyData(TemplateSnippet snippet, Map<String, Object> data) {
        try {
            final String compiledContent = snippet.getCompiledSnippet().apply(data);
            LOGGER.trace("Applying: \n{0} to \n{1}\n and result is \n{2}", data, snippet.getSnippet(), compiledContent);
            final StringBuilder snippetFinalMarkup = new StringBuilder();

            snippetFinalMarkup.append(startComment(snippet));
            snippetFinalMarkup.append(compiledContent);
            snippetFinalMarkup.append(endComment(snippet));

            final Element snippetFinalElement = snippet.getSnippet().after(snippetFinalMarkup.toString());
            snippet.getSnippet().remove();
            return Observable.just(snippetFinalElement);
        } catch (IOException e) {
            return Observable.error(e);
        }
    }

    private StringBuilder startComment(TemplateSnippet snippet) {
        return snippetComment(snippet, START_WEBSERVICE_CALL_DEBUG_MARKER);
    }

    private StringBuilder endComment(TemplateSnippet snippet) {
        return snippetComment(snippet, END_WEBSERVICE_CALL_DEBUG_MARKER);
    }

    private StringBuilder snippetComment(TemplateSnippet snippet, String commentTemplate) {
        StringBuilder debugLine = new StringBuilder();
        if (templateDebug) {
            debugLine.append(String.format(commentTemplate, snippet,
                    snippet.getCalledServicesUri()));
        }
        return debugLine;
    }

    private void traceSnippet(TemplateSnippet templateSnippet) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Processing snippet <{0}>", templateSnippet.getSnippet().html());
        }
    }
}
