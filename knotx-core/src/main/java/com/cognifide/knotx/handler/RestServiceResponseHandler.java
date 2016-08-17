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
package com.cognifide.knotx.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.cognifide.knotx.event.ObservableRequest;
import com.cognifide.knotx.template.TemplateHandler;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerRequest;

public class RestServiceResponseHandler implements Handler<HttpClientResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceResponseHandler.class);

    private final HttpServerRequest request;

    private final List<Element> snippets;

    private final TemplateHandler templateHandler;

    private final String dataCallUri;

    private final ObservableRequest observableRequest;

    private final Handlebars handlebars;

    private final boolean templateDebug;

    public RestServiceResponseHandler(HttpServerRequest request, Entry<String, List<Element>> snippetGroup,
                                      TemplateHandler templateHandler, ObservableRequest observableRequest,
                                      Handlebars handlebars, boolean templateDebug) {
        this.request = request;
        this.snippets = snippetGroup.getValue();
        this.templateHandler = templateHandler;
        this.dataCallUri = snippetGroup.getKey();
        this.observableRequest = observableRequest;
        this.handlebars = handlebars;
        this.templateDebug = templateDebug;
    }

    @Override
    public void handle(HttpClientResponse response) {
        response.bodyHandler(buffer -> {
            String responseContent = buffer.getString(0, buffer.length());
            LOGGER.debug("Request in: " + request.absoluteURI() + " for " + dataCallUri);
            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> serviceData = new Gson().fromJson(responseContent, mapType);
            applyData(serviceData);
            observableRequest.onFinish();
            templateHandler.finishIfLast();
        });
    }

    private void applyData(Map<String, Object> serviceData) {
        snippets.forEach(snippet -> {
            try {
                Template template = compile(snippet.html());
                String compiledContent = template.apply(serviceData);
                Element snippetParent = new Element(Tag.valueOf("div"), "");
                if (templateDebug) {
                    String debugComment = "<!-- webservice `" + dataCallUri + "` call -->";
                    snippetParent.prepend(debugComment);
                }
                snippet.replaceWith(snippetParent.append(compiledContent));
            } catch (IOException e) {
                LOGGER.error("Cannot apply response to template!", e);
            }
        });
    }

    private Template compile(String html) throws IOException {
        return handlebars.compileInline(html);
    }
}
