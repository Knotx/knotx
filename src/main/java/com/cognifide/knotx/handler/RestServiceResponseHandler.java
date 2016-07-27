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
package com.cognifide.knotx.handler;

import com.google.gson.Gson;

import com.cognifide.knotx.event.ObservableRequest;
import com.cognifide.knotx.template.TemplateHandler;
import com.github.jknack.handlebars.Template;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerRequest;

public class RestServiceResponseHandler implements Handler<HttpClientResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceResponseHandler.class);

    private final HttpServerRequest request;

    private final TemplateHandler templateHandler;

    private final String dataCallUri;

    private final ObservableRequest observableRequest;

    private final boolean templateDebug;

    private final Map<Element, Template> snippetTemplateMap;

    public RestServiceResponseHandler(HttpServerRequest request, Map<Element, Template> snippetTemplateMap, TemplateHandler templateHandler,
                                      String dataCallUri, ObservableRequest observableRequest, Boolean templateDebug) {
        this.request = request;
        this.snippetTemplateMap = snippetTemplateMap;
        this.templateHandler = templateHandler;
        this.dataCallUri = dataCallUri;
        this.observableRequest = observableRequest;
        this.templateDebug = templateDebug;
    }

    @Override
    public void handle(HttpClientResponse response) {
        response.bodyHandler(buffer -> {
            String responseContent = buffer.getString(0, buffer.length());
            LOGGER.debug("Request in: " + request.absoluteURI() + " for " + dataCallUri);
            Map serviceData = new Gson().fromJson(responseContent, Map.class);
            applyData(serviceData);
            observableRequest.onFinish();
            templateHandler.finishIfLast(request);
        });
    }

    private void applyData(Map serviceData) {
        snippetTemplateMap.entrySet().forEach(entry -> {
            try {
                String compiledContent = entry.getValue().apply(serviceData);
                Element snippetParent = new Element(Tag.valueOf("div"), "");
                if (templateDebug) {
                    String debugComment = "<!-- webservice `" + dataCallUri + "` call -->";
                    snippetParent.prepend(debugComment);
                }
                entry.getKey().replaceWith(snippetParent.append(compiledContent));
            } catch (IOException e) {
                LOGGER.error("Can't apply response to template!", e);
            }
        });
    }
}
