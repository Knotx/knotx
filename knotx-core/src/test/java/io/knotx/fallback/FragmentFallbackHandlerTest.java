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
package io.knotx.fallback;

import io.knotx.fragment.Fragment;
import io.knotx.fragment.HandlerLogEntry;
import io.knotx.fragment.HanlderStatus;
import io.knotx.options.FallbackMetadata;
import io.knotx.server.api.FragmentsContext;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FragmentFallbackHandlerTest {

  private static final String CUSTOM_FALLBACK_MESSAGE = "<p class='error'>custom fallback</p>";
  private static final String CUSTOM_FALLBACK_ID = "FALLBACK_1";

  @Test
  public void handle_whenCorrectFragment_expectNotChangedBody() {
    // given
    String actualBody = "ACTUAL CONTENT";
    Fragment fragment = new Fragment("ANY", new JsonObject(), actualBody);

    RoutingContext mockContext = Mockito.mock(RoutingContext.class);
    Mockito.when(mockContext.get(FragmentsContext.KEY)).thenReturn(new FragmentsContext()
        .setFragments(Collections.singletonList(fragment)));

    // when
    new FragmentFallbackHandler(new FragmentFallbackHandlerOptions()).handle(mockContext);

    // then
    FragmentsContext result = mockContext.get(FragmentsContext.KEY);
    Assert.assertEquals(1, result.getFragments().size());
    Assert.assertEquals(actualBody, result.getFragments().get(0).getBody());
  }

  @Test
  public void handle_whenFailedFragmentWithoutFallbackIdentifier_expectEmptyBody() {
    // given
    String actualBody = "ACTUAL CONTENT";
    Fragment fragment = new Fragment("ANY", new JsonObject(), actualBody);
    fragment.appendLog(new HandlerLogEntry("any-one").setStatus(HanlderStatus.SUCCESS));
    fragment.appendLog(new HandlerLogEntry("any-two").setStatus(HanlderStatus.FAILURE));
    fragment.appendLog(new HandlerLogEntry("any-three").setStatus(HanlderStatus.SUCCESS));

    RoutingContext mockContext = Mockito.mock(RoutingContext.class);
    Mockito.when(mockContext.get(FragmentsContext.KEY)).thenReturn(new FragmentsContext()
        .setFragments(Collections.singletonList(fragment)));

    // when
    new FragmentFallbackHandler(new FragmentFallbackHandlerOptions()).handle(mockContext);

    // then
    FragmentsContext result = mockContext.get(FragmentsContext.KEY);
    Assert.assertEquals(1, result.getFragments().size());
    Assert.assertEquals(FallbackConstants.EMPTY_FALLBACK_VALUE,
        result.getFragments().get(0).getBody());
  }

  @Test
  public void handle_whenFailedFragmentWithFallbackIdentifierFromHandlerConfiguration_expectCustomBody() {
    // given
    String actualBody = "ACTUAL CONTENT";
    Fragment fragment = new Fragment("ANY", new JsonObject().put(
        FallbackConstants.FALLBACK_IDENTIFIER, CUSTOM_FALLBACK_ID),
        actualBody);
    fragment.appendLog(new HandlerLogEntry("any-one").setStatus(HanlderStatus.FAILURE));

    FragmentFallbackHandlerOptions options = new FragmentFallbackHandlerOptions();
    options.addFallback(new FallbackMetadata(CUSTOM_FALLBACK_ID, CUSTOM_FALLBACK_MESSAGE));

    RoutingContext mockContext = Mockito.mock(RoutingContext.class);
    Mockito.when(mockContext.get(FragmentsContext.KEY)).thenReturn(new FragmentsContext()
        .setFragments(Collections.singletonList(fragment)));

    // when
    new FragmentFallbackHandler(options).handle(mockContext);

    // then
    FragmentsContext result = mockContext.get(FragmentsContext.KEY);
    Assert.assertEquals(1, result.getFragments().size());
    Assert.assertEquals(CUSTOM_FALLBACK_MESSAGE, result.getFragments().get(0).getBody());
  }

  @Test
  public void handle_whenFailedFragmentWithFallbackIdentifierWhenFallbackDefinedAsFragment_expectCustomBody() {
    // given
    String actualBody = "ACTUAL CONTENT";
    Fragment testedFragment = new Fragment("ANY", new JsonObject().put(
        FallbackConstants.FALLBACK_IDENTIFIER, CUSTOM_FALLBACK_ID),
        actualBody);
    testedFragment.appendLog(new HandlerLogEntry("any-two").setStatus(HanlderStatus.FAILURE));

    Fragment fallbackFragment = new Fragment(FallbackConstants.FALLBACK_TYPE,
        new JsonObject()
            .put(FallbackConstants.FALLBACK_IDENTIFIER, CUSTOM_FALLBACK_ID),
        CUSTOM_FALLBACK_MESSAGE);
    RoutingContext mockContext = Mockito.mock(RoutingContext.class);
    Mockito.when(mockContext.get(FragmentsContext.KEY)).thenReturn(new FragmentsContext()
        .setFragments(Arrays.asList(testedFragment, fallbackFragment)));

    // when
    new FragmentFallbackHandler(new FragmentFallbackHandlerOptions()).handle(mockContext);

    // then
    FragmentsContext result = mockContext.get(FragmentsContext.KEY);
    Assert.assertEquals(1, result.getFragments().size());
    Assert.assertEquals(CUSTOM_FALLBACK_MESSAGE, result.getFragments().get(0).getBody());
  }

}
