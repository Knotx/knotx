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


import java.util.regex.Pattern;

public class SnippetPatterns {

  private static final String ANY_SNIPPET_PATTERN =
      "(?is).*<%s\\s+" + FragmentConstants.SNIPPET_IDENTIFIER_NAME + ".*";
  private static final String SNIPPET_PATTERN =
      "<%s\\s+" + FragmentConstants.SNIPPET_IDENTIFIER_NAME
          + "\\s*=\\s*\"([A-Za-z0-9-,]+)\"[^>]*>.+?</%s>";

  private final Pattern anySnippetPattern;
  private final Pattern snippetPattern;

  public SnippetPatterns(String tagName) {
    anySnippetPattern = Pattern
        .compile(String.format(ANY_SNIPPET_PATTERN, tagName), Pattern.DOTALL);
    snippetPattern = Pattern
        .compile(String.format(SNIPPET_PATTERN, tagName, tagName), Pattern.DOTALL);
  }

  public Pattern getAnySnippetPattern() {
    return anySnippetPattern;
  }

  public Pattern getSnippetPattern() {
    return snippetPattern;
  }
}
