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
package com.cognifide.knotx.mocks.knot;

import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.file.FileSystem;
import rx.Observable;

public class MockKnotHandler implements Handler<Message<KnotContext>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingContext.class);

  private static final KnotContext ERROR_RESPONSE = new KnotContext().setClientResponse(new ClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR));
  private static final KnotContext NOT_FOUND = new KnotContext().setClientResponse(new ClientResponse().setStatusCode(HttpResponseStatus.NOT_FOUND));

  private final FileSystem fileSystem;
  private final JsonObject handlerConfig;

  public MockKnotHandler(JsonObject handlerConfig, FileSystem fileSystem) {
    this.handlerConfig = handlerConfig;
    this.fileSystem = fileSystem;
  }

  @Override
  public void handle(Message<KnotContext> message) {
    final Observable<KnotContext> result = Observable.just(message)
        .map(Message::body)
        .filter(this::findConfiguration)
        .doOnNext(this::logProcessedInfo)
        .flatMap(this::prepareHandlerResponse)
        .defaultIfEmpty(NOT_FOUND);

    result.subscribe(
        message::reply,
        error -> {
          LOGGER.error("Unable to return response!", error.getMessage());
          message.reply(ERROR_RESPONSE);
        }
    );
  }

  private Boolean findConfiguration(KnotContext context) {
    return handlerConfig.containsKey(context.clientRequest().path());
  }

  private void logProcessedInfo(KnotContext context) {
    LOGGER.info("Processing `{}`", context.clientRequest().path());
  }

  private Observable<KnotContext> prepareHandlerResponse(KnotContext context) {
    final JsonObject responseConfig = handlerConfig.getJsonObject(context.clientRequest().path());

    return Observable.from(KnotContextKeys.values())
        .flatMap(key -> key.valueOrDefault(fileSystem, responseConfig, context))
        .filter(value -> value.getRight().isPresent())
        .reduce(new JsonObject(), this::mergeResponseValues)
        .map(val -> new KnotContext().setClientRequest(null));
  }

  private JsonObject mergeResponseValues(JsonObject result, Pair<String, Optional<Object>> value) {
    return new JsonObject().put(value.getLeft(), value.getRight().orElse(StringUtils.EMPTY));
  }

}
