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
import io.knotx.junit.converter.FragmentArgumentConverter;
import io.knotx.junit5.util.FileReader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

public class UnprocessedFragmentStrategyTest {

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "io/knotx/assembler/simple_fragment.txt;io/knotx/assembler/simple_fragment.txt;knotx:snippet;",
      "io/knotx/assembler/raw_fragment.txt;io/knotx/assembler/raw_fragment.txt;knotx:snippet;",
      "io/knotx/assembler/customTagAndParamsPrefix_snippet.txt;io/knotx/assembler/customTagAndParamsPrefix_snippet.txt;knotx:snippet;"
  })
  public void asIs_whenConfiguredSnippetTag_expectIgnoredContent(
      @ConvertWith(FragmentArgumentConverter.class) Fragment fragment,
      String expectedContentFileName) throws Exception {
    final String unwrappedContent = UnprocessedFragmentStrategy.AS_IS
        .get(fragment);
    final String expectedContent = FileReader.readText(expectedContentFileName);

    assertThat(unwrappedContent, equalToIgnoringWhiteSpace(expectedContent));
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "io/knotx/assembler/simple_fragment.txt;io/knotx/assembler/simple_fragment-expected_ignored_content.txt;knotx:snippet;",
      "io/knotx/assembler/raw_fragment.txt;io/knotx/assembler/raw_fragment.txt;knotx:snippet;"
      //when fragment is a raw fragment, it is not ignored
  })
  public void ignore_whenConfiguredSnippetTag_expectIgnoredContent(
      @ConvertWith(FragmentArgumentConverter.class) Fragment fragment,
      String expectedContentFileName) throws Exception {
    // given
    Mockito.when(fragment.unprocessed()).thenReturn(true);

    // when
    final String unwrappedContent = UnprocessedFragmentStrategy.IGNORE
        .get(fragment);

    // then
    final String expectedContent = FileReader.readText(expectedContentFileName);
    assertThat(unwrappedContent, equalToIgnoringWhiteSpace(expectedContent));
  }

}
