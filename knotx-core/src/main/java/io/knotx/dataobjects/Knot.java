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
import com.google.common.collect.Maps;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.Map;

@DataObject(inheritConverter = true)
public class Knot {
  private String name;
  private KnotStatus status = KnotStatus.UNPROCESSED; // SUCCESS, FAILURE, UNPROCESSED
  private Map<String, Object> errors = Maps.newHashMap(); // key: errorCode, value: errorMessage

  private static final String NAME_KEY = "_NAME";
  private static final String STATUS_KEY ="_STATUS";
  private static final String ERRORS_KEY ="_ERRORS";

  public Knot(String name) {
    this.name = name;
  }

  public Knot(JsonObject knot) {
    name = knot.getString(NAME_KEY);
    status = KnotStatus.valueOf(knot.getString(STATUS_KEY));
    JsonObject jsonErrors = knot.getJsonObject(ERRORS_KEY);
    for (String key : jsonErrors.fieldNames()) {
      errors.put(key, jsonErrors.getString(key));
    }
  }

  public JsonObject toJson() {
    return new JsonObject().put(NAME_KEY, name)
        .put(STATUS_KEY, status.name())
        .put(ERRORS_KEY, new JsonObject(errors));
  }

  public String getName() {
    return name;
  }

  public KnotStatus getStatus() {
    return status;
  }

  public Map<String, Object> getErrors() {
    return errors;
  }

  public Knot addError(String code, Object message) {
    errors.put(code, message);
    return this;
  }

  public Knot setStatus(KnotStatus status) {
    this.status = status;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Knot)) {
      return false;
    }
    Knot that = (Knot) o;
    return Objects.equal(name, that.name) &&
        Objects.equal(status, that.status) &&
        Objects.equal(errors, that.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, status, errors);
  }
}
