/*
 * Knot.x - Reactive microservice assembler - Repository Verticle
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
package com.cognifide.knotx.repository;

import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.NoSuchFileException;
import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.file.AsyncFile;
import io.vertx.rxjava.core.file.FileSystem;
import rx.Observable;

public class FilesystemRepositoryVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemRepositoryVerticle.class);
  private static final OpenOptions OPEN_OPTIONS = new OpenOptions().setCreate(false).setWrite(false);
  private static final String ERROR_MESSAGE = "Unable to get template from the repository";
  private String catalogue;
  private String address;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.address = config().getString("address");
    this.catalogue = config().getString("catalogue");
  }

  @Override
  public void start() throws Exception {
    LOGGER.info("Registered <{}>", this.getClass().getSimpleName());

    EventBus eventBus = vertx.eventBus();
    eventBus.<JsonObject>consumer(address).handler(
        message -> Observable.just(message)
            .doOnNext(this::traceMessage)
            .flatMap(this::getTemplateContent, Pair::of)
            .subscribe(
                response -> response.getLeft().reply(response.getRight().toJson()),
                error -> {
                  LOGGER.error(ERROR_MESSAGE, error);
                  message.reply(processError(error));
                }
            )
    );

  }

  private Observable<ClientResponse> getTemplateContent(final Message<JsonObject> repoMessage) {
    FileSystem fileSystem = vertx.fileSystem();

    ClientRequest repoRequest = new ClientRequest(repoMessage.body());

    final String localFilePath = catalogue + StringUtils.stripStart(repoRequest.path(), "/");
    final Optional<String> contentType = Optional.ofNullable(MimeMapping.getMimeTypeForFilename(localFilePath));

    LOGGER.trace("Fetching file `{}` from local repository.", localFilePath);

    return fileSystem.openObservable(localFilePath, OPEN_OPTIONS)
        .flatMap(this::processFile)
        .map(buffer -> new ClientResponse().setStatusCode(HttpResponseStatus.OK).setHeaders(headers(contentType)).setBody(buffer))
        .defaultIfEmpty(new ClientResponse().setStatusCode(HttpResponseStatus.NOT_FOUND));
  }

  private MultiMap headers(Optional<String> contentType) {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    if (contentType.isPresent()) {
      headers.add("Content-Type", contentType.get());
    }
    return headers;
  }

  private Observable<Buffer> processFile(final AsyncFile asyncFile) {
    return Observable.just(Buffer.buffer())
        .mergeWith(asyncFile.toObservable())
        .reduce(Buffer::appendBuffer);
  }

  private ClientResponse processError(Throwable error) {
    HttpResponseStatus statusCode;
    if (error.getCause().getClass().equals(NoSuchFileException.class)) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    return new ClientResponse().setStatusCode(statusCode);
  }

  private void traceMessage(Message<JsonObject> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body().encodePrettily());
    }
  }
}
