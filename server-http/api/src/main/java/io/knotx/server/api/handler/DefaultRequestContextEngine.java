/*
 * Copyright (C) 2019 Cognifide Limited
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
package io.knotx.server.api.handler;

import io.knotx.server.api.context.RequestContext;
import io.knotx.server.api.context.RequestEvent;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * A generic Fragment processing handler.
 */
public class DefaultRequestContextEngine implements RequestContextEngine {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRequestContextEngine.class);
  private final String handlerId;

  public DefaultRequestContextEngine(String handlerId) {
    this.handlerId = handlerId;
  }

  @Override
  public void processAndSaveResult(RequestEventHandlerResult result, RoutingContext context,
      RequestContext requestContext) {
    traceMessage(result);
    updateRequestContextClientResponse(requestContext, result);
    if (isSuccess(result)) {
      next(context, requestContext, result.getRequestEvent().get());
    } else {
      fail(context, requestContext, result.getStatusCode(), result.getErrorMessage());
    }
    context.put(RequestContext.KEY, requestContext);
  }

  @Override
  public void handleFatal(RoutingContext context, RequestContext requestContext, Throwable e) {
    context.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    requestContext.fatal(getHandlerId());
    LOGGER.error("{} failed to process {}", getHandlerId(),
        requestContext.getRequestEvent().getClientRequest().getPath(), e);
  }

  @Override
  public String getHandlerId() {
    return handlerId;
  }

  protected boolean isSuccess(RequestEventHandlerResult result) {
    return result.getRequestEvent().isPresent();
  }

  protected void next(RoutingContext context, RequestContext requestContext,
      RequestEvent requestEvent) {
    requestContext.success(getHandlerId(), requestEvent);
    context.next();
  }

  protected void fail(RoutingContext context, RequestContext requestContext,
      Integer statusCode, String errorMessage) {
    LOGGER.warn("{} processing failed: {}", getHandlerId(), errorMessage);
    requestContext.failure(getHandlerId(), errorMessage);
    context.fail(statusCode != null ? statusCode : HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
  }

  protected void updateRequestContextClientResponse(RequestContext requestContext,
      RequestEventHandlerResult result) {
    requestContext.setBody(result.getBody())
        .setHeaders(result.getHeaders())
        .setStatusCode(result.getStatusCode());
  }

  private void traceMessage(RequestEventHandlerResult ctx) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("{} processed context: {}", getHandlerId(), ctx);
    }
  }

}
