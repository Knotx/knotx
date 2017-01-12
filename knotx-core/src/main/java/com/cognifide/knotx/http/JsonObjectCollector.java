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
package com.cognifide.knotx.http;

import com.google.common.collect.ImmutableSet;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class JsonObjectCollector<T> implements Collector<T, JsonObject, JsonObject> {

  private final Function<T, String> keyGetter;
  private final Function<T, JsonArray> value;

  public JsonObjectCollector(Function<T, String> keyGetter, Function<T, JsonArray> value) {
    this.keyGetter = keyGetter;
    this.value = value;
  }

  public static <T> JsonObjectCollector<T> toJsonObject(Function<T, String> keyGetter, Function<T, JsonArray> valueGetter) {
    return new JsonObjectCollector<>(keyGetter, valueGetter);
  }

  @Override
  public Supplier<JsonObject> supplier() {
    return JsonObject::new;
  }

  @Override
  public BiConsumer<JsonObject, T> accumulator() {
    return (json, t) -> value.apply(t).forEach(value -> json.put(keyGetter.apply(t), value));
  }


  @Override
  public BinaryOperator<JsonObject> combiner() {
    return (map1, map2) -> {
      map1.mergeIn(map2);
      return map1;
    };
  }

  @Override
  public Function<JsonObject, JsonObject> finisher() {
    return map -> map;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return ImmutableSet.of(Characteristics.IDENTITY_FINISH);
  }
}
