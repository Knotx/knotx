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

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;

public class MockActionAdapterHandler extends MockAdapterHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingContext.class);

  public MockActionAdapterHandler(String catalogue, FileSystem fileSystem) {
    super(catalogue, fileSystem);
  }

  @Override
  public void handle(Message<JsonObject> message) {
    ClientRequest request = new ClientRequest(message.body().getJsonObject("clientRequest"));
    JsonObject params = message.body().getJsonObject("params");

    String resourcePath = getFilePath(params.getString("step"));
    fileSystem.readFile(resourcePath, ar -> {
      if (ar.succeeded()) {
        final JsonObject signals = ar.result().toJsonObject();
        message.reply(signalResponse(request, signals));
      } else {
        LOGGER.error("Unable to read file. {}", ar.cause());
        message.reply(getErrorResponse());
      }
    });
  }

  @Override
  protected String getFilePath(String step) {
    return super.getFilePath(step) + ".json";
  }

  private Pair<Optional<String>, JsonObject> getErrorResponse() {
    return Pair.of(Optional.empty(),
        new JsonObject().put("response", errorResponse().toJson()));
  }

  private JsonObject signalResponse(ClientRequest request, JsonObject signals) {
    final Pair<Optional<String>, JsonObject> result = getSignalResult(request, signals);

    final JsonObject resultBody = result.getRight().put("form", request.formAttributes());

    final String data = resultBody.toString();
    final ClientResponse clientResponse = new ClientResponse()
        .setHeaders(headers(request, data))
        .setStatusCode(HttpResponseStatus.OK)
        .setBody(Buffer.buffer(data));

    final JsonObject response = new JsonObject()
        .put("response", clientResponse.toJson());

    final Optional<String> signal = result.getLeft();
    if (signal.isPresent()) {
      response.put("signal", signal.get());
    }
    return response;
  }

  private Pair<Optional<String>, JsonObject> getSignalResult(ClientRequest request, JsonObject signals) {
    return signals.stream()
        .filter(entry -> matchRequest(request, entry)).findFirst()
        .map(this::toSignalPair)
        .orElse(getErrorResponse());
  }

  private Pair<Optional<String>, JsonObject> toSignalPair(Map.Entry<String, Object> entry) {
    return Pair.of(Optional.of(entry.getKey()), ((JsonObject) entry.getValue()).getJsonObject("response"));
  }

  private boolean matchRequest(ClientRequest request, Map.Entry<String, Object> signal) {
    final JsonObject condition = ((JsonObject) signal).getJsonObject("condition");
    final MultiMap params = request.params();
    return condition.stream().allMatch(entry ->
        params.contains(entry.getKey())
            && params.get(entry.getKey()).matches(String.valueOf(entry.getValue()))
    );
  }
}
