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
package io.knotx.knot.templating.helpers.arrays;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;
import io.vertx.core.json.JsonArray;

import java.io.IOException;


/**
 * Get i-th element of json array.<br>
 * Usage:
 * <pre>
 *     {{element array i}}
 * </pre>
 */
public class ElementHelper implements CustomHandlebarsHelper {
  @Override
  public String getName() {
    return "element";
  }

  @Override
  public Object apply(Object firstParam, Options options) throws IOException {
    int index = options.param(0);
    if (firstParam instanceof JsonArray) {
      return ((JsonArray) firstParam).getValue(index);
    }
    return null;
  }
}
