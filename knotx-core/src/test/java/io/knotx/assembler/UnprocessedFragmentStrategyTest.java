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

import com.googlecode.zohhak.api.Configure;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import io.knotx.dataobjects.Fragment;
import io.knotx.fragments.SnippetPatterns;
import io.knotx.junit.coercers.KnotxCoercers;
import io.knotx.junit.util.FileReader;
import org.junit.runner.RunWith;

@RunWith(ZohhakRunner.class)
@Configure(separator = ";", coercers = KnotxCoercers.class)
public class UnprocessedFragmentStrategyTest {

  @TestWith({
      "io/knotx/assembler/simple_snippet.txt;io/knotx/assembler/simple_snippet.txt;script",
      "io/knotx/assembler/customTag_snippet.txt;io/knotx/assembler/customTag_snippet.txt;knotx:snippet",
      "io/knotx/assembler/raw_fragment.txt;io/knotx/assembler/raw_fragment.txt;script"
  })
  public void asIs_whenConfiguredSnippetTag_expectIgnoredContent(Fragment fragment,
      String expectedContentFileName, String snippetTagName) throws Exception {
    final String unwrappedContent = UnprocessedFragmentStrategy.AS_IS
        .get(fragment, new SnippetPatterns(snippetTagName));
    final String expectedContent = FileReader.readText(expectedContentFileName);

    assertThat(unwrappedContent, equalToIgnoringWhiteSpace(expectedContent));
  }

  @TestWith({
      "io/knotx/assembler/simple_snippet.txt;io/knotx/assembler/simple_snippet-expected_unwrapped_content.txt;script",
      "io/knotx/assembler/customTag_snippet.txt|knotx:snippet;io/knotx/assembler/simple_snippet-expected_unwrapped_content.txt;knotx:snippet",
      "io/knotx/assembler/big_snippet.txt;io/knotx/assembler/big_snippet-expected_unwrapped_content.txt;script"
  })
  public void unwrap_whenConfiguredSnippetTag_expectDefinedContentWithComments(Fragment fragment,
      String expectedContentFileName, String snippetTagName) throws Exception {
    final String unwrappedContent = UnprocessedFragmentStrategy.UNWRAP
        .get(fragment, new SnippetPatterns(snippetTagName));
    final String expectedContent = FileReader.readText(expectedContentFileName);

    assertThat(unwrappedContent, equalToIgnoringWhiteSpace(expectedContent));
  }

  @TestWith({
      "io/knotx/assembler/simple_snippet.txt;io/knotx/assembler/simple_snippet-expected_ignored_content.txt;script",
      "io/knotx/assembler/customTag_snippet.txt;io/knotx/assembler/simple_snippet-expected_ignored_content.txt;knotx:snippet",
      "io/knotx/assembler/raw_fragment.txt;io/knotx/assembler/raw_fragment.txt;script"
      //when fragment is a raw fragment, it is not ignored
  })
  public void ignore_whenConfiguredSnippetTag_expectIgnoredContent(Fragment fragment,
      String expectedContentFileName, String snippetTagName) throws Exception {
    final String unwrappedContent = UnprocessedFragmentStrategy.IGNORE
        .get(fragment, new SnippetPatterns(snippetTagName));
    final String expectedContent = FileReader.readText(expectedContentFileName);

    assertThat(unwrappedContent, equalToIgnoringWhiteSpace(expectedContent));
  }


}
