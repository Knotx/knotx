/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.knot.service.impl;

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.knot.AbstractKnotProxy;
import io.knotx.knot.service.ServiceKnotOptions;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class ServiceKnotProxyImpl extends AbstractKnotProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceKnotProxyImpl.class);

  private static final String SUPPORTED_FRAGMENT_ID = "services";

  private FragmentProcessor snippetProcessor;

  public ServiceKnotProxyImpl(Vertx vertx, ServiceKnotOptions options) {
    this.snippetProcessor = new FragmentProcessor(vertx, options);
  }

  @Override
  protected Single<KnotContext> processRequest(KnotContext knotContext) {
    return Optional.ofNullable(knotContext.getFragments())
        .map(fragments ->
            Observable.fromIterable(fragments)
            .filter(fragment -> fragment.knots().contains(SUPPORTED_FRAGMENT_ID))
            .doOnNext(this::traceFragment)
            .map(FragmentContext::from)
            .flatMapSingle(
                fragmentContext -> snippetProcessor.processSnippet(fragmentContext, knotContext))
            .toList()
        ).orElse(Single.just(Collections.emptyList()))
        .map(result -> createSuccessResponse(knotContext))
        .onErrorReturn(error -> processError(knotContext, error));
  }

  @Override
  protected boolean shouldProcess(Set<String> knots) {
    return knots.contains(SUPPORTED_FRAGMENT_ID);
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    LOGGER.error("Error happened during Template processing", error);
    ClientResponse errorResponse = new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());

    return new KnotContext()
        .setClientRequest(knotContext.getClientRequest())
        .setClientResponse(errorResponse);
  }

  private KnotContext createSuccessResponse(KnotContext inputContext) {
    return new KnotContext()
        .setClientRequest(inputContext.getClientRequest())
        .setClientResponse(inputContext.getClientResponse())
        .setFragments(
            Optional.ofNullable(inputContext.getFragments()).orElse(Collections.emptyList()))
        .setTransition(DEFAULT_TRANSITION);
  }

  private void traceFragment(Fragment fragment) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Processing fragment {}", fragment.toJson().encodePrettily());
    }
  }
}
