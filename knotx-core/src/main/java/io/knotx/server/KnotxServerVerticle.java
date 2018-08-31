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

import io.knotx.server.configuration.KnotxServerOptions;
import io.knotx.server.configuration.RoutingOperationOptions;
import io.knotx.server.handler.api.RoutingHandlerFactory;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.SingleSource;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.RxHelper;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

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
    LOGGER.info("Open API specification location [{}]",
        options.getRoutingSpecificationLocation());

    OpenAPI3RouterFactory.rxCreate(vertx, options.getRoutingSpecificationLocation())
        .doOnSuccess(this::registerHandlers)
        .map(OpenAPI3RouterFactory::getRouter)
        .doOnSuccess(this::logRouterRoutes)
        .flatMap(this::configureHttpServer)
        .subscribe(
            ok -> {
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

  private void registerHandlers(OpenAPI3RouterFactory routerFactory) {
    List<RoutingHandlerFactory> handlerFactories = loadRoutingHandlerFactories();
    options.getRoutingOperations().forEach(options -> {
      registerHandlersPerOperation(routerFactory, handlerFactories, options);
      registerFailureHandlersPerOperation(routerFactory, handlerFactories, options);
      LOGGER.info("Registered handlers [{}]", options);
    });
  }

  private void registerHandlersPerOperation(OpenAPI3RouterFactory routerFactory,
      List<RoutingHandlerFactory> handlerFactories,
      RoutingOperationOptions options) {
    options.getHandlers().forEach(routingHandlerOptions ->
        handlerFactories.stream()
            .filter(
                handlerFactory -> handlerFactory.getName()
                    .equals(routingHandlerOptions.getName()))
            .findAny()
            .map(handlerFactory ->
                routerFactory
                    .addHandlerByOperationId(options.getOperationId(),
                        handlerFactory.create(vertx, routingHandlerOptions.getConfig()))
            )
            .orElseThrow(IllegalStateException::new)
    );
  }

  private void registerFailureHandlersPerOperation(OpenAPI3RouterFactory openAPI3RouterFactory,
      List<RoutingHandlerFactory> handlerFactories,
      RoutingOperationOptions routingOperationOptions) {
    routingOperationOptions.getFailureHandlers().forEach(routingHandlerOptions ->
        handlerFactories.stream()
            .filter(
                handlerFactory -> handlerFactory.getName()
                    .equals(routingHandlerOptions.getName()))
            .findAny()
            .map(handlerFactory ->
                openAPI3RouterFactory
                    .addFailureHandlerByOperationId(routingOperationOptions.getOperationId(),
                        handlerFactory.create(vertx, routingHandlerOptions.getConfig()))
            )
            .orElseThrow(IllegalStateException::new)
    );
  }

  private SingleSource<? extends HttpServer> configureHttpServer(Router router) {
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

    return httpServer.rxListen();
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

  private List<RoutingHandlerFactory> loadRoutingHandlerFactories() {
    List<RoutingHandlerFactory> routingFactories = new ArrayList<>();
    ServiceLoader.load(RoutingHandlerFactory.class)
        .iterator()
        .forEachRemaining(routingFactories::add);

    LOGGER.info("Routing handler factory names [{}] registered.",
        routingFactories.stream().map(RoutingHandlerFactory::getName).collect(Collectors
            .joining(",")));

    return routingFactories;
  }

  private void logRouterRoutes(Router router) {
    LOGGER.info("Routes [{}]", router.getRoutes());
    printRoutes(router);
  }

  private void printRoutes(Router router) {
    // @formatter:off
    System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    System.out.println("@@                              ROUTER CONFIG                                 @@");
    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    router.getRoutes().forEach(route -> System.out.println("@@     " + route.getDelegate()));
    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
    // @formatter:on
  }

}
