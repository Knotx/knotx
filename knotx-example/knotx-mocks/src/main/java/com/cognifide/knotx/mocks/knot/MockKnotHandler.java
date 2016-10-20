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

import com.cognifide.knotx.dataobjects.HttpRequestWrapper;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.stream.Collectors;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.file.FileSystem;
import rx.Observable;

public class MockKnotHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingContext.class);

  private static final JsonObject ERROR_RESPONSE = new JsonObject().put(KnotContextKeys.RESPONSE.key(), new JsonObject().put("statusCode", 500));
  private static final JsonObject NOT_FOUND = new JsonObject().put(KnotContextKeys.RESPONSE.key(), new JsonObject().put("statusCode", 404));

  private final FileSystem fileSystem;
  private final Map<String, JsonObject> handledMocks;

  public MockKnotHandler(JsonArray handledMocks, FileSystem fileSystem) {
    this.handledMocks = handledMocks.stream()
        .map(object -> (JsonObject) object)
        .collect(Collectors.toMap(value -> value.getString("request.path"), value -> value));
    this.fileSystem = fileSystem;
  }

  @Override
  public void handle(Message<JsonObject> message) {
    final Observable<JsonObject> result = Observable.just(message)
        .map(msg -> message.body())
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

  private Boolean findConfiguration(JsonObject message) {
    final HttpRequestWrapper request = new HttpRequestWrapper(message.getJsonObject(KnotContextKeys.REQUEST.key()));
    return handledMocks.containsKey(request.path());
  }

  private void logProcessedInfo(JsonObject message) {
    final HttpRequestWrapper request = new HttpRequestWrapper(message.getJsonObject(KnotContextKeys.REQUEST.key()));
    LOGGER.info("Processing `{}`", request.path());
  }

  private Observable<JsonObject> prepareHandlerResponse(JsonObject message) {
    final JsonObject handlerResponse = new JsonObject();
    final HttpRequestWrapper request = new HttpRequestWrapper(message.getJsonObject(KnotContextKeys.REQUEST.key()));
    final JsonObject responseConfig = handledMocks.get(request.path());

    return Observable.from(KnotContextKeys.values())
        .map(key -> key.valueOrDefault(fileSystem, responseConfig))
        .filter(value -> value.getRight() != null)
        .reduce(handlerResponse, this::mergeResponseValues);
  }

  private JsonObject mergeResponseValues(JsonObject result, Pair<String, Object> value) {
    return new JsonObject().put(value.getLeft(), value.getRight());
  }

}
