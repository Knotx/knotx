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
package com.cognifide.knotx.engine;

import com.cognifide.knotx.FileReader;
import com.cognifide.knotx.engine.parser.HtmlFragment;
import com.cognifide.knotx.engine.parser.HtmlParser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class HtmlParserTest {

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  private String TEST_HTML = "test.html";
  private String TEST_NO_FRAGMENTS_HTML = "test-no-fragments.html";
  private String TEST_ONE_FRAGMENT_HTML = "test-one-fragment.html";

  private String EXPECTED_ONE_FRAGMENT = "<script data-api-type=\"templating\" data-uri-get=\"/service/mock/first.json\" type=\"text/x-handlebars-template\"><h2>{{message}}</h2></script>";


  private List<HtmlFragment> test;
  private List<HtmlFragment> testNoTemplates;
  private List<HtmlFragment> testOneTemplates;

  @Before
  public void setUp() throws Exception {
    test = new HtmlParser(FileReader.readText(TEST_HTML)).getFragments();
    testNoTemplates = new HtmlParser(FileReader.readText(TEST_NO_FRAGMENTS_HTML)).getFragments();
    testOneTemplates = new HtmlParser(FileReader.readText(TEST_ONE_FRAGMENT_HTML)).getFragments();
  }

  @Test
  public void testComposeFragmetns() throws Exception {
    StringBuilder result = new StringBuilder();
    IntStream.rangeClosed(0, 6).forEach(idx -> result.append(test.get(idx).getContent()));

    assertThat(result.toString().trim(), equalTo(FileReader.readText(TEST_HTML).trim()));
  }

  @Test
  public void testNoFragments() throws Exception {
    assertThat(testNoTemplates.size(), equalTo(1));
    assertThat(testNoTemplates.get(0).hasHandlebarsTemplate(), equalTo(false));
    assertThat(testNoTemplates.get(0).getContent(), equalTo(FileReader.readText(TEST_NO_FRAGMENTS_HTML)));
  }

  @Test
  public void testOneFragment() throws Exception {
    assertThat(testOneTemplates.size(), equalTo(3));
    assertThat(testOneTemplates.get(0).hasHandlebarsTemplate(), equalTo(false));
    assertThat(testOneTemplates.get(1).hasHandlebarsTemplate(), equalTo(true));
    assertThat(testOneTemplates.get(1).getContent(), equalTo(EXPECTED_ONE_FRAGMENT));
    assertThat(testOneTemplates.get(2).hasHandlebarsTemplate(), equalTo(false));
  }
}
