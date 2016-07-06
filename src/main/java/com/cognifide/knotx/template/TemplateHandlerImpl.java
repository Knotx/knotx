/*
 * Knot.x - reactive microservice assembler
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

import com.google.common.collect.Iterables;

import com.cognifide.knotx.Server;
import com.cognifide.knotx.event.ObservableRequest;
import com.cognifide.knotx.event.TrafficObserver;
import com.cognifide.knotx.handler.RestServiceResponseHandler;
import com.cognifide.knotx.repository.Template;
import com.github.jknack.handlebars.Handlebars;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import rx.Observable;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TemplateHandlerImpl implements TemplateHandler<String, URI> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateHandlerImpl.class);

    private static final String SNIPPET_TAG = "script[data-api-type=\"templating\"]";

    @Autowired
    private Server server;

    @Value("${template.debug}")
    private Boolean templateDebug;

    private CountDownLatch templatesLatch;

    private TrafficObserver trafficObserver = new TrafficObserver();

    private Document htmlDocument;

    private Handlebars handlebars = new Handlebars();

    @Override
    public void handle(Template<String, URI> template, HttpServerRequest request) {
        htmlDocument = Jsoup.parse(template.get());
        final Elements snippets = htmlDocument.select(SNIPPET_TAG);
        templatesLatch = new CountDownLatch(Iterables.size(snippets));

        Observable.from(snippets).subscribe(
                snippet -> handleTemplate(snippet, request),
                throwable -> {
                    LOGGER.error("Fatal error when requesting {}", request.absoluteURI(), throwable);
                    finishIfLast(request);
                });
    }

    @Override
    public void finishIfLast(HttpServerRequest request) {
        templatesLatch.countDown();
        if (templatesLatch.getCount() == 0) {
            LOGGER.info("Finished: " + request.absoluteURI());
            request.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.TEXT_HTML).end(htmlDocument.html());
            trafficObserver.onFinish();
        }
    }

    private void handleTemplate(Element snippet, HttpServerRequest request) {
        final String dataCallUri = getServiceUrl(request, snippet);
        ObservableRequest observableRequest = new ObservableRequest(dataCallUri);
        observableRequest.addObserver(trafficObserver);
        observableRequest.onStart();

        String templateContent = snippet.html();
        try {
            com.github.jknack.handlebars.Template template = handlebars.compileInline(templateContent);
            RestServiceResponseHandler serviceResponseHandler =
                    new RestServiceResponseHandler(request, template, this, dataCallUri, observableRequest, snippet, templateDebug);
            server.callService(request, dataCallUri, serviceResponseHandler);
        } catch (IOException e) {
            LOGGER.error("Could not process template [{}]", dataCallUri);
        }
    }

    private String getServiceUrl(HttpServerRequest request, Element snippet) {
        final String templateCallUri = snippet.attr("data-call-uri");
        final StringBuilder urlSB = new StringBuilder(templateCallUri.contains("?") ? templateCallUri : templateCallUri + "?");
        request.params().entries().forEach(
                param -> urlSB.append("&").append(param.getKey()).append("=").append(param.getValue()));
        return urlSB.toString();
    }
}
