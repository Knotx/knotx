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
package io.knotx.knot.splitter.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.googlecode.zohhak.api.Configure;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import io.knotx.dataobjects.Fragment;
import io.knotx.junit.util.FileReader;
import io.knotx.knot.splitter.impl.FragmentSplitter;
import io.knotx.knot.splitter.impl.HtmlFragmentSplitter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(ZohhakRunner.class)
@Configure(separator = ";")
public class HtmlFragmentSplitterContentTest {

  private List<Fragment> defaultSnippetTagFragments;
  private List<Fragment> customSnippetTagFragments;

  @Before
  public void setUp() throws Exception {
    defaultSnippetTagFragments = new HtmlFragmentSplitter("script")
        .split(FileReader.readText("test-many-fragments.html"));
    customSnippetTagFragments = new HtmlFragmentSplitter("knotx:snippet")
        .split(FileReader.readText("test-many-fragments-custom-snippet.html"));
  }

  @TestWith({
      "0;fragment1.txt",
      "1;fragment2-snippet.txt",
      "2;fragment3.txt",
      "3;fragment4-snippet.txt",
      "4;fragment5-snippet.txt",
      "5;fragment6.txt",
      "6;fragment7-snippet.txt",
      "7;fragment8.txt",
      "8;fragment9-snippet.txt"
  })
  public void split_whenDefaultSnippetTag_expectNineFragments(int fragmentId, String fragmentFile)
      throws Exception {
    assertThat(defaultSnippetTagFragments.get(fragmentId).content().trim(),
        equalTo(FileReader.readText(fragmentFile).trim()));
  }

  @TestWith({
      "0;fragment1.txt",
      "1;fragment2-custom-snippet.txt",
      "2;fragment3.txt",
      "3;fragment4-custom-snippet.txt",
      "4;fragment5-custom-snippet.txt",
      "5;fragment6.txt",
      "6;fragment7-custom-snippet.txt",
      "7;fragment8.txt",
      "8;fragment9-custom-snippet.txt"
  })
  public void split_whenCustomSnippetTag_expect8Fragments(int fragmentId, String fragmentFile)
      throws Exception {
    assertThat(customSnippetTagFragments.get(fragmentId).content().trim(),
        equalTo(FileReader.readText(fragmentFile).trim()));
  }
}
