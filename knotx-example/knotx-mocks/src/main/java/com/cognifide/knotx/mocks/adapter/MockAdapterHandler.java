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

import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;

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

public class MockAdapterHandler implements Handler<Message<JsonObject>> {
  private static final String SEPARATOR = "/";

  private static final String DEFAULT_MIME = "text/plain";
  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingContext.class);

  protected final FileSystem fileSystem;
  protected String catalogue;

  public MockAdapterHandler(String catalogue, FileSystem fileSystem) {
    this.catalogue = catalogue;
    this.fileSystem = fileSystem;
  }
  @Override
  public void handle(Message<JsonObject> message) {
    ClientRequest request = new ClientRequest(message.body().getJsonObject("clientRequest"));
    JsonObject params = message.body().getJsonObject("params");

    String resourcePath = getFilePath(params.getString("path"));
    fileSystem.readFile(resourcePath, ar -> {
      if (ar.succeeded()) {
        String mockData = ar.result().toString();
        message.reply(okResponse(request, mockData).toJson());
      } else {
        LOGGER.error("Unable to read file. {}", ar.cause());
        message.reply(errorResponse().toJson());
      }
    });
  }

  private ClientResponse okResponse(ClientRequest request, String data) {
    return new ClientResponse()
        .setHeaders(headers(request, data))
        .setStatusCode(HttpResponseStatus.OK)
        .setBody(Buffer.buffer(data));
  }

  protected ClientResponse errorResponse() {
    return new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);
  }

  protected String getContentType(ClientRequest request) {
    return Optional.ofNullable(MimeMapping.getMimeTypeForFilename(request.path())).orElse(DEFAULT_MIME);
  }

  protected String getFilePath(String path) {
    return catalogue + File.separator + StringUtils.substringAfterLast(path, SEPARATOR);
  }

  protected MultiMap headers(ClientRequest request, String data) {
    return MultiMap.caseInsensitiveMultiMap().add(HttpHeaders.CONTENT_TYPE.toString(), getContentType(request))
        .add(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(data.length()));
  }
}
