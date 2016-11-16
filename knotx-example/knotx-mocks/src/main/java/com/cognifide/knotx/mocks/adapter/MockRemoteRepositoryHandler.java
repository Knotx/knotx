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
package com.cognifide.knotx.mocks.adapter;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;

import io.vertx.core.Handler;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServerRequest;

public class MockRemoteRepositoryHandler implements Handler<HttpServerRequest> {

  private static final String SEPARATOR = "/";
  private static final Logger LOGGER = LoggerFactory.getLogger(MockRemoteRepositoryHandler.class);

  private Set<String> textFileExtensions = Sets.newHashSet("html", "php", "html", "js", "css", "txt", "text", "json", "xml", "xsm", "xsl", "xsd",
      "xslt", "dtd", "yml", "svg", "csv", "log", "sgml", "sgm");

  private String catalogue;

  public MockRemoteRepositoryHandler(String catalogue) {
    this.catalogue = catalogue;
  }

  @Override
  public void handle(HttpServerRequest event) {

    String resourcePath = catalogue + SEPARATOR + getContentPath(event.path());
    final Optional<String> contentType = Optional.ofNullable(MimeMapping.getMimeTypeForFilename(resourcePath));
    final String fileExtension = getFileExtension(resourcePath);
    final boolean isTextFile = fileExtension != null ? textFileExtensions.contains(fileExtension) : false;

    Buffer fileContent = Buffer.buffer();
    try {
      URL resourceUrl = this.getClass().getClassLoader().getResource(resourcePath);
      if (resourceUrl != null) {
        URL url = Resources.getResource(resourcePath);

        if (isTextFile) {
          fileContent = Buffer.buffer(Resources.toString(url, Charsets.UTF_8));
        } else {
          fileContent = Buffer.buffer(Resources.toByteArray(url));
        }
        LOGGER.info("Mocked clientRequest [{}] fetch data from file [{}]", event.path(), resourcePath);
      }
    } catch (IOException e) {
      LOGGER.error("Could not read content!", e);
    } finally {
      event.response().putHeader("Access-Control-Allow-Origin", "*");
      event.response().putHeader("Content-Type", createContentType(contentType.get(), isTextFile));
      event.response().putHeader("Server", "Vert.x");
      event.response().putHeader("Cache-control", "no-cache, no-store, must-revalidate");
      event.response().end(new io.vertx.rxjava.core.buffer.Buffer(fileContent));
      event.connection().close();
    }
  }

  private String createContentType(String detectedContentType, boolean isText) {
    if (isText) {
      return detectedContentType+"; charset=UTF-8";
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

  public static String getFileExtension(String filename) {
    int li = filename.lastIndexOf('.');
    if (li != -1 && li != filename.length() - 1) {
      return filename.substring(li + 1, filename.length());
    }
    return null;
  }

}
