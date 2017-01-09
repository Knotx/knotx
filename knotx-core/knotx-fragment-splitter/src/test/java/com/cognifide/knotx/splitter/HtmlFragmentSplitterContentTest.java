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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.launcher.junit.FileReader;
import com.cognifide.knotx.splitter.impl.FragmentSplitter;
import com.cognifide.knotx.splitter.impl.HtmlFragmentSplitter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

//FIXME: rename tests to meet naming convention
@RunWith(value = Parameterized.class)
public class HtmlFragmentSplitterContentTest {

  private List<Fragment> fragments;

  private int fragmentId;

  private String fragmentFile;

  public HtmlFragmentSplitterContentTest(int fragmentId, String fragmentFile) {
    this.fragmentId = fragmentId;
    this.fragmentFile = fragmentFile;
  }

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {0, "fragment1.txt"},
        {1, "fragment2.txt"},
        {2, "fragment3.txt"},
        {3, "fragment4.txt"},
        {4, "fragment5.txt"},
        {5, "fragment6.txt"},
        {6, "fragment7.txt"},
        {7, "fragment8.txt"}
    });
  }

  @Before
  public void setUp() throws Exception {
    FragmentSplitter splitter = new HtmlFragmentSplitter();
    fragments = splitter.split(FileReader.readText("test-many-fragments.html"));
  }

  @Test
  public void testFragment() throws Exception {
    assertThat(fragments.get(fragmentId).getContent().trim(), equalTo(FileReader.readText(fragmentFile).trim()));
  }
}
