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
package io.knotx.repository.fs;

import io.knotx.server.api.context.ClientRequest;
import io.knotx.server.api.context.ClientResponse;
import io.knotx.server.api.context.FragmentsContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.file.FileSystem;
import java.nio.file.NoSuchFileException;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

class FilesystemRepositoryConnector {

  private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemRepositoryConnector.class);

  private static final String ERROR_MESSAGE = "Unable to get template from the repository";

  private final FilesystemRepositoryOptions config;
  private final FileSystem fileSystem;

  FilesystemRepositoryConnector(Vertx vertx, FilesystemRepositoryOptions configuration) {
    this.fileSystem = FileSystem.newInstance(vertx.getDelegate().fileSystem());
    this.config = configuration;
  }

  FragmentsContext process(FragmentsContext fragmentsContext) {
//    final String localFilePath =
//        config.getCatalogue() + StringUtils.stripStart(request.getPath(), "/");
//    final Optional<String> contentType = Optional
//        .ofNullable(MimeMapping.getMimeTypeForFilename(localFilePath));
//
//    LOGGER.debug("Fetching file `{}` from local repository.", localFilePath);
//
//    fileSystem.rxReadFile(localFilePath)
//        .map(buffer -> new ClientResponse().setStatusCode(HttpResponseStatus.OK.code())
//            .setHeaders(headers(contentType)).setBody(buffer.getDelegate()))
//        .subscribe(
//            response -> result.handle(Future.succeededFuture(response)),
//            error -> {
//              LOGGER.error(ERROR_MESSAGE, error);
//              result.handle(Future.succeededFuture(processError(error)));
//            }
//        );
    return null;
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
