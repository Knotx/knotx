/*
 * Knot.x - Reactive microservice assembler - HTTP Server
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
package com.cognifide.knotx.util;

import java.util.Optional;
import java.util.function.Consumer;
import rx.functions.Action0;

public class OptionalAction<T> {

  private Optional<T> optional;

  private OptionalAction(Optional<T> optional) {
    this.optional = optional;
  }

  public static <T> OptionalAction<T> of(Optional<T> optional) {
    return new OptionalAction<>(optional);
  }

  public OptionalAction<T> ifPresent(Consumer<T> c) {
    optional.ifPresent(c);
    return this;
  }

  public OptionalAction<T> ifNotPresent(Action0 action) {
    if (!optional.isPresent()) {
      action.call();
    }
    return this;
  }
}
