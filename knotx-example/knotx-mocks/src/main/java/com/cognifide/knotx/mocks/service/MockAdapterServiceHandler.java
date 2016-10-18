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

import com.cognifide.knotx.dataobjects.HttpRequestWrapper;
import com.cognifide.knotx.dataobjects.HttpResponseWrapper;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;

public class MockAdapterServiceHandler implements Handler<Message<JsonObject>> {
  private static final String SEPARATOR = "/";

  private static final String DEFAULT_MIME = "text/plain";
  private final Logger LOGGER = LoggerFactory.getLogger(RoutingContext.class);
  private final FileSystem fileSystem;
  private boolean withBouncer = false;
  private String catalogue;

  public MockAdapterServiceHandler(String catalogue, FileSystem fileSystem) {
    this.catalogue = catalogue;
    this.fileSystem = fileSystem;
  }

  public MockAdapterServiceHandler withBouncer() {
    this.withBouncer = true;
    return this;
  }

  @Override
  public void handle(Message<JsonObject> message) {
    HttpRequestWrapper request = new HttpRequestWrapper(message.body().getJsonObject("request"));
    JsonObject params = message.body().getJsonObject("params");

    String resourcePath = getFilePath(params.getString("path"));
    fileSystem.readFile(resourcePath, ar -> {
      if (ar.succeeded()) {
        String mockData = ar.result().toString();
        if (withBouncer) {
          message.reply(bouncerResponse(request, mockData).toJson());
        } else {
          message.reply(okResponse(request, mockData).toJson());
        }
      } else {
        LOGGER.error("Unable to read file. {}", ar.cause());
        message.reply(errorResponse().toJson());
      }
    });
  }


  private HttpResponseWrapper bouncerResponse(HttpRequestWrapper request, String mockData) {
    JsonObject responseBody = new JsonObject(mockData);
    MultiMap formParams = request.formAttributes();
    formParams.names().forEach(name -> responseBody.put(name, formParams.get(name)));

    return new HttpResponseWrapper()
        .setStatusCode(HttpResponseStatus.OK)
        .setHeaders(headers(request, mockData))
        .setBody(Buffer.buffer(responseBody.toString()));
  }

  private HttpResponseWrapper okResponse(HttpRequestWrapper request, String data) {
    return new HttpResponseWrapper()
        .setHeaders(headers(request, data))
        .setStatusCode(HttpResponseStatus.OK)
        .setBody(Buffer.buffer(data));
  }

  private HttpResponseWrapper errorResponse() {
    return new HttpResponseWrapper()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);
  }

  private String getContentType(HttpRequestWrapper request) {
    return Optional.ofNullable(MimeMapping.getMimeTypeForFilename(request.path())).orElse(DEFAULT_MIME);
  }

  private String getFilePath(String path) {
    return catalogue + File.separator + StringUtils.substringAfterLast(path, SEPARATOR);
  }

  private MultiMap headers(HttpRequestWrapper request, String data) {
    return MultiMap.caseInsensitiveMultiMap().add(HttpHeaders.CONTENT_TYPE.toString(), getContentType(request))
        .add(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(data.length()));
  }
}
