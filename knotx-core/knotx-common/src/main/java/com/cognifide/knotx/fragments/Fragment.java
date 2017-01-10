/*
 * Knot.x - Reactive microservice assembler - Common
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
package com.cognifide.knotx.fragments;

import com.google.common.base.Objects;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class Fragment {

  private static final String RAW_FRAGMENT_ID = "_raw";

  private static final String KNOTS = "_KNOTS";

  private static final String CONTENT = "_CONTENT";

  private static final String CONTEXT = "_CONTEXT";

  private final List<String> knots;

  private String content;

  private final JsonObject context;

  public Fragment(JsonObject fragment) {
    this.knots = fragment.getJsonArray(KNOTS).stream().map(String::valueOf).collect(Collectors.toList());
    this.content = fragment.getString(CONTENT);
    this.context = fragment.getJsonObject(CONTEXT, new JsonObject());
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
    return new JsonObject().put(KNOTS, new JsonArray(knots)).put(CONTENT, content).put(CONTEXT, context);
  }

  public List<String> knots() {
    return knots;
  }

  public String content() {
    return content;
  }

  public Fragment content(String content) {
    this.content = content;
    return this;
  }

  public JsonObject context() {
    return context;
  }

  public boolean isRaw() {
    return knots.contains(RAW_FRAGMENT_ID);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Fragment)) return false;
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
