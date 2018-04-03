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
import io.knotx.server.configuration.KnotxServerOptions;
import io.knotx.server.configuration.MethodRoutingEntries;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class KnotxServerOptionsRoutingTest {

  private static final int FIRST_IDX = 0;
  private static final int SECOND_IDX = 1;
  private Map<String, MethodRoutingEntries> engineRouting;

  @Before
  public void before() throws Exception {
    engineRouting = new KnotxServerOptions(
        new JsonObject(FileReader.readText("io/knotx/server/test-config-server.json"))).getDefaultFlow()
        .getRouting();
  }

  @Test
  public void whenConfigWithTwoRoutingMethods_expectTwoMethodsInRouting() throws Exception {
    assertThat(engineRouting.keySet().size(), equalTo(2));
    assertThat(engineRouting.keySet(), hasItem(HttpMethod.GET.toString()));
    assertThat(engineRouting.keySet(), hasItem(HttpMethod.POST.toString()));
  }

  @Test
  public void whenConfigWithGetRouting_expectTwoRoutings() throws Exception {
    assertThat(engineRouting.get(HttpMethod.GET.toString()).getItems().size(), equalTo(2));
    assertThat(engineRouting.get(HttpMethod.GET.toString()).getItems().get(FIRST_IDX).getPath(),
        equalTo(".*/local/.*\\.html"));
    assertThat(engineRouting.get(HttpMethod.GET.toString()).getItems().get(FIRST_IDX).getAddress(), equalTo("A-engine"));

    assertThat(engineRouting.get(HttpMethod.GET.toString()).getItems().get(SECOND_IDX).getPath(), equalTo(".*\\.html"));
    assertThat(engineRouting.get(HttpMethod.GET.toString()).getItems().get(SECOND_IDX).getAddress(), equalTo("C-engine"));
  }

  @Test
  public void whenConfigWithPostRouting_expectTwoRoutings() throws Exception {
    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().size(), equalTo(2));
    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getPath(),
        equalTo(".*/local/.*\\.html"));
    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getAddress(),
        equalTo("A-post-engine"));

    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().get(SECOND_IDX).getPath(), equalTo(".*\\.html"));
    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().get(SECOND_IDX).getAddress(),
        equalTo("B-engine"));
  }

  @Test
  public void whenConfigWithGetRoutingAndTransitions_expectTransitionsExistsWhenDefined()
      throws Exception {
    assertThat(engineRouting.get(HttpMethod.GET.toString()).getItems().get(FIRST_IDX).getOnTransition().isEmpty(),
        equalTo(false));
    assertThat(engineRouting.get(HttpMethod.GET.toString()).getItems().get(FIRST_IDX).getOnTransition().keySet().size(),
        equalTo(1));
    assertThat(engineRouting.get(HttpMethod.GET.toString()).getItems().get(FIRST_IDX).getOnTransition().keySet(),
        hasItem("go-c"));
    assertThat(
        engineRouting.get(HttpMethod.GET.toString()).getItems().get(FIRST_IDX).getOnTransition().get("go-c").getAddress(),
        equalTo("C-engine"));
    assertThat(
        engineRouting.get(HttpMethod.GET.toString()).getItems().get(FIRST_IDX).getOnTransition().get("go-c")
            .getOnTransition()
            .isEmpty(), equalTo(true));

    assertThat(engineRouting.get(HttpMethod.GET.toString()).getItems().get(SECOND_IDX).getOnTransition().isEmpty(),
        equalTo(true));
  }

  @Test
  public void whenConfigWithPostRoutingAndTransitions_expectTransitionsExistsWhenDefined()
      throws Exception {
    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().isEmpty(),
        equalTo(false));
    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().keySet().size(),
        equalTo(2));
    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().keySet(),
        hasItem("go-b"));
    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().keySet(),
        hasItem("go-c"));

    assertThat(
        engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().get("go-b")
            .getAddress(),
        equalTo("B-engine"));
    assertThat(
        engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().get("go-b")
            .getOnTransition()
            .isEmpty(), equalTo(false));
    assertThat(
        engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().get("go-b")
            .getOnTransition()
            .keySet().size(), equalTo(1));
    assertThat(
        engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().get("go-b")
            .getOnTransition()
            .keySet(), hasItem("go-c"));
    assertThat(
        engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().get("go-b")
            .getOnTransition()
            .get("go-c").getAddress(),
        equalTo("C-engine"));
    assertThat(
        engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().get("go-b")
            .getOnTransition()
            .get("go-c").getOnTransition().isEmpty(),
        equalTo(true));

    assertThat(
        engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().get("go-c")
            .getAddress(),
        equalTo("C-engine"));
    assertThat(
        engineRouting.get(HttpMethod.POST.toString()).getItems().get(FIRST_IDX).getOnTransition().get("go-c")
            .getOnTransition()
            .isEmpty(), equalTo(true));

    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().get(SECOND_IDX).getOnTransition().isEmpty(),
        equalTo(false));
    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().get(SECOND_IDX).getOnTransition().keySet().size(),
        equalTo(1));
    assertThat(engineRouting.get(HttpMethod.POST.toString()).getItems().get(SECOND_IDX).getOnTransition().keySet(),
        hasItem("go-c"));
    assertThat(
        engineRouting.get(HttpMethod.POST.toString()).getItems().get(SECOND_IDX).getOnTransition().get("go-c")
            .getAddress(),
        equalTo("C-engine"));
    assertThat(
        engineRouting.get(HttpMethod.POST.toString()).getItems().get(SECOND_IDX).getOnTransition().get("go-c")
            .getOnTransition()
            .isEmpty(), equalTo(true));
  }
}
