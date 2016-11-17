/*
 * Knot.x - Reactive microservice assembler - View Knot
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
package com.cognifide.knotx.knot.service;

import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.knot.api.AbstractKnot;
import com.cognifide.knotx.knot.service.impl.TemplateEngine;
import com.cognifide.knotx.knot.service.impl.TemplateSnippetProcessor;
import com.cognifide.knotx.knot.service.parser.HtmlFragment;
import com.cognifide.knotx.knot.service.parser.RawHtmlFragment;
import com.cognifide.knotx.knot.service.parser.TemplateHtmlFragment;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class ServiceKnotVerticle extends AbstractKnot<ServiceKnotConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceKnotVerticle.class);

  private TemplateSnippetProcessor snippetProcessor;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.snippetProcessor = new TemplateSnippetProcessor(this.vertx.eventBus(), configuration);
  }

  @Override
  protected ServiceKnotConfiguration initConfiguration(JsonObject config) {
    return new ServiceKnotConfiguration(config);
  }

  @Override
  protected void process(Message<KnotContext> message, Handler<KnotContext> handler) {
    KnotContext inputContext = message.body();
    inputContext.fragments()
        .map(fragments -> Observable.from(fragments)
//            .filter(fragment -> fragment.getId().matches("templating|form.*"))
            .doOnNext(this::traceFragment)
            .flatMap(this::compileHtmlFragment)
            .flatMap(compiledFragment -> snippetProcessor.processSnippet(compiledFragment, inputContext))
            .subscribe(next -> {
                },
                error -> message.reply(processError(inputContext, error)),
                () -> handler.handle(createSuccessResponse(inputContext))));
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    LOGGER.error("Error happened during Template processing", error);
    ClientResponse errorResponse = new ClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);

    return new KnotContext()
        .setClientRequest(knotContext.clientRequest())
        .setClientResponse(errorResponse);
  }

  private KnotContext createSuccessResponse(KnotContext inputContext) {
    ClientResponse clientResponse = inputContext.clientResponse();

    return new KnotContext()
        .setClientRequest(inputContext.clientRequest())
        .setClientResponse(clientResponse);
  }

  private Observable<HtmlFragment> compileHtmlFragment(Fragment fragment) {
    if (!fragment.isRaw()) {
      return Observable.create(subscriber -> {
        subscriber.onNext(new TemplateHtmlFragment(fragment));
        subscriber.onCompleted();
      });
    } else {
      return Observable.just(new RawHtmlFragment(fragment));
    }
  }

  private void traceFragment(Fragment fragment) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Processing fragment {}", fragment.toJson().encodePrettily());
    }
  }

}
