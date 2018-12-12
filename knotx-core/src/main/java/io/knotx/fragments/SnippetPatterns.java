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
          + "%s" + FragmentConstants.SNIPPET_FALLBACK_NAME + "\\s*=\\s*\"([^\"]*)\"[^>]*>.+?</%s>";
  private static final String FALLBACK_PATTERN = "<%s\\s+%s" + FragmentConstants.FALLBACK_ID
      + "\\s*=\\s*\"([A-Za-z0-9-_,]+)\"[^>]*>.+?</%s>";
  private static final String FALLBACK_WITH_STRATEGY_PATTERN = "<%s\\s+%s" + FragmentConstants.FALLBACK_ID
      + "\\s*=\\s*\"([A-Za-z0-9-_,]+)\"[^>]*"
      + "%s" + FragmentConstants.FALLBACK_STRATEGY + "\\s*=\\s*\"([^\"]*)\"[^>]*>.+?</%s>";


  private final Pattern anySnippetPattern;
  private final Pattern snippetPattern;
  private final Pattern snippetWithFallbackPattern;
  private final Pattern fallbackPattern;
  private final Pattern fallbackWithStrategyPattern;

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
    fallbackPattern = Pattern
        .compile(String
            .format(FALLBACK_PATTERN, snippetOptions.getFallbackTagName(), snippetOptions.getParamsPrefix(),
                snippetOptions.getFallbackTagName()), Pattern.DOTALL);
    fallbackWithStrategyPattern = Pattern
        .compile(String
            .format(FALLBACK_WITH_STRATEGY_PATTERN, snippetOptions.getFallbackTagName(), snippetOptions.getParamsPrefix(),
                snippetOptions.getParamsPrefix(), snippetOptions.getFallbackTagName()), Pattern.DOTALL);

  }

  public Pattern getAnySnippetPattern() {
    return anySnippetPattern;
  }

  public Pattern getSnippetPattern() {
    return snippetPattern;
  }

  public Pattern getSnippetWithFallbackPattern() {
    return snippetWithFallbackPattern;
  }

  public Pattern getFallbackPattern() {
    return fallbackPattern;
  }

  public Pattern getFallbackWithStrategyPattern() {
    return fallbackWithStrategyPattern;
  }
}

