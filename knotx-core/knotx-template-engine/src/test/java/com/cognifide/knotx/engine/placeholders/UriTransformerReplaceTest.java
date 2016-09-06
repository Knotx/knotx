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
package com.cognifide.knotx.engine.placeholders;

import java.util.Arrays;
import java.util.Collection;

import org.jsoup.nodes.Attribute;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.cognifide.knotx.api.TemplateEngineRequest;
import com.cognifide.knotx.engine.service.ServiceEntry;

import io.vertx.rxjava.core.MultiMap;

@RunWith(Parameterized.class)
public class UriTransformerReplaceTest {

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // path
                {"/path.html?path={path}", "/a/b/c/d.s1.s2.html/c/d.s.txt#f",
                        "/path.html?path=/a/b/c/d"},
                {"/path.html?path={path}", "/a/b/c/d.s1.s2.html/c/d.s.txt#f",
                        "/path.html?path=/a/b/c/d"},
                // pathparts
                {"/path/{pathpart[2]}.html", "/a/b/c/d/e.s1.s2.html/c/d.s.txt#f", "/path/c.html"},
                {"/path/{pathpart[7]}.html", "/a/b/c/d/e.s1.s2.html/c/d.s.txt#f", "/path/.html"},
                // extension
                {"/path/second.html/a.{extension}", "/a/b/c/d/e.s1.s2.html",
                        "/path/second.html/a.html"},
                // selectors
                {"/selectors.{selectorstring}.html", "/a/b.s1.s2.html/c/d.s.txt#f",
                        "/selectors.s1.s2.html"},
                {"/selectors.{selector[0]}.html", "/a/b.s1.s2.html/c/d.s.txt#f",
                        "/selectors.s1.html"},
                {"/selectors.{selector[2]}.html", "/a/b.s1.s2.html/c/d.s.txt#f",
                        "/selectors..html"},
                // suffix
                {"/suffix.html{suffix}", "/a/b/dsds.dd.html/my/nice/suffix.html",
                        "/suffix.html/my/nice/suffix.html"},
                // invalid
                {"/selectors.{invalid}.html", "/a/b.s1.s2.html/c/d.s.txt#f", "/selectors..html"},
                // param
                {"/solr/search/{param.q}", "/c/d/s?q=my search is fetched from static getParams()",
                        "/solr/search/knot.x"},
                // headers
                {"/solr/{header.authorizationId}/", "/c/d/s?q=my action from headers",
                        "/solr/486434684345/"}});
    }

    @Parameterized.Parameter
    public String uri;

    @Parameterized.Parameter(value = 1)
    public String requestedUri;

    @Parameterized.Parameter(value = 2)
    public String expectedUri;

    @Test
    public void testReplace() {
        TemplateEngineRequest request = new TemplateEngineRequest(null, null, getHeadersMultiMap(),
                getParamsMultiMap(), new MultiMap(null), requestedUri);
        ServiceEntry serviceEntry = ServiceEntry.of(new Attribute("data-uri", uri));

        String finalUri = UriTransformer.getServiceUri(request, serviceEntry);

        Assert.assertEquals(expectedUri, finalUri);
    }

    private static MultiMap getHeadersMultiMap() {
        MultiMap map = MultiMap.caseInsensitiveMultiMap();
        map.add("authorizationId", "486434684345");
        return map;
    }

    private static MultiMap getParamsMultiMap() {
        MultiMap map = MultiMap.caseInsensitiveMultiMap();
        map.add("q", "knot.x");
        map.add("action", "/some/action/path");
        return map;
    }
}
