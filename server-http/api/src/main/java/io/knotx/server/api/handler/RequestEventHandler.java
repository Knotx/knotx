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
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * A generic Fragment processing handler.
 */
public abstract class RequestEventHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestEventHandler.class);

  @Override
  public void handle(RoutingContext context) {
    RequestContext requestContext = context.get(RequestContext.KEY);
    try {
      RequestEventResult result = handle(requestContext.getRequestEvent());
      traceMessage(result);
      if (isSuccess(result)) {
        next(context, requestContext, result);
      } else {
        fail(context, requestContext, result);
      }
    } catch (Exception e) {
      handleFatal(context, requestContext, e);
    }
  }

  /**
   * @param result - handling result
   * @return true if handle was successful
   */
  protected boolean isSuccess(RequestEventResult result) {
    return result.getRequestEvent().isPresent();
  }

  /**
   * Enables to handle error that occurred during fragment context handling
   *
   * @param context - vert.x web context that contains payload passed between handlers
   * @param requestContext - knot.x request context from the previous handler
   * @param e - exception
   */
  protected void handleFatal(RoutingContext context, RequestContext requestContext, Exception e) {
    context.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    requestContext.fatal(getHandlerId());
    LOGGER.error("{} failed to process {}", getHandlerId(),
        requestContext.getRequestEvent().getClientRequest().getPath(), e);
  }

  /**
   * Handle RequestEvent processing
   *
   * @param requestEvent - knot.x fragment context from the previous handler
   * @return RequestEventResult that contains handling result
   */
  protected abstract RequestEventResult handle(RequestEvent requestEvent);

  /**
   * @return unique identifier of this handler
   */
  protected String getHandlerId() {
    return getClass().getName();
  }

  private void traceMessage(RequestEventResult ctx) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("{} processed context: {}", getHandlerId(), ctx);
    }
  }

  private void next(RoutingContext context, RequestContext requestContext,
      RequestEventResult result) {
    requestContext.success(getHandlerId(), result.getRequestEvent().get());
    context.put(RequestContext.KEY, requestContext);
    context.next();
  }

  private void fail(RoutingContext context, RequestContext requestContext,
      RequestEventResult result) {
    requestContext.failure(getHandlerId(), result.getClientResponse());
    context.fail(result.getClientResponse().getStatusCode());
  }

}
