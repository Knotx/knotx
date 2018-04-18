/*
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
package io.knotx.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

  /**
   * Returns the string value of the system property with the
   * specified name. The first argument is treated as the name of a
   * system property.  System properties are accessible through the
   * {@link java.lang.System#getProperty(java.lang.String)} method.
   *
   * <p>The second argument is the default value. The default value is
   * returned if there is no property of the specified name or if the
   * specified name is empty or {@code null}.
   *
   * @param propertyName property name.
   * @param defaultVal default value.
   * @return the {@code String} value of the property.
   * @throws SecurityException for the same reasons as {@link System#getProperty(String) System.getProperty}
   * @see System#getProperty(java.lang.String)
   * @see System#getProperty(java.lang.String, java.lang.String)
   */
  public static String getString(String propertyName, String defaultVal) {
    String value = null;
    try {
      value = System.getProperty(propertyName);
    } catch (IllegalArgumentException | NullPointerException e) {
    }
    if (StringUtils.isNotBlank(value)) {
      return value;
    }
    return defaultVal;
  }

}
