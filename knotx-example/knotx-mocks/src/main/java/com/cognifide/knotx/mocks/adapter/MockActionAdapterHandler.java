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

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;

public class MockActionAdapterHandler extends MockAdapterHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingContext.class);
  private static final String DEFAULT_TRANSITION = "_self";

  public MockActionAdapterHandler(String catalogue, FileSystem fileSystem) {
    super(catalogue, fileSystem);
  }

  @Override
  public void handle(Message<JsonObject> message) {
    ClientRequest request = new ClientRequest(message.body().getJsonObject("clientRequest"));
    JsonObject params = message.body().getJsonObject("params");

    String resourcePath = getFilePath(params.getString("path"));
    fileSystem.readFile(resourcePath, ar -> {
      if (ar.succeeded()) {
        final JsonArray transitions = ar.result().toJsonArray();
        message.reply(transitionResponse(request, transitions));
      } else {
        LOGGER.error("Unable to read file. {}", ar.cause());
        message.reply(getErrorResponse());
      }
    });
  }

  private JsonObject getErrorResponse() {
    return new JsonObject()
        .put("response", errorResponse().toJson());
  }

  private JsonObject transitionResponse(ClientRequest request, JsonArray transitions) {
    final JsonObject result = getTransitionResult(request, transitions);

    final String transition = result.getString("transition");
    final String data = result.getJsonObject("data").toString();
    final ClientResponse clientResponse = new ClientResponse()
        .setHeaders(headers(request, data))
        .setStatusCode(HttpResponseStatus.OK)
        .setBody(Buffer.buffer(data));

    return new JsonObject()
        .put("response", clientResponse.toJson())
        .put("transition", transition);
  }

  private JsonObject getTransitionResult(ClientRequest request, JsonArray transitions) {
    return transitions.stream()
        .map(o -> (JsonObject) o)
        .filter(t -> matchRequest(request, t)).findFirst()
        .orElse(getErrorResponse());
  }

  private boolean matchRequest(ClientRequest request, JsonObject transition) {
    final JsonObject condition = transition.getJsonObject("condition");
    final MultiMap params = request.params();
    return condition.stream().allMatch(entry ->
        params.contains(entry.getKey())
            && params.get(entry.getKey()).equals(entry.getValue())
    );
  }


}
