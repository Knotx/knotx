/*
 * Knot.x - Reactive microservice assembler - HTML Fragment Splitter
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
package com.cognifide.knotx.splitter;


import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.junit.FileReader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

//FIXME: rename tests to meet naming convention
public class HtmlFragmentSplitterTest {

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  private String TEST_NO_SNIPPETS_HTML = "test-no-fragments.html";
  private String TEST_ONE_SNIPPET_HTML = "test-one-fragment.html";
  private String TEST_MANY_SNIPPETS_HTML = "test-many-fragments.html";

  private String EXPECTED_ONE_FRAGMENT = "<script data-api-type=\"templating-X\" data-uri-get=\"/service/mock/first.json\" type=\"text/x-handlebars-template\"><h2>{{message}}</h2></script>";


  private List<Fragment> testNoSnippets;
  private List<Fragment> testOneSnippets;
  private List<Fragment> testManySnippets;

  @Before
  public void setUp() throws Exception {
    testNoSnippets = new HtmlFragmentSplitter().split(FileReader.readText(TEST_NO_SNIPPETS_HTML));
    testOneSnippets = new HtmlFragmentSplitter().split(FileReader.readText(TEST_ONE_SNIPPET_HTML));
    testManySnippets = new HtmlFragmentSplitter().split(FileReader.readText(TEST_MANY_SNIPPETS_HTML));
  }

  @Test
  public void testJoinFragments() throws Exception {
    StringBuilder result = new StringBuilder();
    IntStream.rangeClosed(0, testManySnippets.size() - 1).forEach(idx -> result.append(testManySnippets.get(idx).getContent()));

    assertThat(result.toString().trim(), equalTo(FileReader.readText(TEST_MANY_SNIPPETS_HTML).trim()));
  }

  @Test
  public void testNoFragments() throws Exception {
    assertThat(testNoSnippets.size(), equalTo(1));
    assertThat(testNoSnippets.get(0).isRaw(), equalTo(true));
    assertThat(testNoSnippets.get(0).getContent(), equalTo(FileReader.readText(TEST_NO_SNIPPETS_HTML)));
  }

  @Test
  public void testOneFragment() throws Exception {
    assertThat(testOneSnippets.size(), equalTo(3));
    assertThat(testOneSnippets.get(0).isRaw(), equalTo(true));
    assertThat(testOneSnippets.get(1).isRaw(), equalTo(false));
    assertThat(testOneSnippets.get(1).getContent(), equalTo(EXPECTED_ONE_FRAGMENT));
    assertThat(testOneSnippets.get(2).isRaw(), equalTo(true));
  }
}
