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
