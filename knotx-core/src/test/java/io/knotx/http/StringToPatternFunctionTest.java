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
  }

  @Test
  public void whenTextDoesNotMatchStringPattern_thenTextDoesNotMatchRegexPattern() {
    assertFalse(matches("foo","bar"));

    assertFalse(matches("","bar"));
    assertFalse(matches("*",""));

    assertFalse(matches("foo*","foo"));
    assertFalse(matches("*foo","foo"));
    assertFalse(matches("foo*bar","foobar"));

    //TODO The class should escape regexp constructs
    //assertFalse(matches(".","a"));
    //assertFalse(matches("[a-z]","a"));
    //assertFalse(matches("\\d","1"));
    //assertFalse(matches("$", ""));
    //assertFalse(matches("^", ""));
    //assertFalse(matches("a+", "aaa"));
    //assertFalse(matches("a?", "a"));
    //assertFalse(matches("a?", ""));
    //assertFalse(matches("a{3}", "aaa"));
    //assertFalse(matches("a|b", "a"));
    //assertFalse(matches("a|b", "b"));
    //assertFalse(matches("b(ar|az)", "bar"));
    //assertFalse(matches("b(ar|az)", "baz"));
  }

  private boolean matches(String pattern, String text) {
    Pattern regexPattern = stringToPattern.apply(pattern);
    Matcher matcher = regexPattern.matcher(text);
    return matcher.matches();
  }
}
