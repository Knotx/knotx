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

import com.google.common.collect.Maps;

import com.cognifide.knotx.api.TemplateEngineRequest;
import com.cognifide.knotx.template.event.TrafficObserver;
import com.github.jknack.handlebars.Handlebars;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import rx.Observable;

@Component
public class TemplateEngineImpl implements TemplateEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngineImpl.class);

    private static final String SNIPPET_TAG = "script[data-api-type=\"templating\"]";

    @Autowired
    private Handlebars handlebars;

    @Value("${template.debug}")
    private boolean templateDebug;

    private CountDownLatch templatesLatch;

    private TrafficObserver trafficObserver = new TrafficObserver();

    @Override
    public Observable<String> process(TemplateEngineRequest request) {
        final Document htmlDocument = Jsoup.parse(request.getTemplate().toString());

        return Observable.just(htmlDocument)
                .flatMap(document ->
                                Observable.from(document.select(SNIPPET_TAG))
                                        .map(TemplateSnippet::raw)
                                        .flatMap(this::compileSnippetTemplate)
                                        .flatMap(snippet ->
                                                snippet.getServices()
                                                        .flatMap(this::doServiceCall,
                                                                (service, serviceResult) ->
                                                                        service.setResult(serviceResult))
                                                        .flatMap(serviceEntry -> applyData(snippet, serviceEntry.getServiceResult()))
                                        ),
                        (doc, results) -> doc.html()
                );
    }

//        io.vertx.rx.java.RxHelper
//
//        //Observable.create()
//
//        if (template != null) {
//            if (noSnippetsToProcessLeft()) {
//                finishRequest(request);
//            }
//        } else {
//            request.response()
//                    .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaders.TEXT_HTML.toString())
//                    .setStatusCode(HttpStatus.NOT_FOUND.value())
//                    .end();
//            trafficObserver.onFinish();
//        }
//
//        Observable.from(snippetGroups.entrySet()).subscribe(
//                snippetGroup -> handleTemplate(snippetGroup, request),
//                throwable -> {
//                    LOGGER.error("Fatal error when requesting {}", request.absoluteURI(), throwable);
//                    finishIfLast(request);
//                });


    private Observable<Map<String, Object>> doServiceCall(TemplateSnippet.ServiceEntry serviceEntry) {
        return Observable.just(Maps.newHashMap());
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

    private Observable<Element> applyData(TemplateSnippet template, Map<String, Object> data) {
        return Observable.just(new Element(Tag.valueOf("h1"), "test"));
    }

//    @Override
//    public void finishIfLast(HttpServerRequest request) {
//        templatesLatch.countDown();
//        if (noSnippetsToProcessLeft()) {
//            finishRequest(request);
//        }
//    }

//    private boolean noSnippetsToProcessLeft() {
//        return templatesLatch.getCount() == 0;
//    }
//
//    private void finishRequest(HttpServerRequest request) {
//        LOGGER.info("Finished: " + request.absoluteURI());
//        request.response().putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaders.TEXT_HTML.toString()).end(htmlDocument.html());
//        trafficObserver.onFinish();
//    }
//
//    private void handleTemplate(String dataCallUri, List<Element> snippetGroup, HttpServerRequest request) {
//        ObservableRequest observableRequest = new ObservableRequest(dataCallUri);
//        observableRequest.addObserver(trafficObserver);
//        observableRequest.onStart();
//
//        RestServiceResponseHandler serviceResponseHandler =
//                new RestServiceResponseHandler(request, snippetGroup, this, observableRequest, handlebars, templateDebug);
//        callService(request, dataCallUri, serviceResponseHandler);
//    }
//
//    private List<String> getServiceUrls(HttpServerRequest request, Element snippet) {
//        final String templateCallUri = snippet.attr("data-call-uri");
//        final StringBuilder urlSB = new StringBuilder(templateCallUri.contains("?") ? templateCallUri : templateCallUri + "?");
//        request.params().names().forEach(
//                paramName -> urlSB.append("&").append(paramName).append("=").append(request.getParam(paramName)));
//        return urlSB.toString();
//    }
//
//    public void callService(HttpServerRequest request, String dataCallUri,
//                            Handler<HttpClientResponse> serviceResponseHandler) {
//        HttpClient httpClient = vertx.createHttpClient();
//        Optional<? extends ServiceEndpoint> optionalServiceEndpoint = serviceEndpointFacade
//                .getServiceEndpoint(dataCallUri);
//        if (optionalServiceEndpoint.isPresent()) {
//            final ServiceEndpoint serviceEndpoint = optionalServiceEndpoint.get();
//            HttpClientRequest httpClientRequest = httpClient.get(serviceEndpoint.getPort(),
//                    serviceEndpoint.getDomain(), dataCallUri, serviceResponseHandler);
//            rewriteHeaders(request, httpClientRequest);
//            httpClientRequest.end();
//        } else {
//            LOGGER.error("No provider found! Request can't be processed.");
//        }
//    }
//
//    private void rewriteHeaders(HttpServerRequest request, HttpClientRequest httpClientRequest) {
//        request.headers().names().stream()
//                .filter(headerName ->
//                        configuration.serviceCallHeaders().contains(headerName))
//                .forEach(headerName ->
//                        httpClientRequest.putHeader(headerName, request.getHeader(headerName))
//                );
//    }
}
