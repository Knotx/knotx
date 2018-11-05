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
package io.knotx.splitter;

import io.knotx.dataobjects.Fragment;
import io.knotx.fragments.FragmentConstants;
import io.knotx.fragments.SnippetPatterns;
import io.knotx.options.SnippetOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;

class HtmlFragmentSplitter implements FragmentSplitter {

  private final SnippetPatterns snippetPatterns;

  private final String defaultFallback;

  HtmlFragmentSplitter(SnippetOptions snippetOptions) {
    snippetPatterns = new SnippetPatterns(snippetOptions);
    defaultFallback = snippetOptions.getDefaultFallback();
  }

  @Override
  public List<Fragment> split(String html) {
    if (StringUtils.isEmpty(html)) {
      throw new NoSuchElementException("html cannot be empty");
    }
    List<Fragment> fragments = new ArrayList<>();
    List<FallbackMarker> fallbackMarkers = getFallbackMarkers(html);
    if (snippetPatterns.getAnySnippetPattern().matcher(html).matches()) {
      Matcher matcher = snippetPatterns.getSnippetPattern().matcher(html);
      int idx = 0;
      while (matcher.find()) {
        MatchResult matchResult = matcher.toMatchResult();
        if (idx < matchResult.start()) {
          processMarkup(fragments, fallbackMarkers, html, idx, matchResult.start());
        }
        fragments.add(
            toSnippet(matchResult.group(1).intern()
                    .split(FragmentConstants.FRAGMENT_IDENTIFIERS_SEPARATOR), html,
                matchResult.start(), matchResult.end()));
        idx = matchResult.end();
      }
      if (idx < html.length()) {
        processMarkup(fragments, fallbackMarkers, html, idx, html.length());
      }
    } else {
      processMarkup(fragments, fallbackMarkers, html, 0, html.length());
    }
    return fragments;
  }

  private void processMarkup(List<Fragment> fragments, List<FallbackMarker> fallbackMarkers, String html, int startIdx, int endIdx) {
    int idx = startIdx;
    for (FallbackMarker fe : fallbackMarkers) {
      if (idx > fe.start) {
        continue;
      } else if (fe.start > endIdx) {
        break;
      } else if (idx < fe.start) {
        fragments.add(Fragment.raw(html.substring(idx, fe.start)));
      }
      fragments.add(toFallback(html, fe.start, fe.end, fe.id));
      idx = fe.end;
    }
    if (idx < endIdx) {
      fragments.add(Fragment.raw(html.substring(idx, endIdx)));
    }
  }

  private Fragment toFallback(String html, int startIdx, int endIdx, String id) {
    String snippet = html.substring(startIdx, endIdx);
    return Fragment.fallback(snippet, id);
  }

  private Fragment toSnippet(String[] ids, String html, int startIdx, int endIdx) {
    String snippet = html.substring(startIdx, endIdx);
    Matcher matcher = snippetPatterns.getSnippetWithFallbackPattern().matcher(snippet);
    String fallback = matcher.matches() ? matcher.group(2) : defaultFallback;
    return Fragment.snippet(Arrays.asList(ids), snippet, fallback);
  }

  private List<FallbackMarker> getFallbackMarkers(String html) {
    Matcher matcher = snippetPatterns.getFallbackPattern().matcher(html);
    List<FallbackMarker> result = new ArrayList<>();
    while (matcher.find()) {
      MatchResult matchResult = matcher.toMatchResult();
      result.add(new FallbackMarker(matchResult.start(), matchResult.end(), matcher.group(1)));
    }
    return result;
  }

  private class FallbackMarker {
    int start;
    int end;
    String id;

    FallbackMarker(int startIndex, int endIndex, String id) {
      this.start = startIndex;
      this.end = endIndex;
      this.id = id;
    }
  }

}
