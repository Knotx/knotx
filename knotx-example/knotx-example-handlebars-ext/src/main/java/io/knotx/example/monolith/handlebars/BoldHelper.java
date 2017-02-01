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
package io.knotx.example.monolith.handlebars;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;
import java.io.IOException;

public class BoldHelper implements CustomHandlebarsHelper<Object> {

  @Override
  public String getName() {
    return "bold";
  }

  @Override
  public CharSequence apply(Object value, Options options) throws IOException {
    Options.Buffer buffer = options.buffer();
    buffer.append("<strong>");
    buffer.append(options.fn());
    buffer.append("</strong>");
    return buffer;
  }
}
