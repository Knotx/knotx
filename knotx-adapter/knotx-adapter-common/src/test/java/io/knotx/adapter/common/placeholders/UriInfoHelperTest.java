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
package io.knotx.adapter.common.placeholders;

import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class UriInfoHelperTest {

  @Parameterized.Parameter
  public String uri;
  @Parameterized.Parameter(value = 1)
  public String path;
  @Parameterized.Parameter(value = 2)
  public String selectorString;
  @Parameterized.Parameter(value = 3)
  public String extension;
  @Parameterized.Parameter(value = 4)
  public String suffix;

  @Parameterized.Parameters(name = "{index}: {0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{{"/a/b", "/a/b", null, null, null},
        {"/a/b.html", "/a/b", null, "html", null},
        {"/a/b.s1.html", "/a/b", "s1", "html", null},
        {"/a/b.s1.s2.html", "/a/b", "s1.s2", "html", null},
        {"/a/b.s1.html/c/d", "/a/b", "s1", "html", "/c/d"},
        {"/a/b.s1.s2.html/c/d", "/a/b", "s1.s2", "html", "/c/d"},
        {"/a/b.html/c/d.s.txt", "/a/b", null, "html", "/c/d.s.txt"},
        {"/a/b.s1.html/c/d.s.txt", "/a/b", "s1", "html", "/c/d.s.txt"},
        {"/a/b.s1.s2.html/c/d.s.txt", "/a/b", "s1.s2", "html", "/c/d.s.txt"},
        {"/a/b?q=v", "/a/b", null, null, null},
        {"/a/b.html?q=v", "/a/b", null, "html", null},
        {"/a/b.s1.html?q=v", "/a/b", "s1", "html", null},
        {"/a/b.s1.s2.html?q=v", "/a/b", "s1.s2", "html", null},
        {"/a/b.s1.html/c/d?q=v", "/a/b", "s1", "html", "/c/d"},
        {"/a/b.s1.s2.html/c/d?q=v", "/a/b", "s1.s2", "html", "/c/d"},
        {"/a/b.html/c/d.s.txt?q=v", "/a/b", null, "html", "/c/d.s.txt"},
        {"/a/b.s1.html/c/d.s.txt?q=v", "/a/b", "s1", "html", "/c/d.s.txt"},
        {"/a/b.s1.s2.html/c/d.s.txt?q=v", "/a/b", "s1.s2", "html", "/c/d.s.txt"},
        {"/a/b#f", "/a/b", null, null, null}, {"/a/b.html#f", "/a/b", null, "html", null},
        {"/a/b.s1.html#f", "/a/b", "s1", "html", null},
        {"/a/b.s1.s2.html#f", "/a/b", "s1.s2", "html", null},
        {"/a/b.s1.html/c/d#f", "/a/b", "s1", "html", "/c/d"},
        {"/a/b.s1.s2.html/c/d#f", "/a/b", "s1.s2", "html", "/c/d"},
        {"/a/b.html/c/d.s.txt#f", "/a/b", null, "html", "/c/d.s.txt"},
        {"/a/b.s1.html/c/d.s.txt#f", "/a/b", "s1", "html", "/c/d.s.txt"},
        {"/a/b.s1.s2.html/c/d.s.txt#f", "/a/b", "s1.s2", "html", "/c/d.s.txt"}});
  }

  @Test
  public void getUriInfo_whenGivenUrl_expectProperlyDecomposedUrl() {
    final SlingUriInfo uriInfo = SlingUriInfoHelper.getUriInfo(uri);
    boolean result = StringUtils.equals(uriInfo.getPath(), path);
    result &= StringUtils.equals(uriInfo.getSelectorString(), selectorString);
    result &= StringUtils.equals(uriInfo.getExtension(), extension);
    result &= StringUtils.equals(uriInfo.getSuffix(), suffix);
    Assert.assertTrue(result);
  }
}
