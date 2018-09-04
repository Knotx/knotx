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
import io.knotx.options.SnippetOptions;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class HtmlFragmentSplitterContentTest {

  private List<Fragment> defaultSnippetTagFragments;
  private List<Fragment> customSnippetTagFragments;
  private List<Fragment> customSnippetCustomParamsPrefixFragments;

  @BeforeEach
  public void setUp() throws Exception {
    defaultSnippetTagFragments = new HtmlFragmentSplitter(
        new SnippetOptions()
            .setTagName("script")
            .setParamsPrefix("data-knotx-"))
        .split(FileReader.readText("io/knotx/splitter/test-many-fragments.html"));
    customSnippetTagFragments = new HtmlFragmentSplitter(
        new SnippetOptions()
            .setTagName("knotx:snippet")
            .setParamsPrefix("data-knotx-"))
        .split(FileReader.readText("io/knotx/splitter/test-many-fragments-custom-snippet.html"));
    customSnippetCustomParamsPrefixFragments = new HtmlFragmentSplitter(
        new SnippetOptions()
            .setTagName("knotx:snippet")
            .setParamsPrefix(""))
        .split(FileReader
            .readText("io/knotx/splitter/test-many-fragments-custom-snippet-and-prefix.html"));
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "0;io/knotx/splitter/fragments/1.txt",
      "1;io/knotx/splitter/fragments/2-snippet.txt",
      "2;io/knotx/splitter/fragments/3.txt",
      "3;io/knotx/splitter/fragments/4-snippet.txt",
      "4;io/knotx/splitter/fragments/5-snippet.txt",
      "5;io/knotx/splitter/fragments/6.txt",
      "6;io/knotx/splitter/fragments/7-snippet.txt",
      "7;io/knotx/splitter/fragments/8.txt",
      "8;io/knotx/splitter/fragments/9-snippet.txt"
  })
  public void split_whenDefaultSnippetTag_expectNineFragments(int fragmentId, String fragmentFile)
      throws Exception {
    assertThat(defaultSnippetTagFragments.get(fragmentId).content().trim(),
        equalTo(FileReader.readText(fragmentFile).trim()));
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "0;io/knotx/splitter/fragments/1.txt",
      "1;io/knotx/splitter/fragments/2-custom-snippet.txt",
      "2;io/knotx/splitter/fragments/3.txt",
      "3;io/knotx/splitter/fragments/4-custom-snippet.txt",
      "4;io/knotx/splitter/fragments/5-custom-snippet.txt",
      "5;io/knotx/splitter/fragments/6.txt",
      "6;io/knotx/splitter/fragments/7-custom-snippet.txt",
      "7;io/knotx/splitter/fragments/8.txt",
      "8;io/knotx/splitter/fragments/9-custom-snippet.txt"
  })
  public void split_whenCustomSnippetTag_expect9Fragments(int fragmentId, String fragmentFile)
      throws Exception {
    assertThat(customSnippetTagFragments.get(fragmentId).content().trim(),
        equalTo(FileReader.readText(fragmentFile).trim()));
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "0;io/knotx/splitter/fragments/1.txt",
      "1;io/knotx/splitter/fragments/2-custom-snippet-no-prefix.txt",
      "2;io/knotx/splitter/fragments/3.txt",
      "3;io/knotx/splitter/fragments/4-custom-snippet-no-prefix.txt",
      "4;io/knotx/splitter/fragments/5-custom-snippet-no-prefix.txt",
      "5;io/knotx/splitter/fragments/6.txt",
      "6;io/knotx/splitter/fragments/7-custom-snippet-no-prefix.txt",
      "7;io/knotx/splitter/fragments/8.txt",
      "8;io/knotx/splitter/fragments/9-custom-snippet-no-prefix.txt"
  })
  public void split_whenCustomSnippetTagAndNoPrefix_expect9Fragments(int fragmentId,
      String fragmentFile)
      throws Exception {
    assertThat(customSnippetCustomParamsPrefixFragments.get(fragmentId).content().trim(),
        equalTo(FileReader.readText(fragmentFile).trim()));
  }
}
