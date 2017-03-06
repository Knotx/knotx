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
package io.knotx.repository.impl;

import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.proxy.RepositoryConnectorProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.MultiMap;
import java.nio.file.NoSuchFileException;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class FilesystemRepositoryConnectorProxyImpl implements RepositoryConnectorProxy {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(FilesystemRepositoryConnectorProxyImpl.class);

  private static final String ERROR_MESSAGE = "Unable to get template from the repository";

  private final String catalogue;
  private final FileSystem fileSystem;

  public FilesystemRepositoryConnectorProxyImpl(Vertx vertx, JsonObject configuration) {
    this.fileSystem = vertx.fileSystem();
    this.catalogue = configuration.getString("catalogue");
  }

  @Override
  public void process(ClientRequest request, Handler<AsyncResult<ClientResponse>> result) {
    final String localFilePath = catalogue + StringUtils.stripStart(request.getPath(), "/");
    final Optional<String> contentType = Optional
        .ofNullable(MimeMapping.getMimeTypeForFilename(localFilePath));

    LOGGER.trace("Fetching file `{}` from local repository.", localFilePath);

    ObservableFuture<Buffer> fileObservable = RxHelper.observableFuture();
    fileObservable
        .map(buffer -> new ClientResponse().setStatusCode(HttpResponseStatus.OK.code())
            .setHeaders(headers(contentType)).setBody(buffer))
        .defaultIfEmpty(new ClientResponse().setStatusCode(HttpResponseStatus.NOT_FOUND.code()))
        .subscribe(
            response -> result.handle(Future.succeededFuture(response)),
            error -> {
              LOGGER.error(ERROR_MESSAGE, error);
              result.handle(Future.succeededFuture(processError(error)));
            }
        );

    fileSystem.readFile(localFilePath, fileObservable.toHandler());
  }

  private MultiMap headers(Optional<String> contentType) {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    contentType.ifPresent(s -> headers.add("Content-Type", s));
    return headers;
  }

  private ClientResponse processError(Throwable error) {
    HttpResponseStatus statusCode;
    if (error.getCause().getClass().equals(NoSuchFileException.class)) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    return new ClientResponse().setStatusCode(statusCode.code());
  }
}
