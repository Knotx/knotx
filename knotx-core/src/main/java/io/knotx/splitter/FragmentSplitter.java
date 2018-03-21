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
package io.knotx.splitter;

import io.knotx.dataobjects.Fragment;
import java.util.List;

@FunctionalInterface
public interface FragmentSplitter {

  /**
   * Splits a template (markup) into list of fragments (see {@link io.knotx.dataobjects.Fragment}).
   *
   * @param template - a string containing the template content which will be split.
   * @return list of fragments (see {@link io.knotx.dataobjects.Fragment}).
   */
  List<Fragment> split(String template);

}
