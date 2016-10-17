/*
 * Knot.x - Reactive microservice assembler - http service adapter
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
package com.cognifide.knotx.core.serviceadapter.http;

import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import io.vertx.rxjava.core.MultiMap;

public class MultiMapCollector<T> implements Collector<T, MultiMap, MultiMap> {

  private final Function<T, String> keyGetter;
  private final Function<T, String> value;

  public MultiMapCollector(Function<T, String> keyGetter, Function<T, String> value) {
    this.keyGetter = keyGetter;
    this.value = value;
  }

  public static <T> MultiMapCollector<T> toMultimap(Function<T, String> keyGetter, Function<T, String> valueGetter) {
    return new MultiMapCollector<>(keyGetter, valueGetter);
  }

  @Override
  public Supplier<MultiMap> supplier() {
    return MultiMap::caseInsensitiveMultiMap;
  }

  @Override
  public BiConsumer<MultiMap, T> accumulator() {
    return (multiMap, t) -> multiMap.add(keyGetter.apply(t), value.apply(t));
  }

  @Override
  public BinaryOperator<MultiMap> combiner() {
    return (map1, map2) -> {
      map1.addAll(map2);
      return map1;
    };
  }

  @Override
  public Function<MultiMap, MultiMap> finisher() {
    return map -> map;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return ImmutableSet.of(Characteristics.IDENTITY_FINISH);
  }
}
