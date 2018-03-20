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
package io.knotx.mocks.handler;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import java.io.File;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.apache.commons.lang3.StringUtils;

public class MockServiceHandler implements Handler<RoutingContext> {

  private static final String SEPARATOR = "/";

  private static final String DEFAULT_MIME = "text/plain";
  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingContext.class);
  private final Vertx vertx;
  private final JsonObject delayPerPath;
  private BiConsumer<RoutingContext, String> bodyProcessor;
  private String catalogue;
  private long delayAllMs;

  public MockServiceHandler(String catalogue, Vertx vertx, long delayAllMs,
      JsonObject delayPerPath) {
    this.catalogue = catalogue;
    this.vertx = vertx;
    this.delayAllMs = delayAllMs;
    this.delayPerPath = delayPerPath;
  }

  public MockServiceHandler withBodyProcessor(BiConsumer<RoutingContext, String> bodyProcessor) {
    this.bodyProcessor = bodyProcessor;
    return this;
  }

  @Override
  public void handle(RoutingContext context) {
    String resourcePath = getFilePath(context);
    String contentType = getContentType(context);
    String modifiedResponseStatusCode = getStatusCode(context);

    vertx.fileSystem().readFile(resourcePath, ar -> {
      if (ar.succeeded()) {
        String mockData = ar.result().toString();
        if (bodyProcessor != null) {
          bodyProcessor.accept(context, mockData);
        } else {
          generateResponse(context.request().path(), () -> {
            context.response().putHeader("Content-Type", contentType);
            context.response().setStatusCode(
                StringUtils.isNotBlank(modifiedResponseStatusCode) ? Integer
                    .valueOf(modifiedResponseStatusCode) : 200)
                .end(mockData);
          });
        }
      } else {
        LOGGER.error("Unable to read file. {}", ar.cause());
        context.fail(500);
      }
    });
  }

  private long getDelay(String path) {
    if (delayAllMs > 0) {
      return delayAllMs;
    } else {
      long delay = delayPerPath.getJsonObject(path, new JsonObject())
          .getLong("delayMs", delayAllMs);
      return delay > 0 ? delay : 0L;
    }
  }

  private void generateResponse(String path, Runnable action) {
    long delay = getDelay(path);
    if (delay > 0) {
      LOGGER.info("Delaying response for path {} by {} ms", path, delay);
      vertx.setTimer(delay, timerId -> action.run());
    } else {
      action.run();
    }
  }

  private String getStatusCode(RoutingContext context) {
    MultiMap queryParams = context.request().params();
    return queryParams.get("statusCode");
  }

  private String getContentType(RoutingContext context) {
    return Optional.ofNullable(MimeMapping.getMimeTypeForFilename(context.request().path()))
        .orElse(DEFAULT_MIME);
  }

  private String getFilePath(RoutingContext context) {
    return catalogue + File.separator + StringUtils
        .substringAfterLast(context.request().path(), SEPARATOR);
  }

}
