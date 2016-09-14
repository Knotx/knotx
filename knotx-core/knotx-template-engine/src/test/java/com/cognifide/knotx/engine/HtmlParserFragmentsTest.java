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

import com.cognifide.knotx.engine.parser.HtmlParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(value = Parameterized.class)
public class HtmlParserFragmentsTest extends AbstractKnotxConfigurationTest {

  private String TEST_HTML = "test.html";

  private HtmlParser test;

  private int fragmentId;

  private String fragmentFile;

  public HtmlParserFragmentsTest(int fragmentId, String fragmentFile) {
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
        {6, "fragment7.txt"}
    });
  }

  @Before
  public void setUp() throws Exception {
    test = new HtmlParser(readText(TEST_HTML));
  }

  @Test
  public void testFragment() throws Exception {
    assertThat(test.getFragments().get(fragmentId).getContent().trim(), equalTo(readText(fragmentFile).trim()));
  }
}
