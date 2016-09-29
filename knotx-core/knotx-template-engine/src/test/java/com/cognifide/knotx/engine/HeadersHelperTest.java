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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.regex.Pattern;

import io.vertx.core.json.JsonArray;
import io.vertx.rxjava.core.MultiMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

public class HeadersHelperTest {

    private static final JsonArray RULES_SET_ONE = new JsonArray();
    private static final JsonArray RULES_SET_TWO = new JsonArray().add("*");
    private static final JsonArray RULES_SET_THREE = new JsonArray().add("Accept*").add("*-ID").add("If*Match");
    private static final JsonArray RULES_SET_FOUR = new JsonArray().add("Content-Type").add("User-Agent");
    private static final JsonArray RULES_SET_FIVE = new JsonArray().add("X-((*");
    private static final JsonArray RULES_SET_SIX = new JsonArray().add("Accept*").add("*-ID");
    private static final JsonArray RULES_SET_SEVEN = new JsonArray().add("If*Match");

    private static final MultiMap HEADERS = MultiMap.caseInsensitiveMultiMap()
        .add("Accept", "text/plain").add("Accept-Charset", "text/plain")
        .add("Accept-Language","en-US").add("X-Request-ID", "requestId")
        .add("X-Correlation-ID", "correlationId").add("If-Match","match")
        .add("If-None-Match","noneMatch").add("Content-Type","application/x-www-form-urlencoded")
        .add("User-Agent", "userAgent");
    private static final MultiMap EXPECTED_HEADERS_ONE = MultiMap.caseInsensitiveMultiMap()
        .add("Accept-Charset", "text/plain").add("Accept-Language","en-US")
        .add("X-Request-ID", "requestId").add("X-Correlation-ID", "correlationId");
    private static final MultiMap EXPECTED_HEADERS_TWO = MultiMap.caseInsensitiveMultiMap()
        .add("If-Match","match").add("If-None-Match","noneMatch");
    private static final MultiMap EXPECTED_HEADERS_THREE = MultiMap.caseInsensitiveMultiMap()
        .add("Content-Type","application/x-www-form-urlencoded").add("User-Agent", "userAgent");

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getPatternsFromHeadersConfig_whenEmptyRulesProvided_expectEmptyList() {
        assertThat(HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_ONE), is(Collections.EMPTY_LIST));
    }

    @Test
    public void getPatternsFromHeadersConfig_whenSingleWildcardRuleProvided_expectOneItemListWithCorrectPatternReturned() {
        assertThat(HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_TWO).size(), is(1));
        assertThat(HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_TWO).get(0).pattern(), is(Pattern.compile("^(.+)$").pattern()));
    }

    @Test
    public void getPatternsFromHeadersConfig_whenRulesWithWildcardProvided_expectListWithCorrectPatterns() {
        assertThat(HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_THREE).size(), is(3));
        assertThat(HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_THREE).get(0).pattern(), is(Pattern.compile("^Accept(.+)$").pattern()));
        assertThat(HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_THREE).get(1).pattern(), is(Pattern.compile("^(.+)-ID$").pattern()));
        assertThat(HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_THREE).get(2).pattern(), is(Pattern.compile("^If(.+)Match$").pattern()));
    }

    @Test
    public void getPatternsFromHeadersConfig_whenRulesWithoutWildcardProvided_expectListWithCorrectPatterns() {
        assertThat(HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_FOUR).size(), is(2));
        assertThat(HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_FOUR).get(0).pattern(), is(Pattern.compile("^Content-Type$").pattern()));
        assertThat(HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_FOUR).get(1).pattern(), is(Pattern.compile("^User-Agent$").pattern()));
    }

    @Test
    public void getPatternsFromHeadersConfig_whenRulesWithIncorrectSyntaxProvided_expectRuntimeException() {
        exception.expect(RuntimeException.class);
        HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_FIVE);
    }

    @Test
    public void getFilteredHeaders_whenAllowedPatternsListIsEmpty_expectNoHeadersPreserved(){
        assertThat(HeadersHelper.getFilteredHeaders(HEADERS, HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_ONE)).size(), is(0));
    }

    @Test
    public void getFilteredHeaders_whenAllHeadersAllowed_expectAllHeadersPreserved(){
        assertThat(HeadersHelper.getFilteredHeaders(HEADERS, HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_TWO)).size(), is(9));
    }

    @Test
    public void getFilteredHeaders_whenWildcardAtEndOrBeginning_expectCorrectHeadersPreserved(){
        MultiMap filteredHeaders = HeadersHelper.getFilteredHeaders(HEADERS, HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_SIX));
        assertHeadersMapAreEqual(filteredHeaders, EXPECTED_HEADERS_ONE, 4);
    }

    @Test
    public void getFilteredHeaders_whenWildcardInTheMiddle_expectCorrectHeadersPreserved(){
        MultiMap filteredHeaders = HeadersHelper.getFilteredHeaders(HEADERS, HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_SEVEN));
        assertHeadersMapAreEqual(filteredHeaders, EXPECTED_HEADERS_TWO, 2);
    }
    @Test
    public void getFilteredHeaders_whenNoWildcardExpression_expectCorrectHeadersPreserved(){
        MultiMap filteredHeaders = HeadersHelper.getFilteredHeaders(HEADERS, HeadersHelper.getPatternsFromHeadersConfig(RULES_SET_FOUR));
        assertHeadersMapAreEqual(filteredHeaders, EXPECTED_HEADERS_THREE, 2);
    }

    private void assertHeadersMapAreEqual(MultiMap filteredHeaders, MultiMap expectedHeadersOne, int expectedSize) {
        assertThat(filteredHeaders.size(), is(expectedSize));
        filteredHeaders.names().forEach(s -> assertEquals(filteredHeaders.get(s), expectedHeadersOne.get(s)));
    }
}
