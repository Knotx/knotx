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
package io.knotx.http;

import com.google.common.collect.ImmutableSet;
import io.vertx.reactivex.core.MultiMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class MultiMapCollector<T> implements Collector<T, MultiMap, MultiMap> {

  private final Function<T, String> keyGetter;
  private final Function<T, List<String>> value;

  private MultiMapCollector(Function<T, String> keyGetter, Function<T, List<String>> value) {
    this.keyGetter = keyGetter;
    this.value = value;
  }

  public static <T> MultiMapCollector<T> toMultiMap(Function<T, String> keyGetter,
      Function<T, List<String>> valueGetter) {
    return new MultiMapCollector<>(keyGetter, valueGetter);
  }

  @Override
  public Supplier<MultiMap> supplier() {
    return MultiMap::caseInsensitiveMultiMap;
  }

  @Override
  public BiConsumer<MultiMap, T> accumulator() {
    return (multiMap, t) -> value.apply(t)
        .forEach(v -> multiMap.add(keyGetter.apply(t), v));
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
