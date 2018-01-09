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

import io.knotx.server.configuration.KnotxFlowSettings;
import io.knotx.server.configuration.KnotxServerOptions;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.ext.web.RoutingContext;

public class SupportedMethodsAndPathsHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(SupportedMethodsAndPathsHandler.class);

  private final KnotxFlowSettings defaultFlow;

  private final KnotxFlowSettings customFlow;

  private SupportedMethodsAndPathsHandler(KnotxServerOptions configuration) {
    this.defaultFlow = configuration.getDefaultFlow();
    this.customFlow = configuration.getCustomFlow();
  }

  public static SupportedMethodsAndPathsHandler create(KnotxServerOptions configuration) {
    return new SupportedMethodsAndPathsHandler(configuration);
  }

  @Override
  public void handle(RoutingContext context) {
    final String path = context.request().path();
    final HttpMethod method = context.request().method();

    if (isPathSupportedByFlow(path, defaultFlow)) {
      if (isMethodAllowedInFlow(method, defaultFlow)) {
        context.next();
      } else {
        LOGGER.warn("{} path supported by DefaultFlow. {} method not allowed.", path, method);
        context.fail(HttpResponseStatus.METHOD_NOT_ALLOWED.code());
      }
    } else if (isPathSupportedByFlow(path, customFlow)) {
      if (isMethodAllowedInFlow(method, customFlow)) {
        context.next();
      } else {
        LOGGER.warn("{} path supported by CustomFlow. {} method not allowed.", path, method);
        context.fail(HttpResponseStatus.METHOD_NOT_ALLOWED.code());
      }
    } else {
      LOGGER.warn("{} {} is not supported by default and custom flows.", method, path);
      context.fail(HttpResponseStatus.NOT_FOUND.code());
    }
  }

  private boolean isPathSupportedByFlow(final String path, KnotxFlowSettings flow) {
    return flow != null && flow.getRouting() != null && flow.getRouting().values().stream()
        .anyMatch(list -> list.getItems().stream().anyMatch(item -> path.matches(item.getPath())));
  }

  private boolean isMethodAllowedInFlow(final HttpMethod method, KnotxFlowSettings flow) {
    return flow != null & flow.getRouting() != null && flow.getRouting().keySet().stream()
        .anyMatch(item -> HttpMethod.valueOf(item.toUpperCase()) == method);
  }
}
