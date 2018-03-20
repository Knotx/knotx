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

import com.google.common.collect.Sets;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class MockRemoteRepositoryHandler implements Handler<RoutingContext> {

  private static final String SEPARATOR = "/";
  private static final Logger LOGGER = LoggerFactory.getLogger(MockRemoteRepositoryHandler.class);


  private final Vertx vertx;
  private final String catalogue;
  private final JsonObject delayPerPath;
  private long delayAllMs;

  public MockRemoteRepositoryHandler(Vertx vertx, String catalogue, long delayAllMs,
      JsonObject delayPerPath) {
    this.vertx = vertx;
    this.catalogue = catalogue;
    this.delayAllMs = delayAllMs;
    this.delayPerPath = delayPerPath;
  }


  @Override
  public void handle(RoutingContext context) {
    String resourcePath = catalogue + SEPARATOR + getContentPath(context.request().path());
    final Optional<String> contentType = Optional
        .ofNullable(MimeMapping.getMimeTypeForFilename(resourcePath));

    RepositoryFileExtension fileExtension = RepositoryFileExtension.fromFilename(resourcePath);

    vertx.fileSystem().readFile(resourcePath, ar -> {
      HttpServerResponse response = context.response();
      if (ar.succeeded()) {
        LOGGER.info("Mocked clientRequest [{}] fetch data from file [{}]", context.request().path(),
            resourcePath);
        Buffer fileContent = ar.result();
        generateResponse(context.request().path(), () -> {
          setHeaders(response, contentType, fileExtension.isTextFile());
          response.setStatusCode(fileExtension.responseStatus.code()).end(fileContent);
        });
      } else {
        LOGGER.error("Unable to read file.", ar.cause());
        context.fail(404);
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

  private void setHeaders(HttpServerResponse response, Optional<String> contentType,
      boolean isTextFile) {
    response.putHeader("Access-Control-Allow-Origin", "*");
    contentType
        .ifPresent(type -> response.putHeader("Content-Type", createContentType(type, isTextFile)));
    response.putHeader("Server", "Knot.x Repository Mock Server");
    response.putHeader("Cache-control", "no-cache, no-store, must-revalidate");
  }

  private String createContentType(String detectedContentType, boolean isText) {
    if (isText) {
      return detectedContentType + "; charset=UTF-8";
    } else {
      return detectedContentType;
    }
  }

  private String getContentPath(String path) {
    if (path.startsWith("/")) {
      return path.replaceFirst("/", "");
    } else {
      return path;
    }
  }

  private static final class RepositoryFileExtension {

    private static final Set<String> TEXT_FILES_EXTENSIONS =
        Sets.newHashSet("html", "php", "html", "js", "css", "txt", "text", "json", "xml",
            "xsm", "xsl", "xsd", "xslt", "dtd", "yml", "svg", "csv", "log", "sgml", "sgm");

    private final String extensions;
    private final HttpResponseStatus responseStatus;

    private RepositoryFileExtension(String extensions, HttpResponseStatus responseStatus) {
      this.extensions = extensions;
      this.responseStatus = responseStatus;
    }

    static RepositoryFileExtension fromFilename(String filename) {
      HttpResponseStatus responseCode = HttpResponseStatus.OK;
      String ext = StringUtils.substringAfterLast(filename, ".");
      responseCode = extractStatusCode(filename, responseCode);

      return new RepositoryFileExtension(ext, responseCode);
    }

    private static HttpResponseStatus extractStatusCode(String filename, HttpResponseStatus responseCode) {
      final String fileWithoutExt = StringUtils.substringBeforeLast(filename, ".");
      if (StringUtils.isNotBlank(fileWithoutExt)) {
        final String statusCodeStr = StringUtils.substringAfterLast(fileWithoutExt, ".");
        try {
          final int statusCode = Integer.parseInt(statusCodeStr);
          responseCode = HttpResponseStatus.valueOf(statusCode);
        } catch (NumberFormatException e) {
          responseCode = HttpResponseStatus.OK;
        }
      }
      return responseCode;
    }

    Optional<String> getExtensions() {
      return Optional.ofNullable(extensions);
    }

    public HttpResponseStatus getResponseStatus() {
      return responseStatus;
    }

    boolean isTextFile() {
      return getExtensions()
          .filter(TEXT_FILES_EXTENSIONS::contains)
          .isPresent();
    }
  }

}
