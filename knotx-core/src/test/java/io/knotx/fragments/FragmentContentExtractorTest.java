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

import static io.knotx.util.IsEqualApplyingHtmlFormattingMatcher.equalsToWithHtmlFormatting;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import com.googlecode.zohhak.api.Coercion;
import com.googlecode.zohhak.api.Configure;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import io.knotx.dataobjects.Fragment;
import java.io.IOException;
import java.io.InputStreamReader;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(ZohhakRunner.class)
@Configure(separator = ";")
public class FragmentContentExtractorTest {

  @TestWith(value = {
      "io/knotx/fragments/simple_default_snippet.txt;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_default_snippet.txt;io/knotx/fragments/complex_snippet-expected_content.txt",
      "io/knotx/fragments/simple_custom_snippet.txt;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_custom_snippet.txt;io/knotx/fragments/complex_snippet-expected_content.txt"
  })
  public void unwrappedContent_withFragment_expectDefinedContent(Fragment fragment,
      String expectedContentFileName) throws Exception {

    final String expectedContent = readText(expectedContentFileName);
    final String unwrappedContent = FragmentContentExtractor.unwrapContent(fragment);
    assertThat(expectedContent, equalToIgnoringWhiteSpace(unwrappedContent));
  }

  @TestWith(value = {
      "io/knotx/fragments/simple_default_snippet.txt;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_default_snippet.txt;io/knotx/fragments/complex_snippet-expected_content.txt",
      "io/knotx/fragments/simple_custom_snippet.txt;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_custom_snippet.txt;io/knotx/fragments/complex_snippet-expected_content.txt"
  })
  public void unwrappedContent_withString_expectDefinedContent(String snippetFileName,
      String expectedContentFileName) throws Exception {

    final String expectedContent = readText(expectedContentFileName);
    final Element element = FragmentContentExtractor.unwrapContent(readText(snippetFileName));

    assertThat(element.toString(), equalsToWithHtmlFormatting(expectedContent));
  }

  @TestWith(value = {
      "io/knotx/fragments/simple_default_snippet.txt;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_default_snippet.txt;io/knotx/fragments/complex_snippet-expected_content.txt",
      "io/knotx/fragments/simple_custom_snippet.txt;io/knotx/fragments/simple_snippet-expected_content.txt",
      "io/knotx/fragments/complex_custom_snippet.txt;io/knotx/fragments/complex_snippet-expected_content.txt"
  })
  public void unwrapFragmentContent_withFragment_expectDefinedContent(Fragment fragment,
      String expectedContentFileName) throws Exception {

    final String expectedContent = readText(expectedContentFileName);
    final Element element = FragmentContentExtractor.unwrapFragmentContent(fragment);

    assertThat(element.toString(), equalsToWithHtmlFormatting(expectedContent));
  }

  @TestWith({
      "io/knotx/fragments/empty_snippet.txt",
      "io/knotx/fragments/raw_snippet.txt"
  })
  public void getUnwrappedContent_withRawFragment_expectNotChangedContent(Fragment fragment)
      throws Exception {
    final String unwrappedContent = FragmentContentExtractor.unwrapContent(fragment);
    assertThat(unwrappedContent, equalToIgnoringWhiteSpace(fragment.content()));
  }

  @Test
  public void getUnwrappedContent_withNullFragment_expectNull() throws Exception {
    assertNull(FragmentContentExtractor.unwrapContent((String) null));
    assertNull(FragmentContentExtractor.unwrapContent((Fragment) null));
  }

  @Coercion
  public Fragment provideFragment(String fragmentContentFile) throws IOException {
    final String fragmentContent = readText(fragmentContentFile);
    Fragment fragmentMock = Mockito.mock(Fragment.class);
    when(fragmentMock.isRaw())
        .thenReturn(!fragmentContent.contains(FragmentConstants.SNIPPET_IDENTIFIER_NAME));
    when(fragmentMock.content()).thenReturn(fragmentContent);
    return fragmentMock;
  }

  private String readText(String path) throws IOException {
    return CharStreams
        .toString(new InputStreamReader(Resources.getResource(path).openStream(), "utf-8"));
  }

}
