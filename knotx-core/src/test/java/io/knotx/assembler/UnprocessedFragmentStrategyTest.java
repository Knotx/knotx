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
package io.knotx.assembler;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;

import io.knotx.dataobjects.Fragment;
import io.knotx.fragments.SnippetPatterns;
import io.knotx.junit5.KnotxTestUtils;
import io.knotx.junit5.KnotxArgumentConverter;
import io.knotx.options.SnippetOptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

public class UnprocessedFragmentStrategyTest {

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "io/knotx/assembler/simple_snippet.txt;io/knotx/assembler/simple_snippet.txt;script;data-knotx-",
      "io/knotx/assembler/customTag_snippet.txt;io/knotx/assembler/customTag_snippet.txt;knotx:snippet;data-knotx-",
      "io/knotx/assembler/raw_fragment.txt;io/knotx/assembler/raw_fragment.txt;script;data-knotx-",
      "io/knotx/assembler/customTagAndParamsPrefix_snippet.txt;io/knotx/assembler/customTagAndParamsPrefix_snippet.txt;knotx:snippet;"
  })
  public void asIs_whenConfiguredSnippetTag_expectIgnoredContent(
      @ConvertWith(KnotxArgumentConverter.class) Fragment fragment,
      String expectedContentFileName, String snippetTagName, String paramsPrefix) throws Exception {
    final String unwrappedContent = UnprocessedFragmentStrategy.AS_IS
        .get(fragment, new SnippetPatterns(buildOptions(snippetTagName, paramsPrefix)));
    final String expectedContent = KnotxTestUtils.readText(expectedContentFileName);

    assertThat(unwrappedContent, equalToIgnoringWhiteSpace(expectedContent));
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "io/knotx/assembler/simple_snippet.txt;io/knotx/assembler/simple_snippet-expected_unwrapped_content.txt;script;data-knotx-",
      "io/knotx/assembler/customTag_snippet.txt|knotx:snippet;io/knotx/assembler/simple_snippet-expected_unwrapped_content.txt;knotx:snippet;data-knotx-",
      "io/knotx/assembler/big_snippet.txt;io/knotx/assembler/big_snippet-expected_unwrapped_content.txt;script;data-knotx-",
      "io/knotx/assembler/customTagAndParamsPrefix_snippet.txt|knotx:snippet|;io/knotx/assembler/simple_snippet-expected_unwrapped_content.txt;knotx:snippet;"
  })
  public void unwrap_whenConfiguredSnippetTag_expectDefinedContentWithComments(
      @ConvertWith(KnotxArgumentConverter.class) Fragment fragment,
      String expectedContentFileName, String snippetTagName, String paramsPrefix) throws Exception {
    final String unwrappedContent = UnprocessedFragmentStrategy.UNWRAP
        .get(fragment, new SnippetPatterns(buildOptions(snippetTagName, paramsPrefix)));
    final String expectedContent = KnotxTestUtils.readText(expectedContentFileName);

    assertThat(unwrappedContent, equalToIgnoringWhiteSpace(expectedContent));
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "io/knotx/assembler/simple_snippet.txt;io/knotx/assembler/simple_snippet-expected_ignored_content.txt;script;data-knotx-",
      "io/knotx/assembler/customTag_snippet.txt;io/knotx/assembler/simple_snippet-expected_ignored_content.txt;knotx:snippet;data-knotx-",
      "io/knotx/assembler/raw_fragment.txt;io/knotx/assembler/raw_fragment.txt;script;data-knotx-"
      //when fragment is a raw fragment, it is not ignored
  })
  public void ignore_whenConfiguredSnippetTag_expectIgnoredContent(
      @ConvertWith(KnotxArgumentConverter.class) Fragment fragment,
      String expectedContentFileName, String snippetTagName, String paramsPrefix) throws Exception {
    final String unwrappedContent = UnprocessedFragmentStrategy.IGNORE
        .get(fragment, new SnippetPatterns(buildOptions(snippetTagName, paramsPrefix)));
    final String expectedContent = KnotxTestUtils.readText(expectedContentFileName);

    assertThat(unwrappedContent, equalToIgnoringWhiteSpace(expectedContent));
  }

  private SnippetOptions buildOptions(String snippetTagName, String snippetParamPrefix) {
    return new SnippetOptions()
        .setTagName(snippetTagName)
        .setParamsPrefix(snippetParamPrefix);
  }

}
