/*
 * Knot.x - Reactive microservice assembler - API
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
package com.cognifide.knotx.dataobjects;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Optional;

import io.vertx.core.json.JsonObject;
import rx.Observable;

public class KnotContext {

  private Optional<String> transition;

  private ClientRequest clientRequest;

  private ClientResponse clientResponse;

  private String template;

  private volatile Cache<String, Observable<JsonObject>> cache = CacheBuilder.newBuilder().build();

  public KnotContext() {
    //Nothing to set by default
  }

  /**
   * Initialise the fields of this instance from the specified JSON
   *
   * @param json the JSON
   */
  public KnotContext(JsonObject json) {
    this.transition = Optional.ofNullable(json.getString("transition"));
    this.clientRequest = new ClientRequest(json.getJsonObject("clientRequest"));
    this.clientResponse = new ClientResponse(json.getJsonObject("clientResponse"));
    this.template = json.getString("template");
  }

  public KnotContext setClientRequest(ClientRequest request) {
    this.clientRequest = request;
    return this;
  }

  public KnotContext setClientResponse(ClientResponse response) {
    this.clientResponse = response;
    return this;
  }

  public KnotContext setTemplate(String template) {
    this.template = template;
    return this;
  }

  public ClientRequest clientRequest() {
    return clientRequest;
  }

  public ClientResponse clientResponse() {
    return clientResponse;
  }

  public Optional<String> transition() {
    return transition;
  }

  public String template() {
    return template;
  }

  public Cache<String, Observable<JsonObject>> getCache() {
    return cache;
  }

  /**
   * Convert this to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("clientRequest", clientRequest.toJson());
    json.put("clientResponse", clientResponse.toJson());
    transition.ifPresent(value -> json.put("transition", value));
    if (template != null) {
      json.put("template", template);
    }
    return json;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof KnotContext)) return false;
    KnotContext that = (KnotContext) o;
    return Objects.equal(transition, that.transition) &&
        Objects.equal(clientRequest, that.clientRequest) &&
        Objects.equal(clientResponse, that.clientResponse) &&
        Objects.equal(template, that.template);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(transition, clientRequest, clientResponse, template);
  }
}

