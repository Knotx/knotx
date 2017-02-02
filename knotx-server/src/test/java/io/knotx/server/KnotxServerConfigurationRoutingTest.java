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
package io.knotx.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

import io.knotx.junit.util.FileReader;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import java.util.EnumMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class KnotxServerConfigurationRoutingTest {

  public static final int FIRST_IDX = 0;
  public static final int SECOND_IDX = 1;
  private EnumMap<HttpMethod, List<RoutingEntry>> engineRouting;

  @Before
  public void before() throws Exception {
    engineRouting = new KnotxServerConfiguration(
        new JsonObject(FileReader.readText("test-config-server.json"))).getEngineRouting();
  }

  @Test
  public void whenConfigWithTwoRoutingMethods_expectTwoMethodsInRouting() throws Exception {
    assertThat(engineRouting.keySet().size(), equalTo(2));
    assertThat(engineRouting.keySet(), hasItem(HttpMethod.GET));
    assertThat(engineRouting.keySet(), hasItem(HttpMethod.POST));
  }

  @Test
  public void whenConfigWithGetRouting_expectTwoRoutings() throws Exception {
    assertThat(engineRouting.get(HttpMethod.GET).size(), equalTo(2));
    assertThat(engineRouting.get(HttpMethod.GET).get(FIRST_IDX).path(),
        equalTo("/content/local/.*"));
    assertThat(engineRouting.get(HttpMethod.GET).get(FIRST_IDX).address(), equalTo("A-engine"));

    assertThat(engineRouting.get(HttpMethod.GET).get(SECOND_IDX).path(), equalTo("/content/.*"));
    assertThat(engineRouting.get(HttpMethod.GET).get(SECOND_IDX).address(), equalTo("C-engine"));
  }

  @Test
  public void whenConfigWithPostRouting_expectTwoRoutings() throws Exception {
    assertThat(engineRouting.get(HttpMethod.POST).size(), equalTo(2));
    assertThat(engineRouting.get(HttpMethod.POST).get(FIRST_IDX).path(),
        equalTo("/content/local/.*"));
    assertThat(engineRouting.get(HttpMethod.POST).get(FIRST_IDX).address(),
        equalTo("A-post-engine"));

    assertThat(engineRouting.get(HttpMethod.POST).get(SECOND_IDX).path(), equalTo("/content/.*"));
    assertThat(engineRouting.get(HttpMethod.POST).get(SECOND_IDX).address(), equalTo("B-engine"));
  }

  @Test
  public void whenConfigWithGetRoutingAndTransitions_expectTransitionsExistsWhenDefined()
      throws Exception {
    assertThat(engineRouting.get(HttpMethod.GET).get(FIRST_IDX).onTransition().isEmpty(),
        equalTo(false));
    assertThat(engineRouting.get(HttpMethod.GET).get(FIRST_IDX).onTransition().keySet().size(),
        equalTo(1));
    assertThat(engineRouting.get(HttpMethod.GET).get(FIRST_IDX).onTransition().keySet(),
        hasItem("go-c"));
    assertThat(
        engineRouting.get(HttpMethod.GET).get(FIRST_IDX).onTransition().get("go-c").address(),
        equalTo("C-engine"));
    assertThat(
        engineRouting.get(HttpMethod.GET).get(FIRST_IDX).onTransition().get("go-c").onTransition()
            .isEmpty(), equalTo(true));

    assertThat(engineRouting.get(HttpMethod.GET).get(SECOND_IDX).onTransition().isEmpty(),
        equalTo(true));
  }

  @Test
  public void whenConfigWithPostRoutingAndTransitions_expectTransitionsExistsWhenDefined()
      throws Exception {
    assertThat(engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().isEmpty(),
        equalTo(false));
    assertThat(engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().keySet().size(),
        equalTo(2));
    assertThat(engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().keySet(),
        hasItem("go-b"));
    assertThat(engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().keySet(),
        hasItem("go-c"));

    assertThat(
        engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().get("go-b").address(),
        equalTo("B-engine"));
    assertThat(
        engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().get("go-b").onTransition()
            .isEmpty(), equalTo(false));
    assertThat(
        engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().get("go-b").onTransition()
            .keySet().size(), equalTo(1));
    assertThat(
        engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().get("go-b").onTransition()
            .keySet(), hasItem("go-c"));
    assertThat(
        engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().get("go-b").onTransition()
            .get("go-c").address(),
        equalTo("C-engine"));
    assertThat(
        engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().get("go-b").onTransition()
            .get("go-c").onTransition().isEmpty(),
        equalTo(true));

    assertThat(
        engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().get("go-c").address(),
        equalTo("C-engine"));
    assertThat(
        engineRouting.get(HttpMethod.POST).get(FIRST_IDX).onTransition().get("go-c").onTransition()
            .isEmpty(), equalTo(true));

    assertThat(engineRouting.get(HttpMethod.POST).get(SECOND_IDX).onTransition().isEmpty(),
        equalTo(false));
    assertThat(engineRouting.get(HttpMethod.POST).get(SECOND_IDX).onTransition().keySet().size(),
        equalTo(1));
    assertThat(engineRouting.get(HttpMethod.POST).get(SECOND_IDX).onTransition().keySet(),
        hasItem("go-c"));
    assertThat(
        engineRouting.get(HttpMethod.POST).get(SECOND_IDX).onTransition().get("go-c").address(),
        equalTo("C-engine"));
    assertThat(
        engineRouting.get(HttpMethod.POST).get(SECOND_IDX).onTransition().get("go-c").onTransition()
            .isEmpty(), equalTo(true));
  }
}
