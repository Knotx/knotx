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
import io.vertx.reactivex.RxHelper;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CSRFHandler;
import io.vertx.reactivex.ext.web.handler.CookieHandler;
import io.vertx.reactivex.ext.web.handler.ErrorHandler;
import io.vertx.reactivex.ext.web.handler.LoggerHandler;

public class KnotxServerVerticle extends AbstractVerticle {

  public static final String KNOTX_PORT_PROP_NAME = "knotx.port";
  public static final String KNOTX_FILE_UPLOAD_DIR_PROPERTY = "knotx.fileUploadDir";

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxServerVerticle.class);

  private static final HttpResponseStatus BAD_REQUEST = HttpResponseStatus.BAD_REQUEST;

  private KnotxServerOptions options;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    options = new KnotxServerOptions(config());
  }

  @Override
  public void start(Future<Void> fut) {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    KnotxCSRFOptions csrfConfig = options.getCsrfConfig();
    CSRFHandler csrfHandler = CSRFHandler.create(csrfConfig.getSecret())
        .setNagHttps(true) //Generates warning message in log if https is not used
        .setCookieName(csrfConfig.getCookieName())
        .setCookiePath(csrfConfig.getCookiePath())
        .setHeaderName(csrfConfig.getHeaderName())
        .setTimeout(csrfConfig.getTimeout());

    Router router = Router.router(vertx);
    if (options.getAccessLog().isEnabled()) {
      router.route().handler(LoggerHandler.create(options.getAccessLog().isImmediate(),
          options.getAccessLog().getFormat()));
    }
    router.route().handler(KnotxHeaderHandler.create(options));
    router.route().handler(SupportedMethodsAndPathsHandler.create(options));
    router.route().handler(CookieHandler.create());
    router.route().handler(BodyHandler.create(options.getFileUploadDirectory())
        .setBodyLimit(options.getFileUploadLimit()));

    router.route().handler(KnotxContextHandler.create());

    options.getDefaultFlow().getRouting().forEach((key, value) -> {
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
                .handler(KnotxRepositoryHandler.create(vertx, options));

            router.route()
                .method(method)
                .pathRegex(criteria.getPath())
                .handler(KnotxSplitterHandler.create(vertx, options));

            router.route()
                .method(method)
                .pathRegex(criteria.getPath())
                .handler(KnotxEngineHandler
                    .create(vertx, options, criteria.getAddress(),
                        criteria.getOnTransition()));

            router.route()
                .method(method)
                .pathRegex(criteria.getPath())
                .handler(KnotxAssemblerHandler.create(vertx, options));
          }
      );
    });

    if (options.getCustomFlow() != null) {
      options.getCustomFlow().getRouting().forEach((key, value) -> {
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
                          .create(vertx, options, criteria.getAddress()));

              router.route()
                  .method(method)
                  .pathRegex(criteria.getPath())
                  .handler(KnotxEngineHandler
                      .create(vertx, options, criteria.getAddress(),
                          criteria.getOnTransition()));

              router.route()
                  .method(method)
                  .pathRegex(criteria.getPath())
                  .handler(KnotxGatewayResponseProviderHandler.create(vertx, options));
            }
        );
      });
    }

    router.route().failureHandler(ErrorHandler.create(options.isDisplayExceptionDetails()));

    HttpServer httpServer = vertx.createHttpServer(options.getServerOptions());

    if (options.isDropRequests()) {
      httpServer.requestStream().toFlowable()
          .map(HttpServerRequest::pause)
          .onBackpressureBuffer(options.getBackpressureBufferCapacity(),
              () -> LOGGER.warn("Backpressure buffer is overflown. Dropping request"),
              options.getBackpressureStrategy())
          .onBackpressureDrop(
              req -> req.response().setStatusCode(options.getDropRequestResponseCode()).end())
          .observeOn(RxHelper.scheduler(vertx.getDelegate()))
          .subscribe(req -> {
            req.resume();
            routeSafe(req, router);
          }, error -> LOGGER.error("Exception while processing!", error));
    } else {
      httpServer
          .requestHandler(req -> routeSafe(req, router));
    }

    httpServer.rxListen().subscribe(ok -> {
          LOGGER.info("Knot.x HTTP Server started. Listening on port {}",
              options.getServerOptions().getPort());
          fut.complete();
        },
        error -> {
          LOGGER.error("Unable to start Knot.x HTTP Server.", error.getCause());
          fut.fail(error);
        }
    );
  }

  private void routeSafe(HttpServerRequest req, Router router) {
    try {
      router.accept(req);
    } catch (IllegalArgumentException ex) {
      LOGGER.warn("Problem decoding Query String ", ex);

      req.response()
          .setStatusCode(BAD_REQUEST.code())
          .setStatusMessage(BAD_REQUEST.reasonPhrase())
          .end("Invalid characters in Query Parameter");
    }
  }
}
