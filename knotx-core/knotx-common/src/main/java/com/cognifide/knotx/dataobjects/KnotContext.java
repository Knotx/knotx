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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import com.cognifide.knotx.fragments.Fragment;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import rx.Observable;

public class KnotContext extends Codec {

  private String transition;

  private ClientRequest clientRequest;

  private ClientResponse clientResponse;

  private List<Fragment> fragments;

  private volatile Cache<String, Observable<JsonObject>> cache = CacheBuilder.newBuilder().build();

  public KnotContext() {
    //Nothing to set by default
  }

  public KnotContext setClientRequest(ClientRequest request) {
    this.clientRequest = request;
    return this;
  }

  public KnotContext setClientResponse(ClientResponse response) {
    this.clientResponse = response;
    return this;
  }

  public KnotContext setFragments(List<Fragment> fragments) {
    this.fragments = fragments;
    return this;
  }

  public KnotContext clearFragments() {
    this.fragments = null;
    return this;
  }

  public KnotContext setTransition(String transition) {
    this.transition = transition;
    return this;
  }

  public ClientRequest clientRequest() {
    return clientRequest;
  }

  public ClientResponse clientResponse() {
    return clientResponse;
  }

  public Optional<String> transition() {
    return Optional.ofNullable(transition);
  }

  public Optional<List<Fragment>> fragments() {
    return Optional.ofNullable(fragments);
  }

  public Cache<String, Observable<JsonObject>> getCache() {
    return cache;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof KnotContext)) return false;
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

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("transition", transition)
        .add("clientRequest", clientRequest)
        .add("clientResponse", clientResponse)
        .add("fragments", fragments)
        .toString();
  }

  @Override
  public void encodeToWire(Buffer buffer) {
    encodeString(buffer, transition);
    if (clientRequest != null) {
      encodeInt(buffer, 1);
      clientRequest.encodeToWire(buffer);
    } else {
      encodeInt(buffer, 0);
    }
    if (clientResponse != null) {
      encodeInt(buffer, 1);
      clientResponse.encodeToWire(buffer);
    } else {
      encodeInt(buffer, 0);
    }
    if (fragments == null || fragments.isEmpty()) {
      encodeInt(buffer, 0);
    } else {
      encodeInt(buffer, fragments.size());
      for (Fragment item : fragments) {
        encodeString(buffer, item.toJson().encode());
      }
    }
  }

  @Override
  public void decodeFromWire(AtomicInteger position, Buffer buffer) {
    transition = decodeString(position, buffer);
    int exists = decodeInt(position, buffer);
    if (exists == 1) {
      clientRequest = new ClientRequest();
      clientRequest.decodeFromWire(position, buffer);
    }
    exists = decodeInt(position, buffer);
    if (exists == 1) {
      clientResponse = new ClientResponse();
      clientResponse.decodeFromWire(position, buffer);
    }
    int fragmentsAmount = decodeInt(position, buffer);
    if (fragmentsAmount > 0) {
      fragments = Lists.newArrayList();
      for (int fragIdx = fragmentsAmount; fragIdx > 0; fragIdx--) {
        fragments.add(new Fragment(new JsonObject(decodeString(position, buffer))));
      }
    }
  }
}

