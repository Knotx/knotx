/*
 * Knot.x - Reactive microservice assembler - Action Knot Verticle
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
package com.cognifide.knotx.knot.action.impl;

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.knot.action.ActionKnotConfiguration;
import com.cognifide.knotx.knot.action.ActionKnotVerticle;
import com.cognifide.knotx.knot.action.FormConfigurationException;
import com.cognifide.knotx.knot.action.handler.FormActionHandler;
import com.cognifide.knotx.knot.action.handler.GetActionHandler;
import com.cognifide.knotx.knot.api.AbstractKnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import java.util.NoSuchElementException;
import java.util.Set;
import rx.Observable;

public class ActionKnotProxyImpl extends AbstractKnotProxy<ActionKnotConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionKnotVerticle.class);

  private final Vertx vertx;
  private final ActionKnotConfiguration configuration;

  public ActionKnotProxyImpl(Vertx vertx, ActionKnotConfiguration configuration) {
    super(configuration);
    this.vertx = vertx;
    this.configuration = configuration;
  }

  @Override
  protected Observable<KnotContext> processRequest(KnotContext knotContext) {
    if (HttpMethod.POST.equals(knotContext.getClientRequest().getMethod())) {
      return new FormActionHandler(vertx, configuration).process(knotContext);
    } else {
      return new GetActionHandler(configuration).process(knotContext);
    }
  }

  @Override
  protected boolean shouldProcess(Set<String> knots) {
    return true;
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    KnotContext errorResponse = new KnotContext().setClientResponse(knotContext.getClientResponse());
    HttpResponseStatus statusCode;
    if (error instanceof NoSuchElementException) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else if (error instanceof FormConfigurationException) {
      LOGGER.error("Form incorrectly configured [{}]", knotContext.getClientRequest());
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    errorResponse.getClientResponse().setStatusCode(statusCode.code());
    return errorResponse;
  }

}
