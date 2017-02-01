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
package io.knotx.splitter.impl;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import io.knotx.dataobjects.Fragment;
import io.knotx.junit.util.FileReader;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

//FIXME: rename tests to meet naming convention
public class HtmlFragmentSplitterTest {

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();
  private String TEST_NO_SNIPPETS_HTML = "test-no-fragments.html";

  private String TEST_ONE_SNIPPET_MIDDLE_HTML = "test-one-fragment-middle.html";
  private String TEST_ONE_SNIPPET_BEGIN_HTML = "test-one-fragment-begin.html";
  private String TEST_ONE_SNIPPET_END_HTML = "test-one-fragment-end.html";
  private String ONLY_ONE_SNIPPET_WHOLE_HTML = "test-one-fragment-whole.html";

  private String TEST_MANY_SNIPPETS_HTML = "test-many-fragments.html";

  private String EXPECTED_ONE_FRAGMENT = "<script data-knotx-knots=\"templating-X\" data-knotx-service=\"first-service\" type=\"text/knotx-snippet\"><h2>{{message}}</h2></script>";

  private List<Fragment> testNoSnippets;
  private List<Fragment> testOneSnippetBegin;
  private List<Fragment> testOneSnippetMiddle;
  private List<Fragment> testOneSnippetEnd;
  private List<Fragment> testOnlyOneSnippetWhole;
  private List<Fragment> testManySnippets;

  @Before
  public void setUp() throws Exception {
    testNoSnippets = new HtmlFragmentSplitter().split(FileReader.readText(TEST_NO_SNIPPETS_HTML));
    testManySnippets = new HtmlFragmentSplitter()
        .split(FileReader.readText(TEST_MANY_SNIPPETS_HTML));
    testOneSnippetBegin = new HtmlFragmentSplitter()
        .split(FileReader.readText(TEST_ONE_SNIPPET_BEGIN_HTML));
    testOneSnippetMiddle = new HtmlFragmentSplitter()
        .split(FileReader.readText(TEST_ONE_SNIPPET_MIDDLE_HTML));
    testOneSnippetEnd = new HtmlFragmentSplitter()
        .split(FileReader.readText(TEST_ONE_SNIPPET_END_HTML));
    testOnlyOneSnippetWhole = new HtmlFragmentSplitter()
        .split(FileReader.readText(ONLY_ONE_SNIPPET_WHOLE_HTML));
  }

  @Test
  public void testJoinFragments() throws Exception {
    StringBuilder result = new StringBuilder();
    IntStream.rangeClosed(0, testManySnippets.size() - 1)
        .forEach(idx -> result.append(testManySnippets.get(idx).content()));

    assertThat(result.toString().trim(),
        equalTo(FileReader.readText(TEST_MANY_SNIPPETS_HTML).trim()));
  }

  @Test
  public void testNoFragments() throws Exception {
    assertThat(testNoSnippets.size(), equalTo(1));
    assertThat(testNoSnippets.get(0).isRaw(), equalTo(true));
    assertThat(testNoSnippets.get(0).content(),
        equalTo(FileReader.readText(TEST_NO_SNIPPETS_HTML)));
  }

  @Test
  public void testOneFragmentBegin() throws Exception {
    assertThat(testOneSnippetBegin.size(), equalTo(2));
    assertThat(testOneSnippetBegin.get(0).isRaw(), equalTo(false));
    assertThat(testOneSnippetBegin.get(0).content(), equalTo(EXPECTED_ONE_FRAGMENT));
    assertThat(testOneSnippetBegin.get(1).isRaw(), equalTo(true));
  }

  @Test
  public void testOneFragmentMiddle() throws Exception {
    assertThat(testOneSnippetMiddle.size(), equalTo(3));
    assertThat(testOneSnippetMiddle.get(0).isRaw(), equalTo(true));
    assertThat(testOneSnippetMiddle.get(1).isRaw(), equalTo(false));
    assertThat(testOneSnippetMiddle.get(1).content(), equalTo(EXPECTED_ONE_FRAGMENT));
    assertThat(testOneSnippetMiddle.get(2).isRaw(), equalTo(true));
  }

  @Test
  public void testOneFragmentEnd() throws Exception {
    assertThat(testOneSnippetEnd.size(), equalTo(2));
    assertThat(testOneSnippetEnd.get(0).isRaw(), equalTo(true));
    assertThat(testOneSnippetEnd.get(1).isRaw(), equalTo(false));
    assertThat(testOneSnippetEnd.get(1).content(), equalTo(EXPECTED_ONE_FRAGMENT));
  }

  @Test
  public void testOneFragmentWholeHtml() throws Exception {
    assertThat(testOnlyOneSnippetWhole.size(), equalTo(1));
    assertThat(testOnlyOneSnippetWhole.get(0).isRaw(), equalTo(false));
    assertThat(testOnlyOneSnippetWhole.get(0).content(), equalTo(EXPECTED_ONE_FRAGMENT));
  }

}
