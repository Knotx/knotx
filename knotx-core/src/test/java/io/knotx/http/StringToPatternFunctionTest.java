/*
 * Copyright (C) 2018 Piotr Andruszkiewicz
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

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class StringToPatternFunctionTest {

  private static StringToPatternFunction stringToPattern;

  @BeforeClass
  public static void setUp() {
    stringToPattern = new StringToPatternFunction();
  }

  @Test
  public void whenTextMatchesStringPattern_thenTextMatchesRegexPattern() {
    assertTrue(matches("foo", "foo"));
    assertTrue(matches("foo", "FOO"));
    assertTrue(matches("foo", "Foo"));
    assertTrue(matches("foo", "fOO"));

    assertTrue(matches("", ""));
    assertTrue(matches("*", "foo"));

    assertTrue(matches("foo*", "foobar"));
    assertTrue(matches("*foo", "barfoo"));
    assertTrue(matches("foo*bar", "foobazbar"));

    assertTrue(matches("f**", "foo"));
    assertTrue(matches("***", "foo"));
    assertTrue(matches("foo*foo*", "foobarfoobar"));
    assertTrue(matches("*foo*foo", "barfoobarfoo"));
  }

  @Test
  public void whenTextDoesNotMatchStringPattern_thenTextDoesNotMatchRegexPattern() {
    assertFalse(matches("foo", "bar"));

    assertFalse(matches("", "bar"));
    assertFalse(matches("*", ""));
    assertFalse(matches("**", "f"));

    assertFalse(matches("foo*", "foo"));
    assertFalse(matches("*foo", "foo"));
    assertFalse(matches("foo*bar", "foobar"));

    assertFalse(matches("foo*bar", "foobar"));
    assertFalse(matches("foo**bar", "foobar"));
    assertFalse(matches("foo**bar", "fooabar"));
  }

  @Test
  public void whenStringPatternContainsRegexConstructs_thenTheyShouldBeEscaped() {
    assertFalse(matches(".","a"));
    assertFalse(matches("[a-z]","a"));
    assertFalse(matches("\\d","1"));
    assertFalse(matches("$", ""));
    assertFalse(matches("^", ""));
    assertFalse(matches("a+", "aaa"));
    assertFalse(matches("a?", "a"));
    assertFalse(matches("a?", ""));
    assertFalse(matches("a{3}", "aaa"));
    assertFalse(matches("a|b", "a"));
    assertFalse(matches("a|b", "b"));
    assertFalse(matches("b(ar|az)", "bar"));
    assertFalse(matches("b(ar|az)", "baz"));

    assertTrue(matches(".","."));
    assertTrue(matches("[a-z]","[a-z]"));
    assertTrue(matches("\\d","\\d"));
    assertTrue(matches("$", "$"));
    assertTrue(matches("^", "^"));
    assertTrue(matches("a+", "a+"));
    assertTrue(matches("a?", "a?"));
    assertTrue(matches("a{3}", "a{3}"));
    assertTrue(matches("a|b", "a|b"));
    assertTrue(matches("b(ar|az)", "b(ar|az)"));
  }

  private boolean matches(String pattern, String text) {
    Pattern regexPattern = stringToPattern.apply(pattern);
    Matcher matcher = regexPattern.matcher(text);
    return matcher.matches();
  }
}
