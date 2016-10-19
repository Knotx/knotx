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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class Fragments {

  private List<Fragment> fragments;

  public Fragments(List<Fragment> fragments) {
    this.fragments = Collections.unmodifiableList(fragments);
  }

  public Fragments(JsonArray jsonArray) {
    this.fragments = jsonArray.stream()
        .map(String::valueOf)
        .map(JsonObject::new)
        .map(Fragment::new)
        .collect(Collectors.toList());
  }

  public List<Fragment> getFragments() {
    return fragments;
  }

  public JsonArray toJson() {
    return new JsonArray(fragments.stream()
        .map(Fragment::toJson)
        .collect(Collectors.toList()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Fragments)) return false;
    Fragments that = (Fragments) o;
    return Objects.equal(fragments, that.fragments);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(fragments);
  }

}
