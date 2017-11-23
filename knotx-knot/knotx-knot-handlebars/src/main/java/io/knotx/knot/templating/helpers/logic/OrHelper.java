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
package io.knotx.knot.templating.helpers.logic;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.helpers.ConditionalHelper;

import java.util.Arrays;
import java.util.Objects;

/**
 * Checks if any value is not empty.<br>
 * Usage:
 * <pre>
 *     {{#or value1 value2 ... }}
 *         some value not empty
 *     {{else}}
 *         all empty
 *     {{/or}}
 * </pre>
 */
public class OrHelper extends ConditionalHelper {

  @Override
  public String getName() {
    return "or";
  }

  @Override
  protected Boolean checkCondition(Object firstParam, Options options) {
    return !options.isFalsy(firstParam) || Arrays.stream(options.params)
        .filter(Objects::nonNull)
        .anyMatch(param -> !options.isFalsy(param));
  }
}
