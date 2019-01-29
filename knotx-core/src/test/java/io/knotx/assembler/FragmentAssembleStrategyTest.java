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
package io.knotx.assembler;

import static org.junit.Assert.assertEquals;

import io.knotx.fragment.FragmentProcessingLog;
import io.knotx.fragment.FragmentProcessingStatus;
import io.knotx.fragment.NewFragment;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

public class FragmentAssembleStrategyTest {

  @Test
  public void asIs_whenFragment_expectFragment() {
    // given
    String expectedBody = "<h1>Some text</h1>\n"
        + "<p>Some text</p>";
    NewFragment fragment = new NewFragment("_STATIC", new JsonObject(), expectedBody);

    // when
    String actualBody = FragmentAssembleStrategy.AS_IS.extractBody(fragment);

    assertEquals(expectedBody, actualBody);
  }

  @Test
  public void asIs_whenUnprocessedFragment_expectFragment() {
    // given
    String expectedBody = "<h1>Some text</h1>\n"
        + "<p>Some text</p>";
    NewFragment fragment = new NewFragment("_STATIC", new JsonObject(), expectedBody);
    fragment.appendLog(new FragmentProcessingLog("anyHandler").setStatus(
        FragmentProcessingStatus.UNPROCESSED));

    // when
    String actualBody = FragmentAssembleStrategy.AS_IS.extractBody(fragment);

    assertEquals(expectedBody, actualBody);
  }

  @Test
  public void ignore_whenUnprocessedFragment_expectIgnoredBody() {
    // given
    NewFragment fragment = new NewFragment("_STATIC", new JsonObject(), "ANY BODY");
    fragment.appendLog(new FragmentProcessingLog("anyHandler").setStatus(
        FragmentProcessingStatus.UNPROCESSED));

    // when
    final String actualBody = FragmentAssembleStrategy.IGNORE.extractBody(fragment);

    // then
    assertEquals(FragmentAssembleStrategy.SNIPPET_IGNORED, actualBody);
  }

  @Test
  public void ignore_whenProcessedFragment_expectIgnoredBody() {
    // given
    String expectedBody = "<h1>Some text</h1>\n"
        + "<p>Some text</p>";
    NewFragment fragment = new NewFragment("_STATIC", new JsonObject(), expectedBody);
    fragment.appendLog(new FragmentProcessingLog("anyHandler").setStatus(
        FragmentProcessingStatus.SUCCESS));

    // when
    final String actualBody = FragmentAssembleStrategy.IGNORE.extractBody(fragment);

    // then
    assertEquals(expectedBody, actualBody);
  }

}
