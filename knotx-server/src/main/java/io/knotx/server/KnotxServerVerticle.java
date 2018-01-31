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

import io.knotx.server.configuration.KnotxCSRFOptions;
import io.knotx.server.configuration.KnotxServerOptions;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CSRFHandler;
import io.vertx.reactivex.ext.web.handler.CookieHandler;
import io.vertx.reactivex.ext.web.handler.ErrorHandler;
import io.vertx.reactivex.ext.web.handler.LoggerHandler;

public class KnotxServerVerticle extends AbstractVerticle {

  public static final String KNOTX_PORT_PROP_NAME = "knotx.port";

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxServerVerticle.class);

  private static final HttpResponseStatus BAD_REQUEST = HttpResponseStatus.BAD_REQUEST;

  private KnotxServerOptions configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new KnotxServerOptions(config());
  }

  @Override
  public void start(Future<Void> fut) {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    KnotxCSRFOptions csrfConfig = configuration.getCsrfConfig();
    CSRFHandler csrfHandler = CSRFHandler.create(csrfConfig.getSecret())
        .setNagHttps(true) //Generates warning message in log if https is not used
        .setCookieName(csrfConfig.getCookieName())
        .setCookiePath(csrfConfig.getCookiePath())
        .setHeaderName(csrfConfig.getHeaderName())
        .setTimeout(csrfConfig.getTimeout());

    Router router = Router.router(vertx);
    if (configuration.getAccessLog().isEnabled()) {
      router.route().handler(LoggerHandler.create(configuration.getAccessLog().isImmediate(),
          configuration.getAccessLog().getFormat()));
    }
    router.route().handler(KnotxHeaderHandler.create(configuration));
    router.route().handler(SupportedMethodsAndPathsHandler.create(configuration));
    router.route().handler(CookieHandler.create());
    router.route().handler(BodyHandler.create(configuration.getFileUploadDirectory())
        .setBodyLimit(configuration.getFileUploadLimit()));

    router.route().handler(KnotxContextHandler.create());

    configuration.getDefaultFlow().getRouting().forEach((key, value) -> {
      value.getItems().forEach(
          criteria -> {
            HttpMethod method = HttpMethod.valueOf(key.toUpperCase());
            if (criteria.isCsrf()) {
              router.route().method(method)
                  .pathRegex(criteria.getPath())
                  .handler(csrfHandler);
            }
            router.route()
                .method(method)
                .pathRegex(criteria.getPath())
                .handler(KnotxRepositoryHandler.create(vertx, configuration));

            router.route()
                .method(method)
                .pathRegex(criteria.getPath())
                .handler(KnotxSplitterHandler.create(vertx, configuration));

            router.route()
                .method(method)
                .pathRegex(criteria.getPath())
                .handler(KnotxEngineHandler
                    .create(vertx, configuration, criteria.getAddress(),
                        criteria.getOnTransition()));

            router.route()
                .method(method)
                .pathRegex(criteria.getPath())
                .handler(KnotxAssemblerHandler.create(vertx, configuration));
          }
      );
    });

    if (configuration.getCustomFlow() != null) {
      configuration.getCustomFlow().getRouting().forEach((key, value) -> {
        value.getItems().forEach(
            criteria -> {
              HttpMethod method = HttpMethod.valueOf(key.toUpperCase());
              if (criteria.isCsrf()) {
                router.route().method(method)
                    .pathRegex(criteria.getPath())
                    .handler(csrfHandler);
              }
              router.route().method(method)
                  .pathRegex(criteria.getPath())
                  .handler(
                      KnotxGatewayContextHandler
                          .create(vertx, configuration, criteria.getAddress()));

              router.route()
                  .method(method)
                  .pathRegex(criteria.getPath())
                  .handler(KnotxEngineHandler
                      .create(vertx, configuration, criteria.getAddress(),
                          criteria.getOnTransition()));

              router.route()
                  .method(method)
                  .pathRegex(criteria.getPath())
                  .handler(KnotxGatewayResponseProviderHandler.create(vertx, configuration));
            }
        );
      });
    }

    router.route().failureHandler(ErrorHandler.create(configuration.isDisplayExceptionDetails()));

    vertx.createHttpServer(configuration.getServerOptions())
        .requestHandler(request -> {
          try {
            router.accept(request);
          } catch (IllegalArgumentException ex) {
            LOGGER.warn("Problem decoding Query String", ex);

            request.response()
                .setStatusCode(BAD_REQUEST.code())
                .setStatusMessage(BAD_REQUEST.reasonPhrase())
                .end("Invalid characters in Query Parameter");
          }
        })
        .rxListen()
        .subscribe(ok -> {
              LOGGER.info("Knot.x HTTP Server started. Listening on port {}",
                  configuration.getServerOptions().getPort());
              fut.complete();
            },
            error -> {
              LOGGER.error("Unable to start Knot.x HTTP Server.", error.getCause());
              fut.fail(error);
            }
        );

  }
}
