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
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * A generic Fragment processing handler.
 */
public interface RequestContextEngine {

  /**
   * Process RequestEventHandlerResult and saves RequestContext to the RoutingContext afterwards
   * @param result - RequestEventHandlerResult to process
   * @param context - handlers routing context
   * @param requestContext - current request context
   */
  void processAndSaveResult(RequestEventHandlerResult result, RoutingContext context,
      RequestContext requestContext);

  /**
   * Enables to handle error that occurred during fragment context handling
   *
   * @param context - vert.x web context that contains payload passed between handlers
   * @param requestContext - knot.x request context from the previous handler
   * @param e - exception
   */
  void handleFatal(RoutingContext context, RequestContext requestContext, Throwable e);


  /**
   * @return unique identifier of this handler
   */
  public String getHandlerId();

}
