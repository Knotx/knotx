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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.cognifide.knotx.api.TemplateEngineRequest;
import com.github.jknack.handlebars.Handlebars;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

@Component
public class TemplateEngineImpl implements TemplateEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngineImpl.class);

    private static final String SNIPPET_TAG = "script[data-api-type=\"templating\"]";

    private static final String START_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- START webservice `%s` call -->";

    private static final String END_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- END webservice call -->";

    @Autowired
    private Handlebars handlebars;

    @Value("${template.debug}")
    private boolean templateDebug;

    private HttpClient httpClient;

    @Override
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Observable<String> process(TemplateEngineRequest request) {
        final Document htmlDocument = Jsoup.parse(request.getTemplate().toString());

        List<Observable<Element>> snippetsPipeline = htmlDocument.select(SNIPPET_TAG).stream()
                .map(TemplateSnippet::raw)
                .map(snippet ->
                        Observable.just(snippet)
                                .flatMap(this::compileSnippetTemplate)
                                .flatMap(this::processSnippet)
                ).collect(Collectors.toList());

        return Observable.zip(snippetsPipeline, args -> htmlDocument.html());
    }

    private Observable<Element> processSnippet(final TemplateSnippet snippet) {
        return snippet.getServices()
                .doOnNext(serviceEntry -> LOGGER.trace("Call to service: {0}", serviceEntry.getServiceUri()))
                .flatMap(this::doServiceCall, (service, serviceResult) -> service.setResult(serviceResult))
                .flatMap(serviceEntry -> applyData(snippet, serviceEntry.getServiceResult()));
    }

    private Observable<Map<String, Object>> doServiceCall(TemplateSnippet.ServiceEntry serviceEntry) {
        Observable<HttpClientResponse> serviceResponse = RxHelper.get(httpClient, 3000, "localhost", serviceEntry.getServiceUri());
        LOGGER.debug("Calling {0}", serviceEntry.getServiceUri());
        return serviceResponse.flatMap(response -> Observable.just(Buffer.buffer())
                .mergeWith(response.toObservable())
                .reduce(Buffer::appendBuffer))
                .doOnNext(this::traceServiceCall)
                .flatMap(buffer -> {
                            Type mapType = new TypeToken<Map<String, Object>>() {
                            }.getType();
                            return Observable.<Map<String, Object>>just(new Gson().fromJson(buffer.toString(), mapType));
                        }
                );
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
            String compiledContent = snippet.getCompiledSnippet().apply(data);
            LOGGER.debug("Applying: \n{0} to \n{1}\n and result is \n{2}", data, snippet.getSnippet(), compiledContent);
            Element snippetParent = new Element(Tag.valueOf("div"), "");
//            if (templateDebug) {
//                snippetParent.prepend(String.format(START_WEBSERVICE_CALL_DEBUG_MARKER, serviceEntry.getServiceUri()));
//            }
            snippet.getSnippet().replaceWith(snippetParent.append(compiledContent));
            return Observable.just(snippetParent);
        } catch (IOException e) {
            return Observable.error(e);
        }
    }

    private void traceServiceCall(Buffer buffer) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Got message from service <{0}>", buffer.toString());
        }
    }
}
