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


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import io.knotx.dataobjects.Fragment;
import io.knotx.junit.util.FileReader;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HtmlFragmentSplitterTest {

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  private static final String DEFAULT_SCRIPT_TAG = "script";
  private static final String CUSTOM_SCRIPT_TAG = "knotx:snippet";
  private static final String EXPECTED_ONE_FRAGMENT =
      "<script data-knotx-knots=\"templating-X\" data-knotx-service=\"first-service\" type=\"text/knotx-snippet\"><h2>{{message}}</h2></script>";

  @Test
  public void split_whenManyFragments_expectNoChangesInMarkupAfterSplitting() throws Exception {
    StringBuilder result = new StringBuilder();
    List<Fragment> testManySnippets = new HtmlFragmentSplitter(DEFAULT_SCRIPT_TAG)
        .split(FileReader.readText("io/knotx/splitter/test-many-fragments.html"));
    IntStream.rangeClosed(0, testManySnippets.size() - 1)
        .forEach(idx -> result.append(testManySnippets.get(idx).content()));

    assertThat(result.toString().trim(),
        equalTo(FileReader.readText("io/knotx/splitter/test-many-fragments.html").trim()));
  }

  @Test
  public void split_whenNoSnippetsInTemplate_expectOneRawFragment() throws Exception {
    String TEST_NO_SNIPPETS_HTML = "io/knotx/splitter/test-no-fragments.html";
    List<Fragment> testNoSnippets = new HtmlFragmentSplitter(DEFAULT_SCRIPT_TAG)
        .split(FileReader.readText(TEST_NO_SNIPPETS_HTML));
    assertThat(testNoSnippets.size(), equalTo(1));
    assertThat(testNoSnippets.get(0).isRaw(), equalTo(true));
    assertThat(testNoSnippets.get(0).content(),
        equalTo(FileReader.readText(TEST_NO_SNIPPETS_HTML)));
  }

  @Test
  public void split_whenOneSnippetAtTheBeginning_expectOneSnippetOneRawFragment() throws Exception {
    String TEST_ONE_SNIPPET_BEGIN_HTML = "io/knotx/splitter/test-one-fragment-begin.html";
    List<Fragment> testOneSnippetBegin = new HtmlFragmentSplitter(DEFAULT_SCRIPT_TAG)
        .split(FileReader.readText(TEST_ONE_SNIPPET_BEGIN_HTML));
    assertThat(testOneSnippetBegin.size(), equalTo(2));
    assertThat(testOneSnippetBegin.get(0).isRaw(), equalTo(false));
    assertThat(testOneSnippetBegin.get(0).content(), equalTo(EXPECTED_ONE_FRAGMENT));
    assertThat(testOneSnippetBegin.get(1).isRaw(), equalTo(true));
  }

  @Test
  public void split_whenOneSnippetInTheMiddle_expectThreeFragments() throws Exception {
    String TEST_ONE_SNIPPET_MIDDLE_HTML = "io/knotx/splitter/test-one-fragment-middle.html";
    List<Fragment> testOneSnippetMiddle = new HtmlFragmentSplitter(DEFAULT_SCRIPT_TAG)
        .split(FileReader.readText(TEST_ONE_SNIPPET_MIDDLE_HTML));
    assertThat(testOneSnippetMiddle.size(), equalTo(3));
    assertThat(testOneSnippetMiddle.get(0).isRaw(), equalTo(true));
    assertThat(testOneSnippetMiddle.get(1).isRaw(), equalTo(false));
    assertThat(testOneSnippetMiddle.get(1).content(), equalTo(EXPECTED_ONE_FRAGMENT));
    assertThat(testOneSnippetMiddle.get(2).isRaw(), equalTo(true));
  }

  @Test
  public void split_whenOneSnippetAtTheEnd_expectRawAndSnippetFragments() throws Exception {
    String TEST_ONE_SNIPPET_END_HTML = "io/knotx/splitter/test-one-fragment-end.html";
    List<Fragment> testOneSnippetEnd = new HtmlFragmentSplitter(DEFAULT_SCRIPT_TAG)
        .split(FileReader.readText(TEST_ONE_SNIPPET_END_HTML));
    assertThat(testOneSnippetEnd.size(), equalTo(2));
    assertThat(testOneSnippetEnd.get(0).isRaw(), equalTo(true));
    assertThat(testOneSnippetEnd.get(1).isRaw(), equalTo(false));
    assertThat(testOneSnippetEnd.get(1).content(), equalTo(EXPECTED_ONE_FRAGMENT));
  }

  @Test
  public void split_whenOneSnippetIsWholeHtml_expectOneSnippetFragment() throws Exception {
    String ONLY_ONE_SNIPPET_WHOLE_HTML = "io/knotx/splitter/test-one-fragment-whole.html";
    List<Fragment> testOnlyOneSnippetWhole = new HtmlFragmentSplitter(DEFAULT_SCRIPT_TAG)
        .split(FileReader.readText(ONLY_ONE_SNIPPET_WHOLE_HTML));
    assertThat(testOnlyOneSnippetWhole.size(), equalTo(1));
    assertThat(testOnlyOneSnippetWhole.get(0).isRaw(), equalTo(false));
    assertThat(testOnlyOneSnippetWhole.get(0).content(), equalTo(EXPECTED_ONE_FRAGMENT));
  }

  @Test
  public void split_whenCustomSnippetTagOneSnippet_expectThreeFragmentsAndSnippetInTheMiddle()
      throws Exception {
    String TEST_ONE_SNIPPET_MIDDLE_HTML = "io/knotx/splitter/test-custom-snippet-tag.html";
    List<Fragment> testOneSnippetMiddle = new HtmlFragmentSplitter(CUSTOM_SCRIPT_TAG)
        .split(FileReader.readText(TEST_ONE_SNIPPET_MIDDLE_HTML));
    assertThat(testOneSnippetMiddle.size(), equalTo(3));
    assertThat(testOneSnippetMiddle.get(0).isRaw(), equalTo(true));
    assertThat(testOneSnippetMiddle.get(1).isRaw(), equalTo(false));
    assertThat(testOneSnippetMiddle.get(1).content(), equalTo(
        "<knotx:snippet data-knotx-knots=\"templating-X\" data-knotx-service=\"first-service\" type=\"text/knotx-snippet\"><h2>{{message}}</h2></knotx:snippet>"));
    assertThat(testOneSnippetMiddle.get(2).isRaw(), equalTo(true));
  }

  @Test
  public void split_whenCustomSnippetTagManySnippets_expectFiveSnippetsFound()
      throws Exception {
    String TEST_ONE_SNIPPET_MIDDLE_HTML = "io/knotx/splitter/test-many-fragments-custom-snippet.html";
    List<Fragment> testOneSnippetMiddle = new HtmlFragmentSplitter(CUSTOM_SCRIPT_TAG)
        .split(FileReader.readText(TEST_ONE_SNIPPET_MIDDLE_HTML));
    assertThat(testOneSnippetMiddle.size(), equalTo(9));
    assertThat(testOneSnippetMiddle.get(0).isRaw(), equalTo(true));
    assertThat(testOneSnippetMiddle.get(1).isRaw(), equalTo(false));
    assertThat(testOneSnippetMiddle.get(2).isRaw(), equalTo(true));
    assertThat(testOneSnippetMiddle.get(3).isRaw(), equalTo(false));
    assertThat(testOneSnippetMiddle.get(4).isRaw(), equalTo(false));
    assertThat(testOneSnippetMiddle.get(5).isRaw(), equalTo(true));
    assertThat(testOneSnippetMiddle.get(6).isRaw(), equalTo(false));
    assertThat(testOneSnippetMiddle.get(7).isRaw(), equalTo(true));
    assertThat(testOneSnippetMiddle.get(8).isRaw(), equalTo(false));
  }
}
