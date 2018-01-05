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
package io.knotx.mocks.handler;

import io.knotx.dataobjects.AdapterRequest;
import io.knotx.dataobjects.AdapterResponse;
import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.reactivex.core.MultiMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public class MockActionAdapterHandler extends MockAdapterHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingContext.class);

  public MockActionAdapterHandler(String catalogue, FileSystem fileSystem) {
    super(catalogue, fileSystem);
  }

  @Override
  public void handle(Message<AdapterRequest> message) {
    ClientRequest request = message.body().getRequest();
    JsonObject params = message.body().getParams();

    String resourcePath = getFilePath(params.getString("step"));
    fileSystem.readFile(resourcePath, ar -> {
      if (ar.succeeded()) {
        final JsonObject transitions = ar.result().toJsonObject();
        message.reply(replyTransition(request, transitions));
      } else {
        LOGGER.error("Unable to read file. {}", ar.cause());
        message.reply(errorResponse());
      }
    });
  }

  @Override
  protected String getFilePath(String step) {
    return super.getFilePath(step) + ".json";
  }

  private AdapterResponse replyTransition(ClientRequest request, JsonObject transitions) {
    final Pair<Optional<String>, JsonObject> result = getTransitionResult(request, transitions);

    final JsonObject resultBody = result.getRight()
        .put("form", toJsonObject(request.getFormAttributes()));

    final String data = resultBody.toString();
    final ClientResponse clientResponse = new ClientResponse()
        .setHeaders(headers(request, data))
        .setStatusCode(HttpResponseStatus.OK.code())
        .setBody(Buffer.buffer(data));

    final AdapterResponse response = new AdapterResponse()
        .setResponse(clientResponse);

    final Optional<String> transition = result.getLeft();
    if (transition.isPresent()) {
      response.setSignal(transition.get());
    }
    return response;
  }

  private Pair<Optional<String>, JsonObject> getTransitionResult(ClientRequest request,
      JsonObject transitions) {
    return transitions.stream()
        .filter(entry -> matchRequest(request, entry)).findFirst()
        .map(this::toTransitionPair)
        .orElse(getErrorResponse());
  }

  private Pair<Optional<String>, JsonObject> toTransitionPair(Map.Entry<String, Object> entry) {
    return Pair
        .of(Optional.of(entry.getKey()), ((JsonObject) entry.getValue()).getJsonObject("response"));
  }

  private Pair<Optional<String>, JsonObject> getErrorResponse() {
    return Pair.of(Optional.empty(),
        new JsonObject().put("clientResponse", errorResponse().getResponse().toMetadataJson()));
  }

  private boolean matchRequest(ClientRequest request, Map.Entry<String, Object> transition) {
    final JsonObject condition = ((JsonObject) transition.getValue()).getJsonObject("condition");
    final MultiMap formAttributes = request.getFormAttributes();
    return condition.stream().allMatch(entry ->
        formAttributes.contains(entry.getKey())
            && formAttributes.get(entry.getKey()).matches(String.valueOf(entry.getValue()))
    );
  }

  private JsonObject toJsonObject(MultiMap multiMap) {
    JsonObject result = new JsonObject();
    multiMap.names().stream()
        .forEach(name -> result.put(name, multiMap.get(name)));

    return result;
  }
}
