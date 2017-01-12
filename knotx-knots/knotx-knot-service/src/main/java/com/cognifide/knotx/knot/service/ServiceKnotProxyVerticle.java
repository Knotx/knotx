/*
 * Knot.x - Reactive microservice assembler - Service Knot
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
import com.cognifide.knotx.dataobjects.Fragment;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.knot.api.AbstractKnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import rx.Observable;

public class ServiceKnotProxyVerticle extends AbstractKnotProxy<ServiceKnotConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceKnotProxyVerticle.class);

  private static final String DEFAULT_TEMPLATING_KNOT = "next";

  private static final String SUPPORTED_FRAGMENT_ID = "services";

  private FragmentProcessor snippetProcessor;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.snippetProcessor = new FragmentProcessor(this.vertx.eventBus(), configuration);
  }

  @Override
  protected ServiceKnotConfiguration initConfiguration(JsonObject config) {
    return new ServiceKnotConfiguration(config);
  }

  @Override
  protected Observable<KnotContext> process(KnotContext message) {
    return Optional.ofNullable(message.getFragments())
        .map(fragments -> Observable.from(fragments)
            .filter(fragment -> fragment.knots().contains(SUPPORTED_FRAGMENT_ID))
            .doOnNext(this::traceFragment)
            .flatMap(this::compileHtmlFragment)
            .flatMap(compiledFragment -> snippetProcessor.processSnippet(compiledFragment, message))
            .toList()
        ).orElse(Observable.just(Collections.emptyList()))
        .map(result -> createSuccessResponse(message))
        .onErrorReturn(error -> processError(message, error));
  }

  @Override
  protected boolean shouldProcess(Set<String> knots) {
    return knots.contains(SUPPORTED_FRAGMENT_ID);
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    LOGGER.error("Error happened during Template processing", error);
    ClientResponse errorResponse = new ClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());

    return new KnotContext()
        .setClientRequest(knotContext.getClientRequest())
        .setClientResponse(errorResponse);
  }

  private KnotContext createSuccessResponse(KnotContext inputContext) {
    return new KnotContext()
        .setClientRequest(inputContext.getClientRequest())
        .setClientResponse(inputContext.getClientResponse())
        .setFragments(Optional.ofNullable(inputContext.getFragments()).orElse(Collections.emptyList()))
        .setTransition(DEFAULT_TEMPLATING_KNOT);
  }

  private Observable<FragmentContext> compileHtmlFragment(Fragment fragment) {
    return Observable.create(subscriber -> {
      subscriber.onNext(FragmentContext.from(fragment));
      subscriber.onCompleted();
    });
  }

  private void traceFragment(Fragment fragment) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Processing fragment {}", fragment.toJson().encodePrettily());
    }
  }

}
