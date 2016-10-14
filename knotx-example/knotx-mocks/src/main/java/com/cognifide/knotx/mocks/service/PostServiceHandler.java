/*
 * Knot.x - Mocked services for sample app
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.knotx.mocks.service;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import io.vertx.core.Handler;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;

public class PostServiceHandler implements Handler<RoutingContext> {
  private static final String SEPARATOR = "/";
  private final Logger LOGGER = LoggerFactory.getLogger(RoutingContext.class);
  private final FileSystem fileSystem;
  private String catalogue;

  public PostServiceHandler(String catalogue, FileSystem fileSystem) {
    this.catalogue = catalogue;
    this.fileSystem = fileSystem;
  }

  @Override
  public void handle(RoutingContext context) {
    String resourcePath = getFilePath(context.request());

    try {
      fileSystem.readFile(resourcePath, ar -> {
        if (ar.succeeded()) {
          JsonObject responseBody = new JsonObject(ar.result().toString());

          MultiMap formParams = context.request().params();
          formParams.names().forEach(name -> responseBody.put(name, formParams.get(name)));

          context.response().setStatusCode(200).end(responseBody.encodePrettily());
        } else {
          LOGGER.error("Unable to read file. {}", ar.cause());
          context.response().setStatusCode(500).end();
        }
      });
    } catch (Exception ex) {
      LOGGER.error("Unable to read file. {}", ex);
      context.response().setStatusCode(500).end();
    }
  }

  private String getFilePath(HttpServerRequest event) {
    return catalogue + File.separator + StringUtils.substringAfterLast(event.path(), SEPARATOR);
  }
}
