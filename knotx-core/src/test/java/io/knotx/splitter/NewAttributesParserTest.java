/*
 * Copyright (C) 2019 Cognifide Limited
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
package io.knotx.splitter;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewAttributesParserTest {

  private AttributesParser tested;

  @BeforeEach
  void setUp() {
    tested = new NewAttributesParser();
  }

  @Test
  public void split_whenNull_expectNoAttributes() {
    // when
    List<Pair<String, String>> pairs = tested.get(null);

    // then
    Assert.assertTrue(pairs.isEmpty());
  }

  @Test
  public void split_whenEmptyString_expectNoAttributes() {
    // when
    List<Pair<String, String>> pairs = tested.get("");

    // then
    Assert.assertTrue(pairs.isEmpty());
  }

  @Test
  public void split_whenOneAttributeDoubleMarks_expectOneAttribute() {
    // when
    List<Pair<String, String>> pairs = tested.get("attribute=\"value\"");

    // then
    Assert.assertEquals(1, pairs.size());
    Assert.assertEquals("attribute", pairs.get(0).getKey());
    Assert.assertEquals("value", pairs.get(0).getValue());
  }

  @Test
  public void split_whenOneAttributeSingleMarks_expectOneAttribute() {
    // when
    List<Pair<String, String>> pairs = tested.get("attribute='value'");

    // then
    Assert.assertEquals(1, pairs.size());
    Assert.assertEquals("attribute", pairs.get(0).getKey());
    Assert.assertEquals("value", pairs.get(0).getValue());
  }

  @Test
  public void split_whenOneEmptyAttributeDoubleMarks_expectOneAttribute() {
    // when
    List<Pair<String, String>> pairs = tested.get("attribute=\"\"");

    // then
    assertEmptyAttribute(pairs);
  }

  @Test
  public void split_whenOneEmptyAttributeSingleMarks_expectOneAttribute() {
    // when
    List<Pair<String, String>> pairs = tested.get("attribute=''");

    // then
    assertEmptyAttribute(pairs);
  }

  @Test
  public void split_whenOneAttributeWithDash_expectOneAttribute() {
    // when
    List<Pair<String, String>> pairs = tested.get("a-t-t-r-i-b-u-t-e=\"value\"");

    // then
    Assert.assertEquals(1, pairs.size());
    Assert.assertEquals("a-t-t-r-i-b-u-t-e", pairs.get(0).getKey());
    Assert.assertEquals("value", pairs.get(0).getValue());
  }

  @Test
  public void split_whenOneAttributeWithWhitespaces_expectOneAttribute() {
    // when
    List<Pair<String, String>> pairs = tested.get("attribute=\"value with space\"");

    // then
    Assert.assertEquals(1, pairs.size());
    Assert.assertEquals("attribute", pairs.get(0).getKey());
    Assert.assertEquals("value with space", pairs.get(0).getValue());
  }

  @Test
  public void split_whenOneJsonAttributeDoubleMarks_expectOneAttribute() {
    // when
    List<Pair<String, String>> pairs = tested.get("attribute=\"{\\\"key\\\"=\\\"value\\\"}\"");

    // then
    Assert.assertEquals(1, pairs.size());
    Assert.assertEquals("attribute", pairs.get(0).getKey());
    Assert.assertEquals("{\\\"key\\\"=\\\"value\\\"}", pairs.get(0).getValue());
  }

  @Test
  public void split_whenOneJsonAttributeSingleMarks_expectOneAttribute() {
    // when
    List<Pair<String, String>> pairs = tested.get("attribute='{\"key\"=\"value\"}'");

    // then
    Assert.assertEquals(1, pairs.size());
    Assert.assertEquals("attribute", pairs.get(0).getKey());
    Assert.assertEquals("{\"key\"=\"value\"}", pairs.get(0).getValue());
  }

  @Test
  public void split_whenManyAttribute_expectManyAttributes() {
    // when
    List<Pair<String, String>> pairs = tested.get(
        "attributeOne=\"{\\\"key\\\"=\\\"value\\\"}\" attributeTwo=\"valueTwo\" attributeThree=''");

    // then
    Assert.assertEquals(3, pairs.size());
    Assert.assertEquals("attributeOne", pairs.get(0).getKey());
    Assert.assertEquals("{\\\"key\\\"=\\\"value\\\"}", pairs.get(0).getValue());
    Assert.assertEquals("attributeTwo", pairs.get(1).getKey());
    Assert.assertEquals("valueTwo", pairs.get(1).getValue());
    Assert.assertEquals("attributeThree", pairs.get(2).getKey());
    Assert.assertEquals("", pairs.get(2).getValue());
  }

  private void assertEmptyAttribute(List<Pair<String, String>> pairs) {
    Assert.assertEquals(1, pairs.size());
    Assert.assertEquals("attribute", pairs.get(0).getKey());
    Assert.assertTrue(pairs.get(0).getValue().isEmpty());
  }

}
