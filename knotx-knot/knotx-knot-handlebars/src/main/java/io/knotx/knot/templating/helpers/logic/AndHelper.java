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

/**
 * Checks if values are not empty.<br>
 * Usage:
 * <pre>
 *     {{#and value1 value2 ... }}
 *         all values not empty
 *     {{else}}
 *         some or all values empty
 *     {{/and}}
 * </pre>
 */
public class AndHelper extends ConditionalHelper {

  @Override
  public String getName() {
    return "and";
  }

  @Override
  protected Boolean checkCondition(Object firstParam, Options options) {
    return !options.isFalsy(firstParam) && Arrays.stream(options.params)
        .allMatch(param -> !options.isFalsy(param));
  }

}
