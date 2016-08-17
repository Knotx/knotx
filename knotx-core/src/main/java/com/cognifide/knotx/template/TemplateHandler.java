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

import com.cognifide.knotx.repository.Action;
import com.google.common.collect.Iterables;

import com.cognifide.knotx.KnotxVerticle;
import com.cognifide.knotx.event.ObservableRequest;
import com.cognifide.knotx.event.TrafficObserver;
import com.cognifide.knotx.handler.RestServiceResponseHandler;
import com.cognifide.knotx.repository.template.Template;
import com.github.jknack.handlebars.Handlebars;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import rx.Observable;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TemplateHandler implements Action{

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateHandler.class);

    private static final String SNIPPET_TAG = "script[data-api-type=\"templating\"]";

    private final HttpServerRequest httpServerRequest;

    @Autowired
    private Handlebars handlebars;
    @Autowired
    private KnotxVerticle verticle;

    @Value("${template.debug}")
    private boolean templateDebug;

    private CountDownLatch templatesLatch;

    private TrafficObserver trafficObserver = new TrafficObserver();

    private Document htmlDocument;

    private MultiValueMap<String, Element> snippetGroups = new LinkedMultiValueMap<>();

    @Autowired
    public TemplateHandler(HttpServerRequest httpServerRequest) {
        this.httpServerRequest = httpServerRequest;
    }

    @Override
    public void handle(Template<String, ?> template) {
            htmlDocument = Jsoup.parse(template.get());
            htmlDocument.select(SNIPPET_TAG).forEach(snippet -> snippetGroups.add(getServiceUrl(httpServerRequest, snippet), snippet));
            templatesLatch = new CountDownLatch(Iterables.size(snippetGroups.entrySet()));

            if (noSnippetsToProcessLeft()) {
                finishRequest(httpServerRequest);
            }


        Observable.from(snippetGroups.entrySet()).subscribe(
                this::handleTemplate,
                throwable -> {
                    LOGGER.error("Fatal error when requesting {}", httpServerRequest.absoluteURI(), throwable);
                    finishIfLast();
                });
    }

    public void finishIfLast() {
        templatesLatch.countDown();
        if (noSnippetsToProcessLeft()) {
            finishRequest(httpServerRequest);
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

    private void handleTemplate(Entry<String, List<Element>> snippetGroup) {
        final String dataCallUri = snippetGroup.getKey();
        ObservableRequest observableRequest = new ObservableRequest(dataCallUri);
        observableRequest.addObserver(trafficObserver);
        observableRequest.onStart();

        RestServiceResponseHandler serviceResponseHandler =
                new RestServiceResponseHandler(httpServerRequest, snippetGroup, this, observableRequest, handlebars, templateDebug);
        verticle.callService(httpServerRequest, dataCallUri, serviceResponseHandler);
    }

    private String getServiceUrl(HttpServerRequest request, Element snippet) {
        final String templateCallUri = snippet.attr("data-call-uri");
        final StringBuilder urlSB = new StringBuilder(templateCallUri.contains("?") ? templateCallUri : templateCallUri + "?");
        request.params().entries().forEach(
                param -> urlSB.append("&").append(param.getKey()).append("=").append(param.getValue()));
        return urlSB.toString();
    }
}
