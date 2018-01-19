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
package io.knotx.knot.action.domain;

@FunctionalInterface
public interface FormTransformer {

  /**
   * Transforms a snippet that contains a HTML form.
   * 1. Removes all Knot.x specific tags from form fragment (form definition).
   * 2. Adds hidden input field with the from identifier.
   *
   * @param content - content of a fragment that should be processed.
   * @param formIdAttrName - unique identifier of a hidden form field.
   * @param formIdAttrValue - value of the form identifier.
   * @return transformed content.
   */
  String transform(String content, String formIdAttrName, String formIdAttrValue);

}
