/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
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
package com.cognifide.knotx.engine.impl;

import com.cognifide.knotx.api.TemplateEngineRequest;
import com.cognifide.knotx.engine.TemplateEngineConfiguration;
import com.cognifide.knotx.engine.parser.TemplateHtmlFragment;
import com.cognifide.knotx.engine.parser.HtmlFragment;
import com.cognifide.knotx.engine.parser.HtmlParser;
import com.github.jknack.handlebars.Handlebars;

import java.io.IOException;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

public class TemplateEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngine.class);

    private Handlebars handlebars;

    private TemplateSnippetProcessor snippetProcessor;

    public TemplateEngine(Vertx vertx, TemplateEngineConfiguration configuration) {
        this.snippetProcessor = new TemplateSnippetProcessor(vertx, configuration);
        initHandlebars();
    }

    private void initHandlebars() {
        handlebars = new Handlebars();
        HandlebarsHelpers.register(handlebars);
    }

    public Observable<String> process(TemplateEngineRequest request) {
        return extractFragments(request.getTemplate())
                .doOnNext(this::traceSnippet)
                .flatMap(this::compileHtmlFragment)
                .concatMapEager(item ->
                        snippetProcessor.processSnippet(item, request)
                ) //eager will buffer faster processing to emit items in proper order, keeping concurrency.
                .reduce(new StringBuilder(),
                        (builder, fragment) -> builder.append(fragment)
                )
                .map(item -> item.toString());
    }

    private Observable<HtmlFragment> extractFragments(String template) {
        return Observable.from(new HtmlParser(template).getFragments());
    }

    private Observable<HtmlFragment> compileHtmlFragment(HtmlFragment fragment) {
        if (fragment.hasHandlebarsTemplate()) {
            return Observable.create(subscriber -> {
                try {
                    subscriber.onNext(
                            new TemplateHtmlFragment(fragment.getContent()).compileWith(handlebars)
                    );
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            });
        } else {
            return Observable.just(fragment);
        }
    }

    private void traceSnippet(HtmlFragment fragment) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Processing snippet <{}>, <{}>", fragment.hasHandlebarsTemplate() ? "HBS" : "RAW", fragment.getContent());
        }
    }
}
