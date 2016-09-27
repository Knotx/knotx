/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.knotx.engine.parser;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {

  private static String ANY_TEMPLATE_PATTERN = "(?is).*<script\\s+data-api-type\\s*=\\s*\"templating\".*";
  private static Pattern TEMPLATE_PATTERN =
      Pattern.compile("<script\\s+data-api-type\\s*=\\s*\"templating\".+?</script>", Pattern.DOTALL);
  private boolean RAW_HTML = false;
  private boolean RAW_HANDLEBARS_HTML = true;
  private List<HtmlFragment> fragments;

  public HtmlParser(String html) {
    fragments = Lists.newLinkedList();
    if (html.matches(ANY_TEMPLATE_PATTERN)) {
      Matcher matcher = TEMPLATE_PATTERN.matcher(html);
      int idx = 0;
      while (matcher.find()) {
        MatchResult matchResult = matcher.toMatchResult();
        fragments.add(toRaw(html, idx, matchResult.start()));
        fragments.add(toHandlebars(html, matchResult.start(), matchResult.end()));
        idx = matchResult.end();
      }
      fragments.add(toRaw(html, idx, html.length()));
    } else {
      fragments.add(toRaw(html, 0, html.length()));
    }
  }

  public List<HtmlFragment> getFragments() {
    return fragments;
  }

  private HtmlFragment toRaw(String html, int startIdx, int endIdx) {
    return new RawHtmlFragment(html.substring(startIdx, endIdx), RAW_HTML);
  }

  private HtmlFragment toHandlebars(String html, int startIdx, int endIdx) {
    return new RawHtmlFragment(html.substring(startIdx, endIdx), RAW_HANDLEBARS_HTML);
  }
}
