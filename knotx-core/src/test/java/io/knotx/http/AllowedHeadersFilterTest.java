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
package io.knotx.http;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Test;


public class AllowedHeadersFilterTest {

  private static final List<String> TEST_HEADERS =
      Lists.newArrayList("Content-Type", "content-length", "Accept", "Location");

  private StringToPatternFunction patternGenerator = new StringToPatternFunction();

  @Test
  public void whenNoAllowedHeadersAvailable_expectNoHeadersPassed() {
    AllowedHeadersFilter filter = AllowedHeadersFilter.create(Lists.newArrayList());

    assertEquals(0, filterHeaders(filter).size());
  }

  @Test
  public void whenAllAllowedHeadersAvailable_expectAllHeadersPassed() {
    AllowedHeadersFilter filter = AllowedHeadersFilter.create(
        Lists.newArrayList(patternGenerator.apply("*")));

    assertEquals(TEST_HEADERS.size(), filterHeaders(filter).size());
  }

  @Test
  public void whenWildcardPatternAvailable_expectMatchingHeadersPassed() {
    AllowedHeadersFilter filter = AllowedHeadersFilter.create(
        Lists.newArrayList(patternGenerator.apply("content-*")));

    List<String> filtered = filterHeaders(filter);

    assertEquals(2, filtered.size());
    assertEquals("Content-Type", filtered.get(0));
    assertEquals("content-length", filtered.get(1));
  }

  @Test
  public void whenPatternAvailableWithDifferentCasing_expectMatchingHeaderPassed() {
    AllowedHeadersFilter filter = AllowedHeadersFilter.create(
        Lists.newArrayList(patternGenerator.apply("content-type")));

    List<String> filtered = filterHeaders(filter);

    assertEquals(1, filtered.size());
    assertEquals("Content-Type", filtered.get(0));
  }

  @Test
  public void whenNoWildcardPatternsAvailableWithDifferentCasing_expectExactMatchingHeadersPassed() {
    AllowedHeadersFilter filter = AllowedHeadersFilter.create(
        Lists.newArrayList(patternGenerator.apply("content-type"),
            patternGenerator.apply("Location")));

    List<String> filtered = filterHeaders(filter);

    assertEquals(2, filtered.size());
    assertEquals("Content-Type", filtered.get(0));
    assertEquals("Location", filtered.get(1));
  }

  private List<String> filterHeaders(Predicate<String> filter) {
    return TEST_HEADERS.stream()
        .filter(filter)
        .collect(Collectors.toList());
  }
}
