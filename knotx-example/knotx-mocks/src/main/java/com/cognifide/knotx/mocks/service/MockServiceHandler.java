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

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpServerRequest;

public class MockServiceHandler implements Handler<HttpServerRequest> {

  private static final String SEPARATOR = "/";
  private final Logger LOGGER = LoggerFactory.getLogger(MockServiceHandler.class);
  private String catalogue;

  public MockServiceHandler(String catalogue) {
    this.catalogue = catalogue;
  }

  @Override
  public void handle(HttpServerRequest request) {
    URL mockDataFileUrl = this.getClass().getClassLoader().getResource(getFilePath(request));
    JsonObject responseBody = getMockContent(request);

    if (mockDataFileUrl != null) {
      request.setExpectMultipart(true);
      request.endHandler(v -> {
        if (request.method() == HttpMethod.POST) {
          MultiMap formParams = request.formAttributes();

          formParams.names().forEach(name -> responseBody.put(name, formParams.get(name)));
        }
        request.response().end(responseBody.encodePrettily());
        request.connection().close();
      });
    } else {
      request.response().setStatusCode(500);
      request.connection().close();
    }
  }

  private JsonObject getMockContent(HttpServerRequest event) {
    String resourcePath = getFilePath(event);
    URL resourceUrl = this.getClass().getClassLoader().getResource(resourcePath);
    JsonObject content = new JsonObject();

    if (resourceUrl != null) {
      URL url = Resources.getResource(resourcePath);

      try {
        content = new JsonObject(Resources.toString(url, Charsets.UTF_8));
      } catch (IOException e) {
        LOGGER.error("Could not read content!", e);
      }
      LOGGER.info("Mocked request [{}] fetch data from file [{}]", event.path(), resourcePath);
    }

    return content;
  }

  private String getFilePath(HttpServerRequest event) {
    return catalogue + SEPARATOR + StringUtils.substringAfterLast(event.path(), SEPARATOR);
  }

}
