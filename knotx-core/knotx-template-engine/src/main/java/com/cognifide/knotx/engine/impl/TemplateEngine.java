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

import com.cognifide.knotx.dataobjects.RenderRequest;
import com.cognifide.knotx.engine.TemplateEngineConfiguration;
import com.cognifide.knotx.engine.parser.HtmlFragment;
import com.cognifide.knotx.engine.parser.HtmlParser;
import com.cognifide.knotx.engine.parser.TemplateHtmlFragment;
import com.cognifide.knotx.handlebars.CustomHandlebarsHelper;
import com.github.jknack.handlebars.Handlebars;

import java.io.IOException;
import java.util.ServiceLoader;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import rx.Observable;

public class TemplateEngine {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngine.class);

  private Handlebars handlebars;

  private TemplateSnippetProcessor snippetProcessor;

  public TemplateEngine(EventBus eventBus, TemplateEngineConfiguration configuration) {
    this.snippetProcessor = new TemplateSnippetProcessor(eventBus, configuration);
    initHandlebars();
  }

  public Observable<String> process(RenderRequest renderRequest) {
    return extractFragments(renderRequest.template())
        .doOnNext(this::traceSnippet)
        .flatMap(this::compileHtmlFragment)
        .concatMapEager(htmlFragment -> snippetProcessor.processSnippet(htmlFragment, renderRequest))
        // eager will buffer faster processing to emit items in proper order, keeping concurrency.
        .reduce(new StringBuilder(), StringBuilder::append)
        .map(StringBuilder::toString);
  }

  private void initHandlebars() {
    handlebars = new Handlebars();
    DefaultHandlebarsHelpers.registerFor(handlebars);

    ServiceLoader.load(CustomHandlebarsHelper.class)
        .iterator().forEachRemaining(helper -> {
      handlebars.registerHelper(helper.getName(), helper);
      LOGGER.info("Registered custom Handlebars helper: {}", helper.getName());
    });
  }

  private Observable<HtmlFragment> extractFragments(String template) {
    return Observable.from(new HtmlParser(template).getFragments());
  }

  private Observable<HtmlFragment> compileHtmlFragment(HtmlFragment fragment) {
    if (fragment.hasHandlebarsTemplate()) {
      return Observable.create(subscriber -> {
        try {
          subscriber.onNext(
              new TemplateHtmlFragment(fragment.getContent()).compileWith(handlebars));
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
      LOGGER.trace("Processing snippet <{}>, <{}>",
          fragment.hasHandlebarsTemplate() ? "HBS" : "RAW", fragment.getContent());
    }
  }
}
