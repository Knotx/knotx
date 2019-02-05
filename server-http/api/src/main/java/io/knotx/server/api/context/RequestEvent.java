/*
 * Copyright (C) 2018 Cognifide Limited
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
package io.knotx.server.api.context;

import io.knotx.fragment.Fragment;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.stream.Collectors;

public class RequestEvent {

  private final ClientRequest clientRequest;
  private final List<Fragment> fragments;
  private final JsonObject payload;

  public RequestEvent(ClientRequest clientRequest, List<Fragment> fragments, JsonObject payload) {
    this.clientRequest = clientRequest;
    this.fragments = fragments;
    this.payload = payload;
  }

  public RequestEvent(JsonObject json) {
    this.clientRequest = new ClientRequest(json.getJsonObject("clientRequest"));
    this.fragments = json.getJsonArray("fragments").stream()
        .map(JsonObject.class::cast)
        .map(Fragment::new)
        .collect(Collectors.toList());
    this.payload = json.getJsonObject("payload");
  }

  public ClientRequest getClientRequest() {
    return clientRequest;
  }

  public List<Fragment> getFragments() {
    return fragments;
  }

  public JsonObject getPayload() {
    return payload.copy();
  }

  public JsonObject appendPayload(String key, Object value) {
    this.payload.put(key, value);
    return payload;
  }

  public JsonObject toJson() {
    final JsonArray fragmentsArray = new JsonArray();
    fragments.forEach(entry -> fragmentsArray.add(entry.toJson()));
    return new JsonObject()
        .put("clientRequest", clientRequest.toJson())
        .put("fragments", fragmentsArray)
        .put("payload", payload);
  }
}
