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
package io.knotx.fragment;


import io.vertx.core.json.JsonObject;
import java.util.Objects;

public class Fragment {

  private String fragmentType;

  private JsonObject configuration;

  private String body;

  private Fragment(String fragmentType, JsonObject configuration, String body) {
    this.fragmentType = fragmentType;
    this.configuration = configuration;
    this.body = body;
  }

  public static Fragment raw(String body) {
    return new Fragment("_RAW", new JsonObject(), body);
  }

  public static Fragment of(String fragmentType, JsonObject configuration,String body) {
    return new Fragment(fragmentType, configuration, body);
  }

  public String getFragmentType() {
    return fragmentType;
  }

  public Fragment setFragmentType(String fragmentType) {
    this.fragmentType = fragmentType;
    return this;
  }

  public JsonObject getConfiguration() {
    return configuration;
  }

  public Fragment setConfiguration(JsonObject configuration) {
    this.configuration = configuration;
    return this;
  }

  public String getBody() {
    return body;
  }

  public Fragment setBody(String body) {
    this.body = body;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Fragment fragment = (Fragment) o;
    return Objects.equals(fragmentType, fragment.fragmentType) &&
        Objects.equals(configuration, fragment.configuration) &&
        Objects.equals(body, fragment.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fragmentType, configuration, body);
  }

  @Override
  public String toString() {
    return "Fragment{" +
        "fragmentType='" + fragmentType + '\'' +
        ", configuration=" + configuration +
        ", body='" + body + '\'' +
        '}';
  }

}
