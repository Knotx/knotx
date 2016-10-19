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
import java.util.Optional;

import io.vertx.core.Handler;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import rx.functions.Action2;

public class MockServiceHandler implements Handler<RoutingContext> {
  private static final String SEPARATOR = "/";

  private static final String DEFAULT_MIME = "text/plain";
  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingContext.class);
  private final FileSystem fileSystem;
  private Action2<RoutingContext, String> bodyProcessor;
  private String catalogue;

  public MockServiceHandler(String catalogue, FileSystem fileSystem) {
    this.catalogue = catalogue;
    this.fileSystem = fileSystem;
  }

  public MockServiceHandler withBodyProcessor(Action2<RoutingContext, String> bodyProcessor) {
    this.bodyProcessor = bodyProcessor;
    return this;
  }

  @Override
  public void handle(RoutingContext context) {
    String resourcePath = getFilePath(context);
    String contentType = getContentType(context);

    fileSystem.readFile(resourcePath, ar -> {
      if (ar.succeeded()) {
        String mockData = ar.result().toString();
        if (bodyProcessor != null) {
          bodyProcessor.call(context, mockData);
        } else {
          context.response().putHeader("Content-Type", contentType);
          context.response().setStatusCode(200).end(mockData);
        }
      } else {
        LOGGER.error("Unable to read file. {}", ar.cause());
        context.response().setStatusCode(500).end();
      }
    });
  }

  private String getContentType(RoutingContext context) {
    return Optional.ofNullable(MimeMapping.getMimeTypeForFilename(context.request().path())).orElse(DEFAULT_MIME);
  }

  private String getFilePath(RoutingContext context) {
    return catalogue + File.separator + StringUtils.substringAfterLast(context.request().path(), SEPARATOR);
  }

}
