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

import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.FileData;
import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.rxjava.proxy.KnotProxy;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.FileUpload;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

class KnotxGatewayContextHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxGatewayContextHandler.class);
  private static final String KNOT_CONTEXT_KEY = "knotContext";

  private Vertx vertx;
  private String address;

  private KnotxGatewayContextHandler(Vertx vertx, String address) {
    this.vertx = vertx;
    this.address = address;
  }

  static KnotxGatewayContextHandler create(Vertx vertx, String address) {
    return new KnotxGatewayContextHandler(vertx, address);
  }

  @Override
  public void handle(RoutingContext context) {
    try {
      handleRoute(context, address);
    } catch (Exception ex) {
      LOGGER.error("Something very unexpected happened", ex);
      context.fail(ex);
    }
  }

  private void handleRoute(final RoutingContext context, final String address) {
    KnotContext knotContext = new KnotContext()
        .setClientRequest(new ClientRequest(context.request()));
    String bodyAsString = context.getBodyAsString();
    if (StringUtils.isNotBlank(bodyAsString)) {
      knotContext.setFragments(Collections.singletonList(Fragment.raw(bodyAsString)));
    }
    Set<FileUpload> fileUploads = context.fileUploads();
    if (fileUploads != null) {
      knotContext.setFilesData(fileUploads.stream().map(fileUpload -> {
        return new FileData(fileUpload);
      }).collect(Collectors.toSet()));
    }

    KnotProxy knot = KnotProxy.createProxy(vertx, address);

    knot.rxProcess(knotContext)
        .doOnSuccess(ctx -> context.put(KNOT_CONTEXT_KEY, ctx))
        .subscribe(
            ctx -> {
              context.put(KNOT_CONTEXT_KEY, ctx);
              context.next();
            },
            error -> {
              LOGGER.error("Error happened while communicating with {} engine", error, address);
              context.fail(error);
            }
        );
  }

}
