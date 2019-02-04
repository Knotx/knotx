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
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(inheritConverter = true)
public class HandlerError {

  private static final String CODE_KEY = "_CODE";
  private static final String MESSAGE_KEY = "_MESSAGE";

  private String code;
  private Object message;

  public HandlerError(String code, Object message) {
    this.code = code;
    this.message = message;
  }

  public HandlerError(JsonObject x) {
    this(x.getString(CODE_KEY), x.getString(MESSAGE_KEY));
  }

  public JsonObject toJson() {
    return new JsonObject().put(CODE_KEY, code).put(MESSAGE_KEY, message);
  }

  public String getCode() {
    return code;
  }

  public Object getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof HandlerError)) {
      return false;
    }
    HandlerError that = (HandlerError) o;
    return Objects.equal(code, that.code) &&
        Objects.equal(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(code, message);
  }
}
