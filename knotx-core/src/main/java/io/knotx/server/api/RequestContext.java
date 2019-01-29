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
package io.knotx.server.api;

import com.google.common.collect.Lists;
import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.fragment.FragmentProcessingLog;
import io.knotx.fragment.NewFragment;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Objects;

@DataObject(generateConverter = true)
public class RequestContext {

  public static final String KEY = "REQUEST_CONTEXT";

  private List<FragmentProcessingLog> history;

  private ClientRequest clientRequest;

  private ClientResponse clientResponse;

  private List<NewFragment> fragments;

  public RequestContext() {
    //Nothing to set by default
    history = Lists.newArrayList();
    fragments = Lists.newArrayList();
  }

  public RequestContext(JsonObject json) {
    RequestContextConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    RequestContextConverter.toJson(this, json);
    return json;
  }

  public ClientRequest getClientRequest() {
    return clientRequest;
  }

  public RequestContext setClientRequest(ClientRequest clientRequest) {
    this.clientRequest = clientRequest;
    return this;
  }

  public ClientResponse getClientResponse() {
    return clientResponse;
  }

  public RequestContext setClientResponse(ClientResponse clientResponse) {
    this.clientResponse = clientResponse;
    return this;
  }

  public List<NewFragment> getFragments() {
    return fragments;
  }

  public RequestContext setFragments(List<NewFragment> fragments) {
    this.fragments = fragments;
    return this;
  }

  public void addHistoryLog(FragmentProcessingLog historyLog) {
    history.add(historyLog);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestContext that = (RequestContext) o;
    return Objects.equals(clientRequest, that.clientRequest) &&
        Objects.equals(clientResponse, that.clientResponse) &&
        Objects.equals(fragments, that.fragments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientRequest, clientResponse, fragments);
  }

  @Override
  public String toString() {
    return "RequestContext{" +
        "clientRequest=" + clientRequest +
        ", clientResponse=" + clientResponse +
        ", fragments=" + fragments +
        '}';
  }
}
