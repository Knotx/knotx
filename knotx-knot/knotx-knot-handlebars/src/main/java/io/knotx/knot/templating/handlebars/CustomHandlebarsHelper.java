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
package io.knotx.knot.templating.handlebars;

import com.github.jknack.handlebars.Helper;

/**
 * It's an extension of {@link Helper} and allows applications to register custom Handlebars helper.
 * <br>
 * Registration of custom helpers is based on ServiceLoader mechanism. Therefore, to make a custom
 * helper available in Handlebars context, implementation of this interface must be registered:
 * <ul>
 * <li>create a configuration file called
 * {@code META-INF/services/io.knotx.api.CustomHandlebarsHelper} in the same project as
 * your implementation class</li>
 * <li>paste a fully qualified name of the implementation class inside the configuration file. If
 * you're providing multiple helpers in a single JAR, you can list them in new lines (one name per
 * line is allowed)
 * <li>make sure the configuration file is part of the JAR file containing the implementation
 * class(es)</li>
 * <li>run Knot.x with the JAR file in the classpath.
 * </ul>
 *
 * @see Helper
 */
public interface CustomHandlebarsHelper<T> extends Helper<T> {

  /**
   * @return name of the helper under which it's going to be registered in Handlebars
   */
  String getName();

}
