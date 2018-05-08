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
package io.knotx.junit.coercers;

import static org.mockito.Mockito.when;

import io.knotx.dataobjects.Fragment;
import io.knotx.fragments.SnippetPatterns;
import io.knotx.junit.util.FileReader;
import io.knotx.options.SnippetOptions;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;

public class KnotxCoercers {

  private static final String DEFAULT_SNIPPET_TAG_NAME = "script";
  private static final String DEFAULT_SNIPPET_PARAM_PREFIX = "data-knotx-";
  private static final String PARAMETER_SEPARATOR = "\\|";

  // @formatter:off
  /**
   * Builds mock of {@link io.knotx.dataobjects.Fragment} basing on the provided fragmentParameters.
   * The only mandatory value of the fragmentParameters param is fragment content file, that will be
   * the fragment content. There 2 additional params you may pass:
   * <ul>
   *   <li>custom snippet tag name (if not provided, default value is <bb>script</bb>)</li>
   *   <li>custom snippet parameters prefix (if not provided, default value is <bb>data-knotx-</bb>)</li>
   * </ul>
   * To do so, embed them in the fragmentParameters separated by the <bb>|</bb> character, e.g.
   * "content.txt;knotx:snippet;data-knotx-"
   * will produce Fragment mock with content read from content.txt, snippet tag name set to
   * "knotx:snippet" and snippet parameters prefix set to "data-knotx-". If the last character of
   * fragmentParameters is separator <bb>|</bb>, then snippet parameters prefix will be set to empty
   * string (no prefix).
   */
  // @formatter:on
  public Fragment mockFragment(String fragmentParameters)
      throws IOException {
    final String[] params = fragmentParameters.split(PARAMETER_SEPARATOR);
    final String fragmentContentFile = params[0];
    final String snippetTagName= extractSnippetTagName(params);
    final String fragmentContent = FileReader.readText(fragmentContentFile);
    final String snippetParamPrefix = extractSnippetParamPrefix(params, fragmentParameters);
    final SnippetPatterns patterns = new SnippetPatterns(buildOptions(snippetTagName, snippetParamPrefix));

    Fragment fragmentMock = Mockito.mock(Fragment.class);
    when(fragmentMock.content()).thenReturn(fragmentContent);
    when(fragmentMock.isRaw())
        .thenReturn(!patterns.getAnySnippetPattern().matcher(fragmentContent).matches());
    return fragmentMock;
  }

  private SnippetOptions buildOptions(String snippetTagName, String snippetParamPrefix) {
    return new SnippetOptions()
        .setTagName(snippetTagName)
        .setParamsPrefix(snippetParamPrefix);
  }

  private String extractSnippetTagName(String[] params) {
    String snippetTagName;
    if (params.length > 1) {
      snippetTagName = params[1];
    } else {
      snippetTagName = DEFAULT_SNIPPET_TAG_NAME;
    }
    return snippetTagName;
  }

  private String extractSnippetParamPrefix(String[] params, String fragmentParameters) {
    String prefix;
    if (params.length > 2) {
      prefix = params[2];
    } else if (StringUtils.endsWith(fragmentParameters, "|")) {
      prefix = "";
    } else {
      prefix = DEFAULT_SNIPPET_PARAM_PREFIX;
    }
    return prefix;
  }

  public JsonObject provideJsonObjectFromString(String input) {
    return new JsonObject(input);
  }

}
