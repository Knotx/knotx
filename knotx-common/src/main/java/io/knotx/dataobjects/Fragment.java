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
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * An entity representing a markup slice produced during Template fragmentation. It represents both
 * markup with static and dynamic content.
 */
@DataObject(inheritConverter = true)
public class Fragment {

  private static final String RAW_FRAGMENT_ID = "_raw";
  private static final String KNOTS_KEY = "_KNOTS";
  private static final String CONTENT_KEY = "_CONTENT";
  private static final String CONTEXT_KEY = "_CONTEXT";

  private final List<String> knots;
  private final JsonObject context;
  private String content;

  public Fragment(JsonObject fragment) {
    this.knots = fragment.getJsonArray(KNOTS_KEY).stream().map(String::valueOf)
        .collect(Collectors.toList());
    this.content = fragment.getString(CONTENT_KEY);
    this.context = fragment.getJsonObject(CONTEXT_KEY, new JsonObject());
  }

  private Fragment(List<String> knots, String data) {
    if (knots == null || knots.isEmpty() || StringUtils.isEmpty(data)) {
      throw new NoSuchElementException("Fragment is not valid [" + knots + "], [" + data + "].");
    }
    this.knots = knots;
    this.content = data;
    this.context = new JsonObject();
  }

  public static Fragment raw(String data) {
    return new Fragment(Collections.singletonList(RAW_FRAGMENT_ID), data);
  }

  public static Fragment snippet(List<String> knots, String data) {
    return new Fragment(knots, data);
  }

  public JsonObject toJson() {
    return new JsonObject().put(KNOTS_KEY, new JsonArray(knots)).put(CONTENT_KEY, content)
        .put(CONTEXT_KEY, context);
  }

  /**
   * @return list of Knots identifiers that are used during Knot usage decision.
   */
  public List<String> knots() {
    return knots;
  }

  /**
   * @return markup content of this fragment.
   */
  public String content() {
    return content;
  }

  public Fragment content(String content) {
    this.content = content;
    return this;
  }

  /**
   * @return a {@link JsonObject} containing the data produced during Knots chain processing and
   * used to exchange the context between them, e.g. ActionKnot can put there `action` context that
   * contains data accumulated during action processing.
   */
  public JsonObject context() {
    return context;
  }

  /**
   * @return <tt>true</tt> if this Fragment has no dynamic parts (contains only static markup).
   */
  public boolean isRaw() {
    return knots.contains(RAW_FRAGMENT_ID);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Fragment)) {
      return false;
    }
    Fragment that = (Fragment) o;
    return Objects.equal(knots, that.knots) &&
        Objects.equal(content, that.content) &&
        Objects.equal(context, that.context);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(knots, content, context);
  }

  @Override
  public String toString() {
    return toJson().toString();
  }
}
