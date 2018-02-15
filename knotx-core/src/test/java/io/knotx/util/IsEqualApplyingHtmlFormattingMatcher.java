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

import static java.lang.Character.isWhitespace;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

public class IsEqualApplyingHtmlFormattingMatcher extends TypeSafeMatcher<String> {

  private final String expected;

  /**
   * This matcher checks if two Strings are equal by appling to both of them set of whitespace and
   * newline stripping and later applying to both of them the same HTML formatting (with Jsoup).
   */
  public static IsEqualApplyingHtmlFormattingMatcher equalsToWithHtmlFormatting(String expected) {
    return new IsEqualApplyingHtmlFormattingMatcher(expected);
  }

  private IsEqualApplyingHtmlFormattingMatcher(String expected) {
    this.expected = escapeAndFormat(expected);
  }

  @Override
  protected boolean matchesSafely(String actual) {
    final String actualEscaped = escapeAndFormat(actual);
    return expected.equalsIgnoreCase(actualEscaped);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(
        String.format("the given String should match '%s' without whitespaces", expected));
  }

  private String escapeAndFormat(String expected) {
    return stripSpace(applyHtmlFormatting(expected)).replaceAll("\\n+", "");
  }

  private String applyHtmlFormatting(String html) {
    return Jsoup.parse(html, "UTF-8", Parser.xmlParser()).toString();
  }

  private String stripSpace(String toBeStripped) {
    final StringBuilder result = new StringBuilder();
    boolean lastWasSpace = true;
    for (int i = 0; i < toBeStripped.length(); i++) {
      char c = toBeStripped.charAt(i);
      if (isWhitespace(c)) {
        if (!lastWasSpace) {
          result.append(' ');
        }
        lastWasSpace = true;
      } else {
        result.append(c);
        lastWasSpace = false;
      }
    }
    return result.toString().trim();
  }
}
