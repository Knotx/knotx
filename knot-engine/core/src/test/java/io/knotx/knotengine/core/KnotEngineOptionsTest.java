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
package io.knotx.knotengine.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.knotx.junit5.util.FileReader;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class KnotEngineOptionsTest {

  @Test
  public void whenRouteIsEmpty_expectNoAddress()
      throws IOException {
    // given
    JsonObject json = new JsonObject(FileReader.readText("routes-empty.json"));

    // when
    RoutingEntry aRoute = new RoutingEntry(json);

    // then
    assertNull(aRoute.getAddress());
  }

  @Test
  public void whenRouteWithAddress_expectAddressAndNoTransitions()
      throws IOException {
    // given
    JsonObject json = new JsonObject(FileReader.readText("routes-simple.json"));

    // when
    RoutingEntry aRoute = new RoutingEntry(json);

    // then
    assertEquals("A-knot", aRoute.getAddress());
    assertTrue(aRoute.getOnTransition().isEmpty());
  }

  @Test
  public void whenRouteWithTransition_expectAddressAndTransition()
      throws IOException {
    // given
    JsonObject json = new JsonObject(FileReader.readText("routes-simple-with-transition.json"));

    // when
    RoutingEntry aRoute = new RoutingEntry(json);

    // then
    assertEquals("A-knot", aRoute.getAddress());
    assertFalse(aRoute.getOnTransition().isEmpty());
    assertEquals(1, aRoute.getOnTransition().size());
    assertTrue(aRoute.getOnTransition().containsKey("go-b"));
    assertEquals("B-knot", aRoute.getOnTransition().get("go-b").getAddress());
  }

  @Test
  public void whenRouteWithManyTransitions_expectManyTransitions()
      throws IOException {
    // given
    JsonObject json = new JsonObject(FileReader.readText("routes-simple-with-many-transitions.json"));

    // when
    RoutingEntry aRoute = new RoutingEntry(json);

    // then
    assertEquals("A-knot", aRoute.getAddress());
    assertFalse(aRoute.getOnTransition().isEmpty());
    assertEquals(3, aRoute.getOnTransition().size());
    assertEquals("B-knot", aRoute.getOnTransition().get("go-b").getAddress());
    assertEquals("D-knot", aRoute.getOnTransition().get("go-d").getAddress());
    assertEquals("E-knot", aRoute.getOnTransition().get("go-e").getAddress());
  }

  @Test
  public void whenConfigWithRoutesAndTransitions_expectTransitionsExistsWhenDefined()
      throws IOException {
    // given
    JsonObject json = new JsonObject(FileReader.readText("routes-complex.json"));

    // when
    RoutingEntry aRoute = new RoutingEntry(json);

    // then
    RoutingEntry bRoute = aRoute.getOnTransition().get("go-b");
    assertThat(bRoute.getOnTransition().isEmpty(), equalTo(false));
    assertThat(bRoute.getAddress(), equalTo("B-knot"));
    assertThat(bRoute.getOnTransition().keySet().size(), equalTo(1));
    assertThat(bRoute.getOnTransition().keySet(), hasItem("go-c"));

    RoutingEntry cRoute = bRoute.getOnTransition().get("go-c");
    assertThat(cRoute.getAddress(), equalTo("C-knot"));
    assertThat(cRoute.getOnTransition().isEmpty(), equalTo(true));
  }

}
