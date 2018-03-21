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
package io.knotx.dataobjects;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.reactivex.Single;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.List;

@DataObject(generateConverter = true)
public class KnotContext {

  public static final String KEY = "knotContext";

  private String transition;

  private ClientRequest clientRequest;

  private ClientResponse clientResponse;

  private List<Fragment> fragments;

  private volatile Cache<String, Single<JsonObject>> cache = CacheBuilder.newBuilder().build();

  public KnotContext() {
    //Nothing to set by default
  }

  public KnotContext(JsonObject json) {
    KnotContextConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    KnotContextConverter.toJson(this, json);
    return json;
  }

  public KnotContext clearFragments() {
    this.fragments = null;
    return this;
  }

  public String getTransition() {
    return transition;
  }

  public KnotContext setTransition(String transition) {
    this.transition = transition;
    return this;
  }

  public ClientRequest getClientRequest() {
    return clientRequest;
  }

  public KnotContext setClientRequest(ClientRequest request) {
    this.clientRequest = request;
    return this;
  }

  public ClientResponse getClientResponse() {
    return clientResponse;
  }

  public KnotContext setClientResponse(ClientResponse response) {
    this.clientResponse = response;
    return this;
  }

  public List<Fragment> getFragments() {
    return fragments;
  }

  public KnotContext setFragments(List<Fragment> fragments) {
    this.fragments = fragments;
    return this;
  }

  public Cache<String, Single<JsonObject>> getCache() {
    return cache;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof KnotContext)) {
      return false;
    }
    KnotContext that = (KnotContext) o;
    return Objects.equal(transition, that.transition) &&
        Objects.equal(clientRequest, that.clientRequest) &&
        Objects.equal(clientResponse, that.clientResponse) &&
        Objects.equal(fragments, that.fragments);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(transition, clientRequest, clientResponse, fragments);
  }

}

