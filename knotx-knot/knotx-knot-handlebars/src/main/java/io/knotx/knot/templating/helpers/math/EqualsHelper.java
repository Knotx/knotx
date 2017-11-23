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
package io.knotx.knot.templating.helpers.math;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;

import java.io.IOException;

/**
 * Checks if value1 and value2 are equal.<br>
 * Usage:
 * <pre>
 *     {{#eq value1 value2}}
 *         equal
 *     {{else}}
 *         not equal
 *     {{/eq}}
 * </pre>
 */
public class EqualsHelper implements CustomHandlebarsHelper<Integer> {

  @Override
  public String getName() {
    return "eq";
  }

  @Override
  public Object apply(Integer firstParam, Options options) throws IOException {
    Integer secondParam = options.param(0);
    return firstParam == secondParam ? options.fn() : options.inverse();
  }
}
