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

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.http.HttpServerRequest;

public class MockRemoteRepositoryHandler implements Handler<HttpServerRequest> {

  private static final String SEPARATOR = "/";
  private final Logger LOGGER = LoggerFactory.getLogger(MockRemoteRepositoryHandler.class);
  private String catalogue;

  public MockRemoteRepositoryHandler(String catalogue) {
    this.catalogue = catalogue;
  }

  @Override
  public void handle(HttpServerRequest event) {

    String resourcePath = catalogue + SEPARATOR + getContentPath(event.path());
    String htmlContent = "";
    try {
      URL resourceUrl = this.getClass().getClassLoader().getResource(resourcePath);
      if (resourceUrl != null) {
        URL url = Resources.getResource(resourcePath);
        htmlContent = Resources.toString(url, Charsets.UTF_8);
        LOGGER.info("Mocked clientRequest [{}] fetch data from file [{}]", event.path(), resourcePath);
      }
    } catch (IOException e) {
      LOGGER.error("Could not read content!", e);
    } finally {
      event.response().putHeader("Access-Control-Allow-Origin", "*");
      event.response().putHeader("Content-Type", "text/html; charset=UTF-8");
      event.response().putHeader("Server", "Vert.x");
      event.response().putHeader("Cache-control", "no-cache, no-store, must-revalidate");
      event.response().end(htmlContent);
      event.connection().close();
    }
  }

  private String getContentPath(String path) {
    if (path.startsWith("/")) {
      return path.replaceFirst("/", "");
    } else {
      return path;
    }
  }

}
