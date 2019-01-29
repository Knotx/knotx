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
package io.knotx.snippet;

import com.google.common.collect.Lists;
import io.knotx.fragment.Fragment;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Objects;

/**
 * An entity representing a markup slice produced during Template fragmentation. It represents both
 * markup with static and dynamic content.
 */
@DataObject(inheritConverter = true)
public class SnippetFragment {

  // we do not use converters intentionally in this case
  private static final String DELEGATE_KEY = "_DELEGATE";

  private Fragment delegate;

  public SnippetFragment(JsonObject json) {
    this.delegate = new Fragment(json.getJsonObject(DELEGATE_KEY));
  }

  public SnippetFragment(Fragment delegate) {
    this.delegate = delegate;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put(DELEGATE_KEY, delegate.toJson());
    return json;
  }

  /**
   * @return list of Knots identifiers that are used during Knot usage decision.
   */
  public List<String> knots() {
    // TODO lazy init
    return Lists.newArrayList(delegate.getConfiguration().getString("knots").split(","));
  }

  /**
   * @return markup content of this fragment.
   */
  public String content() {
    return delegate.getBody();
  }

  public SnippetFragment content(String content) {
    this.delegate.setBody(content);
    return this;
  }

  /**
   * @return a {@link JsonObject} containing the data produced during Knots chain processing and
   * used to exchange the context between them, e.g. ActionKnot can put there `action` context that
   * contains data accumulated during action processing.
   */
  public JsonObject context() {
    return delegate.getPayload();
  }

  /**
   * @return true if processing of this SnippetFragment has failed
   */
  public boolean failed() {
    return this.delegate.failed();
  }

  public Fragment getDelegate() {
    return delegate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SnippetFragment fragment = (SnippetFragment) o;
    return Objects.equals(delegate, fragment.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(delegate);
  }

  @Override
  public String toString() {
    return toJson().toString();
  }
}
