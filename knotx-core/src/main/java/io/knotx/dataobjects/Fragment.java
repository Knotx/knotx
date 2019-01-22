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
import io.knotx.fallback.FragmentFallbackConstants;
import io.knotx.fragments.FragmentConstants;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * An entity representing a markup slice produced during Template fragmentation. It represents both
 * markup with static and dynamic content.
 */
@DataObject(inheritConverter = true)
public class Fragment {

  private static final String RAW_FRAGMENT_ID = "_raw";
  private static final String FALLBACK_FRAGMENT_ID = "_fallback";
  private static final String KNOTS_KEY = "_KNOTS";
  private static final String CONTENT_KEY = "_CONTENT";
  private static final String CONTEXT_KEY = "_CONTEXT";
  private static final String FALLBACK_KEY = "_FALLBACK";
  private static final String ATTRUBUTES_KEY = "_ATTRIBUTES";


  private final List<KnotTask> knots;
  private final JsonObject context;
  private final JsonObject attributes;
  private String content;
  private String fallback;

  public Fragment(JsonObject fragment) {
    JsonArray knotsArray = fragment.getJsonArray(KNOTS_KEY);
    this.knots = knotsArray.stream()
        .map(entry -> new KnotTask((JsonObject) entry))
        .collect(Collectors.toList());
    this.content = fragment.getString(CONTENT_KEY);
    this.context = fragment.getJsonObject(CONTEXT_KEY, new JsonObject());
    this.fallback = fragment.getString(FALLBACK_KEY);
    this.attributes = fragment.getJsonObject(ATTRUBUTES_KEY, new JsonObject());
  }

  private Fragment(List<String> knots, String data, String fallback) {
    if (knots == null || knots.isEmpty() || StringUtils.isEmpty(data)) {
      throw new NoSuchElementException("Fragment is not valid [" + knots + "], [" + data + "].");
    }
    this.knots = knots.stream().map(KnotTask::new).collect(Collectors.toList());
    this.content = data;
    this.context = new JsonObject();
    this.attributes = new JsonObject();
    this.fallback = fallback;
  }

  public static Fragment raw(String data) {
    return new Fragment(Collections.singletonList(RAW_FRAGMENT_ID), data, null);
  }

  public static Fragment snippet(List<String> knots, String data, String fallback) {
    return new Fragment(knots, data, fallback);
  }

  public static Fragment snippet(List<String> knots, String data) {
    return snippet(knots, data, null);
  }

  public static Fragment fallback(String data, String fallbackId) {
    return new Fragment(Collections.singletonList(FALLBACK_FRAGMENT_ID), data, null)
        .setAttribute(FragmentFallbackConstants.FALLBACK_ID, fallbackId);
  }

  public static Fragment fallback(String data, String fallbackId, String strategy) {
    return Fragment.fallback(data, fallbackId).setAttribute(FragmentConstants.FALLBACK_STRATEGY, strategy);
  }

  /**
   * @return replacement markup that should be rendered if this Fragment has failed. Can be empty
   * string. Absent value indicates that no replacement markup is provided.
   */
  public Optional<String> fallback() {
    return Optional.ofNullable(fallback);
  }

  public JsonObject toJson() {
    return new JsonObject().put(KNOTS_KEY, new JsonArray(knots.stream().map(KnotTask::toJson).collect(Collectors.toList())))
        .put(CONTENT_KEY, content)
        .put(CONTEXT_KEY, context)
        .put(FALLBACK_KEY, fallback)
        .put(ATTRUBUTES_KEY, attributes);
  }

  /**
   * @return list of Knots identifiers that are used during Knot usage decision.
   */
  public List<String> knots() {
    return knots.stream().map(KnotTask::getName).collect(Collectors.toList());
  }

  public List<KnotTask> knotRouting() {
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

  public Fragment setAttribute(String key, String value) {
    attributes.put(key, value);
    return this;
  }

  public String getAttribute(String key) {
    return attributes.getString(key);
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
    return knots().contains(RAW_FRAGMENT_ID);
  }

  public boolean isFallback() {
    return knots().contains(FALLBACK_FRAGMENT_ID);
  }

  /**
   * @return true if processing of this Fragment has failed
   */
  public boolean failed() {
    return this.knots.stream().anyMatch(k -> k.getStatus() == KnotTaskStatus.FAILURE);
  }

  public boolean unprocessed() {
    return this.knots.stream().allMatch(k -> k.getStatus() == KnotTaskStatus.UNPROCESSED);
  }

  public Fragment failure(String knot, Throwable t) {
    this.knots.stream()
        .filter(k -> knot.equals(k.getName()))
        .findFirst()
        .get()
        .setStatus(KnotTaskStatus.FAILURE)
        .error(t.getClass().getSimpleName(), t.getMessage());
    return this;
  }

  public Fragment success(String knot) {
    this.knots.stream()
        .filter(k -> knot.equals(k.getName()))
        .findFirst()
        .get()
        .setStatus(KnotTaskStatus.SUCCESS);
    return this;
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
        Objects.equal(context, that.context) &&
        Objects.equal(attributes, that.attributes) &&
        Objects.equal(fallback, that.fallback);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(knots, content, context, attributes, fallback);
  }

  @Override
  public String toString() {
    return toJson().toString();
  }
}
