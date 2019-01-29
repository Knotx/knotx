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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;

@DataObject(inheritConverter = true)
public class FragmentProcessingLog {
  private final String name;
  private FragmentProcessingStatus status = FragmentProcessingStatus.UNPROCESSED; // SUCCESS, FAILURE, UNPROCESSED
  private List<FragmentProcessingError> errors = Lists.newArrayList();

  private static final String NAME_KEY = "_NAME";
  private static final String STATUS_KEY = "_STATUS";
  private static final String ERRORS_KEY = "_ERRORS";

  public FragmentProcessingLog(String name) {
    this.name = name;
  }

  public FragmentProcessingLog(JsonObject knot) {
    name = knot.getString(NAME_KEY);
    status = FragmentProcessingStatus.valueOf(knot.getString(STATUS_KEY));
    JsonArray jsonErrors = knot.getJsonArray(ERRORS_KEY);
    errors = Lists.newArrayList();
    for (Object error : jsonErrors) {
      errors.add(new FragmentProcessingError((JsonObject) error));
    }
  }

  public JsonObject toJson() {
    JsonArray errorsArray = new JsonArray();
    errors.stream().map(e -> errorsArray.add(e.toJson()));
    return new JsonObject().put(NAME_KEY, name)
        .put(STATUS_KEY, status.name())
        .put(ERRORS_KEY, errorsArray);
  }

  public String getName() {
    return name;
  }

  public FragmentProcessingStatus getStatus() {
    return status;
  }

  public List<FragmentProcessingError> getErrors() {
    return errors;
  }

  public FragmentProcessingLog error(String code, Object message) {
    errors.add(new FragmentProcessingError(code, message));
    return this;
  }

  public FragmentProcessingLog setStatus(FragmentProcessingStatus status) {
    this.status = status;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FragmentProcessingLog)) {
      return false;
    }
    FragmentProcessingLog that = (FragmentProcessingLog) o;
    return Objects.equal(name, that.name) &&
        Objects.equal(status, that.status) &&
        Objects.equal(errors, that.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, status, errors);
  }
}
