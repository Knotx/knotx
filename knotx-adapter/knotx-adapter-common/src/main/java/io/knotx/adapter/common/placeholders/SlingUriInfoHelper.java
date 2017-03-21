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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public final class SlingUriInfoHelper {

  /**
   * This regular expression is used to decompose a sling format uri into following parts:
   * <p>
   * <ul>
   * <li>path</li>
   * <li>selector string</li>
   * <li>extension</li>
   * <li>suffix</li>
   * </ul>
   * <p>
   */
  private static final Pattern URI_PATTERN = Pattern.compile(
      "([^\\.\\?#]+)(\\.([^\\/\\?#]+)\\.)?(\\.?([^\\/\\?#]+))?([^\\?#]+)?((\\?|#).+)?");

  private static final Map<String, SlingUriInfo> cache = new HashMap<>();

  private SlingUriInfoHelper() {
    // util
  }

  public static SlingUriInfo getUriInfo(String uri) {
    if (!cache.containsKey(uri)) {
      cache.put(uri, generateSlingUriInfo(uri));
    }
    return cache.get(uri);
  }

  private static SlingUriInfo generateSlingUriInfo(String uri) {
    final Matcher matcher = URI_PATTERN.matcher(uri);
    SlingUriInfo uriInfo = null;
    if (matcher.matches()) {
      String path = matcher.group(1);
      String[] pathParts = StringUtils.length(path) > 1
          ? path.substring(1).split("/")
          : new String[]{path};

      String selectorString = matcher.group(3);
      String[] selectors = selectorString != null
          ? selectorString.split("\\.")
          : new String[0];

      String extension = matcher.group(5);
      String suffix = matcher.group(6);

      uriInfo = new SlingUriInfo(path, pathParts, selectorString, selectors, extension, suffix);
    }
    return uriInfo;
  }

}
