/*
 * Knot.x - Reactive microservice assembler - API
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
package com.cognifide.knotx.core.serviceadapter.http;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertEquals;

public class AllowedHeadersFilterTest {

  private final static List<String> TEST_HEADERS =
      Lists.newArrayList("Content-Type", "content-length", "Accept", "Location");

  private StringToPatternMap patternGenerator = new StringToPatternMap();

  @Test
  public void whenNoAllowedHeadersAvailable_expectNoHeadersPassed() {
    AllowedHeadersFilter filter = AllowedHeadersFilter.create(Lists.newArrayList());

    assertEquals(0, filterHeaders(filter).size());
  }

  @Test
  public void whenAllAllowedHeadersAvailable_expectAllHeadersPassed() {
    AllowedHeadersFilter filter = AllowedHeadersFilter.create(Lists.newArrayList(patternGenerator.apply("*")));

    assertEquals(TEST_HEADERS.size(), filterHeaders(filter).size());
  }

  @Test
  public void whenWildcardPatternAvailable_expectMatchingHeadersPassed() {
    AllowedHeadersFilter filter = AllowedHeadersFilter.create(Lists.newArrayList(patternGenerator.apply("content-*")));

    List<String> filtered = filterHeaders(filter);

    assertEquals(2, filtered.size());
    assertThat("Content-Type", equalToIgnoringCase(filtered.get(0)));
    assertThat("content-length", equalToIgnoringCase(filtered.get(1)));
  }

  @Test
  public void whenNoWildcardPatternAvailable_expectExactMatchingHeaderPassed() {
    AllowedHeadersFilter filter = AllowedHeadersFilter.create(Lists.newArrayList(patternGenerator.apply("content-type")));

    List<String> filtered = filterHeaders(filter);

    assertEquals(1, filtered.size());
    assertThat("Content-Type", equalToIgnoringCase(filtered.get(0)));
  }

  @Test
  public void whenNoWildcardPatternsAvailable_expectExactMatchingHeadersPassed() {
    AllowedHeadersFilter filter = AllowedHeadersFilter.create(Lists.newArrayList(patternGenerator.apply("content-type"), patternGenerator.apply("Location")));

    List<String> filtered = filterHeaders(filter);

    assertEquals(2, filtered.size());

    assertThat("Content-type", equalToIgnoringCase(filtered.get(0).toLowerCase()));
    assertThat("Location", equalToIgnoringCase(filtered.get(1).toLowerCase()));
  }

  private List<String> filterHeaders(Predicate<String> filter) {
    return TEST_HEADERS.stream()
        .filter(filter)
        .collect(Collectors.toList());
  }
}
