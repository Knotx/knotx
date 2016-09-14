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
import com.cognifide.knotx.engine.TemplateEngineConsts;
import com.cognifide.knotx.engine.parser.HtmlFragment;
import com.cognifide.knotx.engine.parser.HtmlParser;
import com.cognifide.knotx.engine.parser.TemplateHtmlFragment;
import com.github.jknack.handlebars.Handlebars;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.http.HttpClient;
import rx.Observable;

public class TemplateEngine {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngine.class);

  private Handlebars handlebars;

  private TemplateSnippetProcessor snippetProcessor;

  public TemplateEngine(HttpClient httpClient, TemplateEngineConfiguration configuration) {
    this.snippetProcessor = new TemplateSnippetProcessor(httpClient, configuration);
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
        .filter(htmlFragment -> shouldProcessRequest(htmlFragment, request))
        .concatMapEager(htmlFragment ->
            snippetProcessor.processSnippet(htmlFragment, request)
        ) //eager will buffer faster processing to emit items in proper order, keeping concurrency.
        .reduce(new StringBuilder(),
            StringBuilder::append
        )
        .map(StringBuilder::toString);
  }

  private Boolean shouldProcessRequest(HtmlFragment htmlFragment, TemplateEngineRequest request) {
    String requestedWith = StringUtils.defaultString(request.getHeaders().get(TemplateEngineConsts.X_REQUESTED_WITH));
    String formId = StringUtils.defaultString(request.getFormAttributes().get(TemplateEngineConsts.FORM_ID_ATTRIBUTE));
    boolean isRequestByXHR = TemplateEngineConsts.XMLHTTP_REQUEST.equals(requestedWith);
    return !isRequestByXHR || StringUtils.isNotEmpty(htmlFragment.getDataId()) && formId.equals(htmlFragment.getDataId());
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
