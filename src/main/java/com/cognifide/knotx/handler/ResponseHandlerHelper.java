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

import com.cognifide.knotx.event.ObservableRequest;
import com.cognifide.knotx.template.TemplateHandler;
import com.github.jknack.handlebars.Template;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import io.vertx.core.http.HttpServerRequest;

public class ResponseHandlerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandlerHelper.class);

    private final ObservableRequest observableRequest;

    private final HttpServerRequest request;

    private final TemplateHandler templateHandler;

    private final Element snippet;

    private final String dataCallUri;

    private final Template template;

    private final boolean templateDebug;

    public ResponseHandlerHelper(ObservableRequest observableRequest, HttpServerRequest request, TemplateHandler templateHandler, Element snippet, String dataCallUri, Template template, boolean templateDebug) {
        this.observableRequest = observableRequest;
        this.request = request;
        this.templateHandler = templateHandler;
        this.snippet = snippet;
        this.dataCallUri = dataCallUri;
        this.template = template;
        this.templateDebug = templateDebug;
    }

    public void applyData(Map<String, Object> map) {
        try {
            String compiledContent = template.apply(map);
            Element snippetParent = new Element(Tag.valueOf("div"), "");
            if (templateDebug) {
                String debugComment = "<!-- webservice `" + dataCallUri + "` call -->";
                snippetParent.prepend(debugComment);
            }
            snippet.replaceWith(snippetParent.append(compiledContent));
        } catch (IOException e) {
            LOGGER.error("Can't apply response to template!", e);
        } finally {
            observableRequest.onFinish();
            templateHandler.finishIfLast(request);
        }
    }
}
