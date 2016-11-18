/*
 * Knot.x - Reactive microservice assembler - Handlebars Knot
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
package com.cognifide.knotx.templating;

import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.handlebars.CustomHandlebarsHelper;
import com.cognifide.knotx.knot.api.AbstractKnot;
import com.cognifide.knotx.templating.helpers.DefaultHandlebarsHelpers;
import com.cognifide.knotx.templating.wrappers.HtmlFragment;
import com.cognifide.knotx.templating.wrappers.RawHtmlFragment;
import com.cognifide.knotx.templating.wrappers.TemplateHtmlFragment;
import com.github.jknack.handlebars.Handlebars;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ServiceLoader;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class HandlebarsKnotVerticle extends AbstractKnot<HandlebarsKnotConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(HandlebarsKnotVerticle.class);

  private static final String START_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- start compiled snippet -->";

  private static final String END_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- end compiled snippet -->";

  private Handlebars handlebars;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    initHandlebars();
  }

  @Override
  protected void process(Message<KnotContext> message, Handler<KnotContext> handler) {
    final KnotContext inputContext = message.body();
    inputContext.fragments()
        .map(
            fragments -> Observable.from(fragments)
                .flatMap(this::compileHtmlFragment)
                .concatMapEager(this::applyData)
                // eager will buffer faster processing to emit items in proper order, keeping concurrency.
                .reduce(new StringBuilder(), StringBuilder::append)
                .map(StringBuilder::toString)

        ).orElse(Observable.just(StringUtils.EMPTY))
        .subscribe(result -> handler.handle(createSuccessResponse(inputContext, result)),
            error -> handler.handle(processError(inputContext, error)));
  }

  private KnotContext createSuccessResponse(KnotContext inputContext, String renderedContent) {
    ClientResponse clientResponse = inputContext.clientResponse();
    MultiMap headers = clientResponse.headers();
    headers.set(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(renderedContent.length()));
    clientResponse.setBody(Buffer.buffer(renderedContent)).setHeaders(headers);

    return new KnotContext()
        .setClientRequest(inputContext.clientRequest())
        .setClientResponse(clientResponse);
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    return null;
  }

  @Override
  protected HandlebarsKnotConfiguration initConfiguration(JsonObject config) {
    return new HandlebarsKnotConfiguration(config);
  }

  private Observable<String> applyData(HtmlFragment snippet) {
    LOGGER.trace("Applying data to snippet {}", snippet);

    return Observable.just(startComment() +
        snippet.getContentWithContext(snippet.getFragment().getContext()) +
        endComment());
  }

  private String startComment() {
    return snippetComment(START_WEBSERVICE_CALL_DEBUG_MARKER);
  }

  private String endComment() {
    return snippetComment(END_WEBSERVICE_CALL_DEBUG_MARKER);
  }

  private String snippetComment(String commentTemplate) {
    String debugLine = StringUtils.EMPTY;
    if (configuration.templateDebug()) {
      debugLine = commentTemplate;
    }
    return debugLine;
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

  private Observable<HtmlFragment> compileHtmlFragment(Fragment fragment) {
    if (!fragment.isRaw()) {
      return Observable.create(subscriber -> {
        try {
          subscriber.onNext(new TemplateHtmlFragment(fragment).compileWith(handlebars));
          subscriber.onCompleted();
        } catch (IOException e) {
          subscriber.onError(e);
        }
      });
    } else {
      return Observable.just(new RawHtmlFragment(fragment));
    }
  }

}
