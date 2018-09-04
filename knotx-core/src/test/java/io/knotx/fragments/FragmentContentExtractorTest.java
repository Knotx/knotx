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

import static io.knotx.junit5.KnotxTestUtils.readText;
import static io.knotx.util.IsEqualApplyingHtmlFormattingMatcher.equalsToWithHtmlFormatting;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import io.knotx.dataobjects.Fragment;
import io.knotx.junit5.KnotxArgumentConverter;
import java.io.IOException;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

public class FragmentContentExtractorTest {

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "io/knotx/fragments/simple_default_snippet.txt|script;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_default_snippet.txt|script;io/knotx/fragments/complex_snippet-expected_content.txt",
      "io/knotx/fragments/simple_custom_snippet.txt|knotx:snippet;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_custom_snippet.txt|knotx:snippet;io/knotx/fragments/complex_snippet-expected_content.txt"
  })
  public void unwrappedContent_withFragment_expectDefinedContent(
      @ConvertWith(KnotxArgumentConverter.class) Fragment fragment,
      String expectedContentFileName) throws Exception {
    final String expectedContent = readText(expectedContentFileName);
    final String unwrappedContent = FragmentContentExtractor.unwrapContent(fragment);

    assertThat(expectedContent, equalToIgnoringWhiteSpace(unwrappedContent));
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "io/knotx/fragments/simple_default_snippet.txt;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_default_snippet.txt;io/knotx/fragments/complex_snippet-expected_content.txt",
      "io/knotx/fragments/simple_custom_snippet.txt;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_custom_snippet.txt;io/knotx/fragments/complex_snippet-expected_content.txt"
  })
  public void unwrappedContent_withString_expectDefinedContent(
      String snippetFileName,
      String expectedContentFileName) throws Exception {
    final String expectedContent = readText(expectedContentFileName);
    final Element element = FragmentContentExtractor.unwrapContent(readText(snippetFileName));

    assertThat(element.toString(), equalsToWithHtmlFormatting(expectedContent));
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "io/knotx/fragments/simple_default_snippet.txt;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_default_snippet.txt;io/knotx/fragments/complex_snippet-expected_content.txt",
      "io/knotx/fragments/simple_custom_snippet.txt;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_custom_snippet.txt;io/knotx/fragments/complex_snippet-expected_content.txt"
  })
  public void unwrapFragmentContent_withFragment_expectDefinedContent(
      @ConvertWith(KnotxArgumentConverter.class) Fragment fragment,
      String expectedContentFileName) throws Exception {

    final String expectedContent = readText(expectedContentFileName);
    final Element element = FragmentContentExtractor.unwrapFragmentContent(fragment);

    assertThat(element.toString(), equalsToWithHtmlFormatting(expectedContent));
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "io/knotx/fragments/empty_snippet.txt",
      "io/knotx/fragments/raw_snippet.txt"
  })
  public void getUnwrappedContent_withRawFragment_expectNotChangedContent(
      @ConvertWith(KnotxArgumentConverter.class) Fragment fragment) throws Exception {
    final String unwrappedContent = FragmentContentExtractor.unwrapContent(fragment);

    assertThat(unwrappedContent, equalToIgnoringWhiteSpace(fragment.content()));
  }

  @Test
  public void getUnwrappedContent_withNullFragment_expectNull() throws Exception {
    assertNull(FragmentContentExtractor.unwrapContent((String) null));
    assertNull(FragmentContentExtractor.unwrapContent((Fragment) null));
  }

  //@Coercion
  public Fragment provideFragment(String fragmentContentFile) throws IOException {
    final String fragmentContent = readText(fragmentContentFile);
    Fragment fragmentMock = Mockito.mock(Fragment.class);
    when(fragmentMock.content()).thenReturn(fragmentContent);
    when(fragmentMock.isRaw())
        .thenReturn(!fragmentContent.contains(FragmentConstants.SNIPPET_IDENTIFIER_NAME));
    return fragmentMock;
  }

}
