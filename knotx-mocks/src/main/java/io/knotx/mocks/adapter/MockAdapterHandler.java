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
package io.knotx.mocks.adapter;

import io.knotx.dataobjects.AdapterRequest;
import io.knotx.dataobjects.AdapterResponse;
import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.reactivex.core.MultiMap;
import java.io.File;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class MockAdapterHandler implements Handler<Message<AdapterRequest>> {

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
  public void handle(Message<AdapterRequest> message) {
    ClientRequest request = message.body().getRequest();
    JsonObject params = message.body().getParams();

    String resourcePath = getFilePath(params.getString("path"));
    fileSystem.readFile(resourcePath, ar -> {
      if (ar.succeeded()) {
        String mockData = ar.result().toString();
        message.reply(okResponse(request, mockData));
      } else {
        LOGGER.error("Unable to read file. {}", ar.cause());
        message.reply(errorResponse());
      }
    });
  }

  private AdapterResponse okResponse(ClientRequest request, String data) {
    return new AdapterResponse().setResponse(new ClientResponse()
        .setHeaders(headers(request, data))
        .setStatusCode(HttpResponseStatus.OK.code())
        .setBody(Buffer.buffer(data)));
  }

  protected AdapterResponse errorResponse() {
    return new AdapterResponse().setResponse(new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()));
  }

  protected String getContentType(ClientRequest request) {
    return Optional.ofNullable(MimeMapping.getMimeTypeForFilename(request.getPath()))
        .orElse(DEFAULT_MIME);
  }

  protected String getFilePath(String path) {
    return catalogue + File.separator + (path.contains(SEPARATOR) ? StringUtils
        .substringAfterLast(path, SEPARATOR) : path);
  }

  protected MultiMap headers(ClientRequest request, String data) {
    return MultiMap.caseInsensitiveMultiMap()
        .add(HttpHeaders.CONTENT_TYPE.toString(), getContentType(request))
        .add(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(data.length()));
  }
}
