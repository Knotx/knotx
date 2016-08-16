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
package com.cognifide.knotx.template;

import com.cognifide.knotx.placeholder.UriTransformer;
import com.google.common.collect.Iterables;

import com.cognifide.knotx.KnotxVerticle;
import com.cognifide.knotx.event.ObservableRequest;
import com.cognifide.knotx.event.TrafficObserver;
import com.cognifide.knotx.handler.RestServiceResponseHandler;
import com.cognifide.knotx.repository.Template;
import com.github.jknack.handlebars.Handlebars;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import rx.Observable;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class TemplateHandlerImpl implements TemplateHandler<String, URI> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateHandlerImpl.class);

    private static final String SNIPPET_TAG = "script[data-api-type=\"templating\"]";

    @Autowired
    private Handlebars handlebars;

    @Autowired
    private KnotxVerticle verticle;

    @Autowired
    private BeanFactory beanFactory;

    @Value("${template.debug}")
    private boolean templateDebug;

    private CountDownLatch templatesLatch;

    private TrafficObserver trafficObserver = new TrafficObserver();

    private Document htmlDocument;

    private MultiValueMap<String, Element> snippetGroups = new LinkedMultiValueMap<>();

    @Override
    public void handle(Template<String, URI> template, HttpServerRequest request) {
        if (template != null) {
            htmlDocument = Jsoup.parse(template.get());
            final UriTransformer uriTransformer = beanFactory.getBean(UriTransformer.class, beanFactory,
                    request);
            htmlDocument.select(SNIPPET_TAG).forEach(snippet -> snippetGroups
                    .add(uriTransformer.getServiceUrl(snippet), snippet));
            templatesLatch = new CountDownLatch(Iterables.size(snippetGroups.entrySet()));

            if (noSnippetsToProcessLeft()) {
                finishRequest(request);
            }
        } else {
            request.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.TEXT_HTML)
                    .setStatusCode(HttpStatus.NOT_FOUND.value())
                    .end();
            trafficObserver.onFinish();
        }

        Observable.from(snippetGroups.entrySet()).subscribe(
                snippetGroup -> handleTemplate(snippetGroup, request),
                throwable -> {
                    LOGGER.error("Fatal error when requesting {}", request.absoluteURI(), throwable);
                    finishIfLast(request);
                });
    }

    @Override
    public void finishIfLast(HttpServerRequest request) {
        templatesLatch.countDown();
        if (noSnippetsToProcessLeft()) {
            finishRequest(request);
        }
    }

    private boolean noSnippetsToProcessLeft() {
        return templatesLatch.getCount() == 0;
    }

    private void finishRequest(HttpServerRequest request) {
        LOGGER.info("Finished: " + request.absoluteURI());
        request.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.TEXT_HTML).end(htmlDocument.html());
        trafficObserver.onFinish();
    }

    private void handleTemplate(Entry<String, List<Element>> snippetGroup, HttpServerRequest request) {
        final String dataCallUri = snippetGroup.getKey();
        ObservableRequest observableRequest = new ObservableRequest(dataCallUri);
        observableRequest.addObserver(trafficObserver);
        observableRequest.onStart();

        RestServiceResponseHandler serviceResponseHandler =
                new RestServiceResponseHandler(request, snippetGroup, this, observableRequest, handlebars, templateDebug);
        verticle.callService(request, dataCallUri, serviceResponseHandler);
    }

}
