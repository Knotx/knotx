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
package io.knotx.knotengine.api;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.knotx.fragment.Fragment;
import io.knotx.server.api.context.ClientRequest;
import io.knotx.server.api.context.ClientResponse;
import io.knotx.server.api.context.FragmentsContext;
import io.reactivex.Single;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.stream.Collectors;

@DataObject(inheritConverter = true)
public class SnippetFragmentsContext {

  // we do not use converters intentionally in this case
  private static final String DELEGATE_KEY = "_DELEGATE";
  private static final String TRANSITION_KEY = "_TRANSITION";

  private FragmentsContext delegate;

  private String transition;

  private volatile Cache<String, Single<JsonObject>> cache = CacheBuilder.newBuilder().build();

  public SnippetFragmentsContext(FragmentsContext delegate) {
    this.delegate = delegate;
  }

  public SnippetFragmentsContext(JsonObject json) {
    this.delegate = new FragmentsContext(json.getJsonObject(DELEGATE_KEY));
    this.transition = json.getString(TRANSITION_KEY);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put(DELEGATE_KEY, delegate.toJson());
    json.put(TRANSITION_KEY, transition);
    return json;
  }

  public String getTransition() {
    return transition;
  }

  public SnippetFragmentsContext setTransition(String transition) {
    this.transition = transition;
    return this;
  }

  public ClientRequest getClientRequest() {
    return delegate.getClientRequest();
  }

  public SnippetFragmentsContext setClientRequest(ClientRequest request) {
    this.delegate.setClientRequest(request);
    return this;
  }

  public ClientResponse getClientResponse() {
    return delegate.getClientResponse();
  }

  public SnippetFragmentsContext setClientResponse(ClientResponse response) {
    this.delegate.setClientResponse(response);
    return this;
  }

  public List<SnippetFragment> getFragments() {
    return delegate.getFragments().stream().map(SnippetFragment::new)
        .collect(Collectors.toList());
  }

  public SnippetFragmentsContext setFragments(List<SnippetFragment> fragments) {
    List<Fragment> delegates = fragments.stream().map(f -> f.getDelegate())
        .collect(Collectors.toList());
    this.delegate.setFragments(delegates);
    return this;
  }

  public FragmentsContext getDelegate() {
    return delegate;
  }

  public SnippetFragmentsContext setDelegate(FragmentsContext delegate) {
    this.delegate = delegate;
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
    if (!(o instanceof SnippetFragmentsContext)) {
      return false;
    }
    SnippetFragmentsContext that = (SnippetFragmentsContext) o;
    return Objects.equal(transition, that.transition) &&
        Objects.equal(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(transition, delegate);
  }

}

