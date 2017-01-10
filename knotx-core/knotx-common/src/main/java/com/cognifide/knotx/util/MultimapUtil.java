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
package com.cognifide.knotx.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.vertx.rxjava.core.MultiMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MultimapUtil {

  public static Map<String, List<String>> toMap(MultiMap multiMap, boolean caseSensitive) {
    Map<String, List<String>> map = Maps.newHashMap();

    ((io.vertx.core.MultiMap) multiMap.getDelegate()).forEach(
        entry -> {
          String key = caseSensitive ? entry.getKey() : entry.getKey().toLowerCase();
          List<String> values;
          if (map.containsKey(key)) {
            values = map.get(key);
          } else {
            values = Lists.newArrayList();
            map.put(key, values);
          }
          values.add(entry.getValue());
        }
    );

    return Collections.unmodifiableMap(map);
  }
}
