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
package io.knotx.adapter.common.placeholders;

import io.knotx.dataobjects.ClientRequest;

@FunctionalInterface
public interface PlaceholderSubstitutor {

  /**
   * Get the replacement value from the supplied clientRequest and placeholder name
   *
   * @param request the supplied clientRequest
   * @param placeholder the placeholder name
   * @return the replacement value, or null if no replacement can be get
   */
  String getValue(ClientRequest request, String placeholder);

}
