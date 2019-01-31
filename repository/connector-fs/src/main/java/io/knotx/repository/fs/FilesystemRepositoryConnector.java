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
import io.reactivex.Single;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.file.FileSystem;
import java.nio.file.NoSuchFileException;
import org.apache.commons.lang3.StringUtils;

class FilesystemRepositoryConnector {

  private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemRepositoryConnector.class);

  private static final String ERROR_MESSAGE = "Unable to get template from the repository";

  private final FilesystemRepositoryOptions config;
  private final FileSystem fileSystem;

  FilesystemRepositoryConnector(FileSystem fileSystem, FilesystemRepositoryOptions configuration) {
    this.fileSystem = fileSystem;
    this.config = configuration;
  }

  Single<FragmentsContext> process(FragmentsContext fragmentsContext) {
    ClientRequest request = fragmentsContext.getClientRequest();
    final String localFilePath =
        config.getCatalogue() + StringUtils.stripStart(request.getPath(), "/");

    LOGGER.debug("Fetching file `{}` from local repository.", localFilePath);

    return fileSystem.rxReadFile(localFilePath)
        .map(buffer -> this.processSuccess(buffer, localFilePath))
        .map(fragmentsContext::setClientResponse)
        .onErrorResumeNext(error -> processError(error, fragmentsContext));
  }

  private Single<FragmentsContext> processError(Throwable error,
      FragmentsContext fragmentsContext) {
    LOGGER.error(ERROR_MESSAGE);
    HttpResponseStatus statusCode;
    if (error.getCause().getClass().equals(NoSuchFileException.class)) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    fragmentsContext.setClientResponse(new ClientResponse().setStatusCode(statusCode.code()));
    return Single.just(fragmentsContext);
  }


  private MultiMap headers(String contentType) {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    if (StringUtils.isNotBlank(contentType)) {
      headers.add("Content-Type", contentType);
    }
    return headers;
  }

  private ClientResponse processSuccess(Buffer buffer, String filePath) {
    return new ClientResponse().setStatusCode(HttpResponseStatus.OK.code())
        .setHeaders(headers(MimeMapping.getMimeTypeForFilename(filePath)))
        .setBody(buffer.getDelegate());
  }

}
