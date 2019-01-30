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

import io.knotx.server.api.context.FragmentsContext;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * A generic Fragment processing handler.
 */
public abstract class FragmentContextHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FragmentContextHandler.class);

  @Override
  public void handle(RoutingContext context) {
    FragmentsContext fragmentsContext = context.get(FragmentsContext.KEY);
    try {
      FragmentsContext result = handle(context, fragmentsContext);
      traceMessage(result);
      context.put(FragmentsContext.KEY, result);
    } catch (Exception e) {
      context.fail(fragmentsContext.getClientResponse().getStatusCode());
    } finally {
      context.next();
    }
  }

  /**
   * Handle FragmentsContext processing
   *
   * @param context - vert.x web context that contains payload passed between handlers
   * @param fragmentsContext - knot.x fragment context from the previous handler
   * @return FragmentsContext that is saved into routing context and passed to the next handler
   */
  protected abstract FragmentsContext handle(RoutingContext context,
      FragmentsContext fragmentsContext);

  private void traceMessage(FragmentsContext ctx) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("{} processed context: {}", getClass().getName(), ctx);
    }
  }

}
