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
package io.knotx.http;

import io.knotx.exceptions.ConfigurationException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringToPatternFunction implements Function<String, Pattern> {

  private static final Logger LOGGER = LoggerFactory.getLogger(StringToPatternFunction.class);

  private static final StringToPatternFunction INSTANCE = new StringToPatternFunction();

  private static final String WILDCARD = "*";
  private static final String WILDCARD_REPLACEMENT = "(.+)";

  private StringToPatternFunction() {
  }

  public static StringToPatternFunction getInstance() {
    return INSTANCE;
  }

  @Override
  public Pattern apply(String stringPattern) {
    try {
      return makePattern(stringPattern);
    } catch (PatternSyntaxException e) {
      LOGGER.error("Invalid configuration syntax: {}", stringPattern, e);
      throw new ConfigurationException("Application error - invalid configuration");
    }
  }

  private Pattern makePattern(String stringPattern) {
    String regex = toRegex(stringPattern);
    return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
  }

  private String toRegex(String stringPattern) {
    return "^" + stringPattern.replace(WILDCARD, WILDCARD_REPLACEMENT) + "$";
  }
}
