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

import java.io.IOException;
import java.net.URISyntaxException;

import io.knotx.server.configuration.KnotxServerConfiguration;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.ErrorHandler;

public class KnotxServerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxServerVerticle.class);

  private KnotxServerConfiguration configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new KnotxServerConfiguration(config());
  }

  @Override
  public void start(Future<Void> fut) throws IOException, URISyntaxException {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    Router router = Router.router(vertx);
    router.route().handler(SupportedMethodsAndPathsHandler.create(configuration));

    configuration.getDefaultFlow().getEngineRouting().entrySet()
        .forEach(entry -> {
          if (entry.getKey() == HttpMethod.POST) {
            router.route().method(entry.getKey()).handler(BodyHandler.create());
          }
          entry.getValue().forEach(
              criteria -> {
                router.route()
                    .method(entry.getKey())
                    .pathRegex(criteria.path())
                    .handler(KnotxRepositoryHandler.create(vertx, configuration));

                router.route()
                    .method(entry.getKey())
                    .pathRegex(criteria.path())
                    .handler(KnotxSplitterHandler.create(vertx, configuration));

                router.route()
                    .method(entry.getKey())
                    .pathRegex(criteria.path())
                    .handler(KnotxEngineHandler
                        .create(vertx, criteria.address(), criteria.onTransition()));

                router.route()
                    .method(entry.getKey())
                    .pathRegex(criteria.path())
                    .handler(KnotxAssemblerHandler.create(vertx, configuration));
              }
          );
        });

    if(configuration.getCustomFlow().getEngineRouting() != null) {
      configuration.getCustomFlow().getEngineRouting().entrySet()
          .forEach(entry -> {
            if (entry.getKey() == HttpMethod.POST) {
              router.route().method(entry.getKey()).handler(BodyHandler.create());
            }
            entry.getValue().forEach(
                criteria -> {

                  router.route()
                      .method(entry.getKey())
                      .pathRegex(criteria.path())
                      .handler(KnotxEngineHandler
                          .create(vertx, criteria.address(), criteria.onTransition()));

                  router.route()
                      .method(entry.getKey())
                      .pathRegex(criteria.path())
                      .handler(KnotxGatewayResponseProviderHandler
                          .create(vertx, configuration));
                }
            );
          });
    }

    router.route().failureHandler(ErrorHandler.create(configuration.displayExceptionDetails()));

    vertx.createHttpServer()
        .requestHandler(router::accept)
        .rxListen(configuration.getHttpPort())
        .subscribe(ok -> {
              LOGGER.info("Knot.x HTTP Server started. Listening on port {}", configuration.getHttpPort());
              fut.complete();
            },
            error -> {
              LOGGER.error("Unable to start Knot.x HTTP Server.", error.getCause());
              fut.fail(error.getCause());
            }
        );

  }
}
