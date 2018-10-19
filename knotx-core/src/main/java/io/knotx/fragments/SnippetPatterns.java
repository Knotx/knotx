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
package io.knotx.fragments;


import io.knotx.options.SnippetOptions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnippetPatterns {

  private static final String ANY_SNIPPET_PATTERN =
      "(?is).*<%s\\s+%s" + FragmentConstants.SNIPPET_IDENTIFIER_NAME + ".*";
  private static final String SNIPPET_PATTERN =
      "<%s\\s+%s" + FragmentConstants.SNIPPET_IDENTIFIER_NAME
          + "\\s*=\\s*\"([A-Za-z0-9-,]+)\"[^>]*>.+?</%s>";
  private static final String SNIPPET_WITH_FALLBACK_PATTERN =
      "<%s\\s+%s" + FragmentConstants.SNIPPET_IDENTIFIER_NAME
          + "\\s*=\\s*\"([A-Za-z0-9-,]+)\"[^>]*"
          +"%s"+ FragmentConstants.SNIPPET_FALLBACK_NAME + "\\s*=\\s*\"([^\"]*)\"[^>]*>.+?</%s>";

  private static final String SNIPPET_PATTERN_2 =
      "<kx-snippet\\s+data-knots\\s*=\\s*\"([A-Za-z0-9-,]+)\"[^>]*data-fallback\\s*=\\s*\"([A-Za-z0-9-,]+)\"[^>]*>.+?</kx-snippet>";


  private final Pattern anySnippetPattern;
  private final Pattern snippetPattern;
  private final Pattern snippetWithFallbackPattern;

  public SnippetPatterns(SnippetOptions snippetOptions) {
    anySnippetPattern = Pattern
        .compile(String.format(ANY_SNIPPET_PATTERN, snippetOptions.getTagName(),
            snippetOptions.getParamsPrefix()), Pattern.DOTALL);
    snippetPattern = Pattern
        .compile(String
            .format(SNIPPET_PATTERN, snippetOptions.getTagName(), snippetOptions.getParamsPrefix(),
                snippetOptions.getTagName()), Pattern.DOTALL);
    snippetWithFallbackPattern = Pattern
        .compile(String
            .format(SNIPPET_WITH_FALLBACK_PATTERN, snippetOptions.getTagName(), snippetOptions.getParamsPrefix(),
                snippetOptions.getParamsPrefix(), snippetOptions.getTagName()), Pattern.DOTALL);
  }

  public Pattern getAnySnippetPattern() {
    return anySnippetPattern;
  }

  public Pattern getSnippetPattern() {
    return snippetPattern;
  }

  public Pattern getSnippetWithFallbackPattern() { return snippetWithFallbackPattern; }

  public static void main(String[] args) {
    Pattern pattern = Pattern.compile(SNIPPET_PATTERN_2);
    String d1 = "<kx-snippet data-knots=\"foo\" data-fallback=\"bar\">baz</kx-snippet>";
    String d2 = "<kx-snippet data-knots=\"foo2\" data-no-fallback=\"bar2\">baz2</kx-snippet>";
    Matcher m1 = pattern.matcher(d1);
    System.out.println(m1.matches());
    System.out.println(m1.group(1));
    System.out.println(m1.group(2));

    Matcher m2 = pattern.matcher(d2);
    System.out.println(m2.matches());
    System.out.println(m2.group(1));
    System.out.println(m2.group(2));

  }

}

