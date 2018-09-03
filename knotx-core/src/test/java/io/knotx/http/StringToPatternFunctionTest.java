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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StringToPatternFunctionTest {

  private static StringToPatternFunction stringToPattern;

  @BeforeAll
  public static void setUp() {
    stringToPattern = StringToPatternFunction.getInstance();
  }

  @Test
  public void whenPatternHasNoConstructs_thenTextIsMatchedExactly() {
    assertTrue(matches("foo", "foo"));

    assertFalse(matches("foo", "bar"));
  }

  @Test
  public void whenPatternIsMixedCased_thenTextIsMatchedCaseInsensitive() {
    assertTrue(matches("foo", "FOO"));
    assertTrue(matches("foo", "Foo"));
    assertTrue(matches("foo", "fOO"));
    assertTrue(matches("FOO", "foo"));
    assertTrue(matches("Foo", "foo"));
    assertTrue(matches("fOO", "foo"));
  }

  @Test
  public void whenPatternIsEmptyString_thenOnlyEmptyTextMatches() {
    assertTrue(matches("", ""));

    assertFalse(matches("", "bar"));
  }

  @Test
  public void whenPatternContainsOnlyNWildcardSymbols_thenTextMustBeAtLeastNCharactersLong() {
    assertTrue(matches("*", "foo"));
    assertTrue(matches("***", "foo"));

    assertFalse(matches("*", ""));
    assertFalse(matches("**", "f"));
  }

  @Test
  public void whenPatternContainsWildcardSymbols_thenTextIsMatchedAccordingToWildcardDefinition() {
    assertTrue(matches("foo*", "foobar"));
    assertTrue(matches("*foo", "barfoo"));
    assertTrue(matches("foo*bar", "foobazbar"));

    assertTrue(matches("f**", "foo"));
    assertTrue(matches("foo*foo*", "foobarfoobar"));
    assertTrue(matches("*foo*foo", "barfoobarfoo"));

    assertFalse(matches("foo*", "foo"));
    assertFalse(matches("*foo", "foo"));
    assertFalse(matches("foo*bar", "foobar"));

    assertFalse(matches("foo*bar", "foobar"));
    assertFalse(matches("foo**bar", "foobar"));
    assertFalse(matches("foo**bar", "fooabar"));
  }

  @Test
  public void whenPatternContainsRegexConstructs_thenTheyShouldBeMatched() {
    assertTrue(matches(".", "a"));
    assertTrue(matches("[a-z]", "a"));
    assertTrue(matches("\\d", "1"));
    assertTrue(matches("$", ""));
    assertTrue(matches("^", ""));
    assertTrue(matches("a+", "aaa"));
    assertTrue(matches("a?", "a"));
    assertTrue(matches("a?", ""));
    assertTrue(matches("a{3}", "aaa"));
    assertTrue(matches("a|b", "a"));
    assertTrue(matches("a|b", "b"));
    assertTrue(matches("b(ar|az)", "bar"));
    assertTrue(matches("b(ar|az)", "baz"));
  }

  @Test
  public void whenPatternContainsRegexConstructs_thenTheyShouldNotBeEscaped() {
    assertFalse(matches("[a-z]", "[a-z]"));
    assertFalse(matches("\\d", "\\d"));
    assertFalse(matches("$", "$"));
    assertFalse(matches("^", "^"));
    assertFalse(matches("a+", "a+"));
    assertFalse(matches("a?", "a?"));
    assertFalse(matches("a{3}", "a{3}"));
    assertFalse(matches("a|b", "a|b"));
    assertFalse(matches("b(ar|az)", "b(ar|az)"));
  }

  private boolean matches(String pattern, String text) {
    Pattern regexPattern = stringToPattern.apply(pattern);
    Matcher matcher = regexPattern.matcher(text);
    return matcher.matches();
  }
}
