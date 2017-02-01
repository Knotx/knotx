/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.server;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;

public class SupportedMethodsAndPathsHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxSplitterHandler.class);


  private KnotxServerConfiguration configuration;

  private SupportedMethodsAndPathsHandler(KnotxServerConfiguration configuration) {
    this.configuration = configuration;
  }

  public static SupportedMethodsAndPathsHandler create(KnotxServerConfiguration configuration) {
    return new SupportedMethodsAndPathsHandler(configuration);
  }

  @Override
  public void handle(RoutingContext context) {
    boolean shouldRejectMethod = configuration.getEngineRouting().keySet().stream()
        .noneMatch(supportedMethod -> supportedMethod == context.request().method());

    boolean shouldRejectPath = configuration.getEngineRouting().values().stream().noneMatch(
        routingEntries -> routingEntries.stream()
            .anyMatch(item -> context.request().path().matches(item.path()))
    );

    if (shouldRejectMethod) {
      LOGGER.warn("Requested method {} is not supported based on configuration",
          context.request().method());
      context.fail(HttpResponseStatus.METHOD_NOT_ALLOWED.code());
    } else if (shouldRejectPath) {
      LOGGER.warn("Requested path {} is not supported based on configuration",
          context.request().path());
      context.fail(HttpResponseStatus.NOT_FOUND.code());
    } else {
      context.next();
    }
  }
}
