/*
 * Copyright (C) 2018 Cognifide Limited
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
package io.knotx.splitter.html;

import com.google.common.collect.Lists;
import io.knotx.fragment.Fragment;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

class HtmlFragmentSplitter {

  private static final String DYNAMIC_FRAGMENT_REGEXP = "<knotx:(?<type>\\w+)(?<attributes>.*?[^>])>(?<body>.*?)</knotx:\\1>";
  private static final Pattern DYNAMIC_FRAGMENT_PATTERN = Pattern
      .compile(DYNAMIC_FRAGMENT_REGEXP, Pattern.DOTALL);
  private static final String STATIC_FRAGMENT_TYPE = "_STATIC";

  private HtmlAttributesParser attributesParser = new HtmlAttributesParser();

  /**
   * Splits a template (markup) into list of fragments (see {@link Fragment}).
   *
   * @param html - a string containing the template content which will be split.
   * @return list of fragments (see {@link Fragment}).
   */
  List<Fragment> split(String html) {
    List<Fragment> fragments = Lists.newArrayList();
    if (StringUtils.isNotBlank(html)) {
      Matcher matcher = DYNAMIC_FRAGMENT_PATTERN.matcher(html);
      int idx = 0;
      while (matcher.find()) {
        MatchResult matchResult = matcher.toMatchResult();
        if (idx < matchResult.start()) {
          fragments.add(toStatic(html, idx, matchResult.start()));
        }
        fragments.add(
            toDynamic(matcher.group("type"), matcher.group("attributes"), matcher.group("body")));
        idx = matchResult.end();
      }
      if (idx < html.length()) {
        fragments.add(toStatic(html, idx, html.length()));
      }
    }
    return fragments;
  }

  private Fragment toStatic(String html, int startIdx, int endIdx) {
    return new Fragment(STATIC_FRAGMENT_TYPE, new JsonObject(),
        html.substring(startIdx, endIdx));
  }

  private Fragment toDynamic(String type, String attributes, String body) {
    JsonObject configuration = new JsonObject();
    attributesParser.get(attributes).forEach(it -> configuration.put(it.getKey(), it.getValue()));
    return new Fragment(type, configuration, body);
  }
}
