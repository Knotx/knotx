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
package io.knotx.server;

import io.knotx.server.configuration.KnotxFlowConfiguration;
import io.knotx.server.configuration.KnotxServerConfiguration;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.ext.web.RoutingContext;

public class SupportedMethodsAndPathsHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(SupportedMethodsAndPathsHandler.class);

  private final KnotxFlowConfiguration defaultFlow;

  private final KnotxFlowConfiguration customFlow;

  private SupportedMethodsAndPathsHandler(KnotxServerConfiguration configuration) {
    this.defaultFlow = configuration.getDefaultFlow();
    this.customFlow = configuration.getCustomFlow();
  }

  public static SupportedMethodsAndPathsHandler create(KnotxServerConfiguration configuration) {
    return new SupportedMethodsAndPathsHandler(configuration);
  }

  @Override
  public void handle(RoutingContext context) {
    final String path = context.request().path();
    final HttpMethod method = context.request().method();
    boolean pathSupportedByDefaultFlow = isPathSupportedByFlow(path, defaultFlow);
    boolean pathSupportedByCustomFlow = isPathSupportedByFlow(path, customFlow);
    if (pathSupportedByCustomFlow || pathSupportedByDefaultFlow) {
      final boolean methodAllowedInDefaultFlow = isMethodAllowedInFlow(method, defaultFlow);
      final boolean methodAllowedInCustomFlow = isMethodAllowedInFlow(method, customFlow);
      if (methodAllowedInDefaultFlow || methodAllowedInCustomFlow) {
        context.next();
      } else {
        LOGGER.warn("Requested method {} is not supported based on configuration", method);
        context.fail(HttpResponseStatus.METHOD_NOT_ALLOWED.code());
      }
    } else {
      LOGGER.warn("Requested path {} is not supported based on configuration", path);
      context.fail(HttpResponseStatus.NOT_FOUND.code());
    }
  }

  private boolean isMethodAllowedInFlow(HttpMethod method, KnotxFlowConfiguration flow) {
    return flow.getEngineRouting() != null && flow.getEngineRouting().keySet().stream().anyMatch(supportedMethod -> supportedMethod == method);
  }

  private boolean isPathSupportedByFlow(String path, KnotxFlowConfiguration flow) {
    return flow.getEngineRouting() != null && flow.getEngineRouting().values().stream().anyMatch(
        routingEntries -> routingEntries.stream()
            .anyMatch(item -> path.matches(item.path()))
    );
  }
}
