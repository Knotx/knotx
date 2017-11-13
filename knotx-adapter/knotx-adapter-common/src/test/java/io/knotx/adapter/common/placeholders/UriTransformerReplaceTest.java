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

import io.knotx.dataobjects.ClientRequest;
import io.vertx.reactivex.core.MultiMap;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class UriTransformerReplaceTest {

  @Parameterized.Parameter
  public String servicePath;
  @Parameterized.Parameter(value = 1)
  public String requestedUri;
  @Parameterized.Parameter(value = 2)
  public String expectedUri;

  @Parameterized.Parameters(name = "{index}: {0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        // SLING URI DECOMPOSITION
        // path
        {"/path.html?path={slingUri.path}", "/a/b/c/d.s1.s2.html/c/d.s.txt#f",
            "/path.html?path=/a/b/c/d"},
        {"/path.html?path={slingUri.path}", "/a/b/c/d.s1.s2.html/c/d.s.txt#f",
            "/path.html?path=/a/b/c/d"},
        // pathparts
        {"/path/{slingUri.pathpart[2]}.html", "/a/b/c/d/e.s1.s2.html/c/d.s.txt#f",
            "/path/c.html"},
        {"/path/{slingUri.pathpart[7]}.html", "/a/b/c/d/e.s1.s2.html/c/d.s.txt#f",
            "/path/.html"},
        // extension
        {"/path/second.html/a.{slingUri.extension}", "/a/b/c/d/e.s1.s2.html/suffix.xml",
            "/path/second.html/a.html"},
        // selectors
        {"/selectors.{slingUri.selectorstring}.html", "/a/b.s1.s2.html/c/d.s.txt#f",
            "/selectors.s1.s2.html"},
        {"/selectors.{slingUri.selector[0]}.html", "/a/b.s1.s2.html/c/d.s.txt#f",
            "/selectors.s1.html"},
        {"/selectors.{slingUri.selector[2]}.html", "/a/b.s1.s2.html/c/d.s.txt#f",
            "/selectors..html"},
        // suffix
        {"/suffix.html{slingUri.suffix}", "/a/b/dsds.dd.html/my/nice/suffix.html",
            "/suffix.html/my/nice/suffix.html"},
        // REGULAR URI DECOMPOSITION
        // path
        {"/path.html?path={uri.path}", "/a/b/c/d.s1.s2.html/c/d.s.txt#f",
            "/path.html?path=/a/b/c/d.s1.s2.html/c/d.s.txt"},
        // pathpart
        {"/path/{uri.pathpart[5]}.html", "/a/b/c/d/e.s1.s2.html/c/d.s.txt#f",
            "/path/e.s1.s2.html.html"},
        // pathpart
        {"/path/{uri.pathpart[7]}.html", "/a/b/c/d/e.s1.s2.html/c/d.s.txt#f",
            "/path/d.s.txt.html"},
        // pathpart
        {"/path/{uri.pathpart[8]}.html", "/a/b/c/d/e.s1.s2.html/c/d.s.txt#f",
            "/path/.html"},
        // extension
        {"/path/second.html/a.{uri.extension}", "/a/b.s1.s2.html/c/d.xml",
            "/path/second.html/a.xml"},
        // extension
        {"/path/second.html/a.{uri.extension}", "/a/b",
            "/path/second.html/a."},
        // param
        {"/solr/search/{param.q}", "/c/d/s?q=my search is fetched from static getParams()",
            "/solr/search/knot%20%26%20x"},
        // headers
        {"/solr/{header.authorizationId}/", "/c/d/s?q=my action from headers",
            "/solr/486434684345/"},
        // invalid
        {"/selectors.{invalid}.html", "/a/b.s1.s2.html/c/d.s.txt#f", "/selectors..html"}});
  }

  private static MultiMap getHeadersMultiMap() {
    MultiMap map = MultiMap.caseInsensitiveMultiMap();
    map.add("authorizationId", "486434684345");
    return map;
  }

  private static MultiMap getParamsMultiMap() {
    MultiMap map = MultiMap.caseInsensitiveMultiMap();
    map.add("q", "knot & x");
    map.add("action", "/some/action/path");
    return map;
  }

  @Test
  public void getServiceUri_whenGivenUriWithPlaceholdersAndMockedRequest_expectPlaceholdersSubstitutedWithValues() {
    ClientRequest httpRequest = new ClientRequest().setHeaders(getHeadersMultiMap())
        .setParams(getParamsMultiMap()).setPath(requestedUri);

    String finalUri = UriTransformer.resolveServicePath(servicePath, httpRequest);

    Assert.assertEquals(expectedUri, finalUri);
  }
}
