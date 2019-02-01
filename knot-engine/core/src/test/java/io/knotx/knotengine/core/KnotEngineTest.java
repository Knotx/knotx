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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import io.knotx.fragment.Fragment;
import io.knotx.knotengine.api.SnippetFragment;
import io.knotx.knotengine.api.SnippetFragmentsContext;
import io.knotx.knotengine.core.junit.MockKnotProxy;
import io.knotx.server.api.context.ClientRequest;
import io.knotx.server.api.context.ClientResponse;
import io.knotx.server.api.context.FragmentsContext;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@ExtendWith(VertxExtension.class)
public class KnotEngineTest {

  @Mock
  private RoutingContext routingContext;

  private Map<String, FragmentsContext> routingContextMemory;

  @BeforeEach
  void setUp() {
    // error https://github.com/mockito/mockito/issues/873
    MockitoAnnotations.initMocks(this);
    routingContextMemory = new HashMap<>();
    routingContextMemory.put(FragmentsContext.KEY,
        new FragmentsContext().setClientRequest(new ClientRequest())
            .setClientResponse(new ClientResponse()));
    Mockito.when(routingContext.get(eq(FragmentsContext.KEY)))
        .thenAnswer(invocation -> routingContextMemory.get(FragmentsContext.KEY));
    Mockito.when(routingContext.put(eq(FragmentsContext.KEY), any(FragmentsContext.class)))
        .then(invocation -> {
          FragmentsContext argument = invocation.getArgument(1);
          routingContextMemory.put(FragmentsContext.KEY, argument);
          return routingContext;
        });
  }

  @Test
  public void handle_whenKnot_expectOkFragment(VertxTestContext testContext, Vertx vertx)
      throws Throwable {
    // given
    createSuccessKnot(vertx, "knot-a", "+A", null);
    fragmentsContext().setFragments(Collections.singletonList(new Fragment("snippet",
        new JsonObject().put("knots", "knot-a"), "")));

    KnotEngine tested = new KnotEngine(vertx, new DeliveryOptions());

    // when
    tested.handleRoute(routingContext, new RoutingEntry().setAddress("knot-a"),
        onComplete(
            context -> validateAssertions(testContext, context, ctx -> {
              // then
              assertEquals(1, ctx.getFragments().size());
              assertFalse(ctx.getFragments().get(0).failed());
            }), error -> fail("Should not fail")));

    assertTrue(testContext.awaitCompletion(30, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  public void handle_whenKnotAddsBody_expectFragmentBody(VertxTestContext testContext, Vertx vertx)
      throws Throwable {
    // given
    createSuccessKnot(vertx, "knot-a", "+A", null);
    fragmentsContext().setFragments(Collections.singletonList(new Fragment("snippet",
        new JsonObject().put("knots", "knot-a"), "")));

    KnotEngine tested = new KnotEngine(vertx, new DeliveryOptions());

    // when
    tested.handleRoute(routingContext, new RoutingEntry().setAddress("knot-a"),
        onComplete(snippetFragmentsContext -> {
          // then
          try {
            assertEquals("+A", snippetFragmentsContext.getFragments().get(0).content());
            testContext.completeNow();
          } catch (Exception e) {
            testContext.failNow(e);
          }
        }, error -> fail("Should not fail")));

    assertTrue(testContext.awaitCompletion(30, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  public void handle_whenKnotFails_expectFailedFragment(VertxTestContext testContext, Vertx vertx)
      throws Throwable {
    // given
    createFailingFragmentKnot(vertx, "knot-a");
    fragmentsContext().setFragments(Collections.singletonList(new Fragment("snippet",
        new JsonObject().put("knots", "knot-a"), "")));

    KnotEngine tested = new KnotEngine(vertx, new DeliveryOptions());

    // when
    tested.handleRoute(routingContext, new RoutingEntry().setAddress("knot-a"),
        onComplete(snippetFragmentsContext -> {
          // then
          try {
            SnippetFragment fragment = snippetFragmentsContext.getFragments().get(0);
            assertEquals(1, fragment.getDelegate().getAuditLog().size());
            assertTrue(fragment.processed("knot-a"));
            assertTrue(fragment.failed());
            testContext.completeNow();
          } catch (Exception e) {
            testContext.failNow(e);
          }
        }, error -> fail("Should not fail")));

    assertTrue(testContext.awaitCompletion(30, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  public void handle_whenKnotAddsBodyAThenKnotAddsBodyB_expectFragmentWithBodyABodyB(
      VertxTestContext testContext, Vertx vertx)
      throws Throwable {
    // given
    createSuccessKnot(vertx, "knot-a", "+A", "next");
    createSuccessKnot(vertx, "knot-b", "+B", null);
    fragmentsContext().setFragments(Collections.singletonList(new Fragment("snippet",
        new JsonObject().put("knots", "knot-a,knot-b"), "")));

    KnotEngine tested = new KnotEngine(vertx, new DeliveryOptions());

    // when
    tested.handleRoute(routingContext, new RoutingEntry().setAddress("knot-a").setOnTransition(
        Collections.singletonMap("next", new RoutingEntry().setAddress("knot-b"))),
        onComplete(snippetFragmentsContext -> {
          // then
          try {
            assertEquals("+A+B", snippetFragmentsContext.getFragments().get(0).content());
            testContext.completeNow();
          } catch (Exception e) {
            testContext.failNow(e);
          }
        }, error -> fail("Should not fail")));

    assertTrue(testContext.awaitCompletion(30, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  public void handle_whenKnotAddsBodyAThenKnotFails_expectFragmentBodyAAndFailedFragment(
      VertxTestContext testContext, Vertx vertx)
      throws Throwable {
    // given
    createSuccessKnot(vertx, "knot-a", "+A", "next");
    createFailingFragmentKnot(vertx, "knot-b");
    fragmentsContext().setFragments(Collections.singletonList(new Fragment("snippet",
        new JsonObject().put("knots", "knot-a,knot-b"), "")));

    KnotEngine tested = new KnotEngine(vertx, new DeliveryOptions());

    // when
    tested.handleRoute(routingContext, new RoutingEntry().setAddress("knot-a").setOnTransition(
        Collections.singletonMap("next", new RoutingEntry().setAddress("knot-b"))),
        onComplete(snippetFragmentsContext -> {
          // then
          try {
            SnippetFragment fragment = snippetFragmentsContext.getFragments().get(0);
            assertEquals("+A", fragment.content());
            assertTrue(fragment.processed("knot-a"));
            assertTrue(fragment.processed("knot-b"));
            assertTrue(fragment.failed());
            testContext.completeNow();
          } catch (Exception e) {
            testContext.failNow(e);
          }
        }, error -> fail("Should not fail")));

    assertTrue(testContext.awaitCompletion(30, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  public void handle_whenKnotFails_expectNextKnotIsNotCalled(VertxTestContext testContext,
      Vertx vertx)
      throws Throwable {
    // given
    createFailingFragmentKnot(vertx, "knot-a");
    createSuccessKnot(vertx, "knot-b", "+B", null);
    fragmentsContext().setFragments(Collections.singletonList(new Fragment("snippet",
        new JsonObject().put("knots", "knot-a,knot-b"), "")));

    KnotEngine tested = new KnotEngine(vertx, new DeliveryOptions());

    // when
    tested.handleRoute(routingContext, new RoutingEntry().setAddress("knot-a").setOnTransition(
        Collections.singletonMap("next", new RoutingEntry().setAddress("knot-b"))),
        onComplete(snippetFragmentsContext -> {
          // then
          try {
            SnippetFragment fragment = snippetFragmentsContext.getFragments().get(0);
            assertTrue(fragment.processed("knot-a"));
            assertFalse(fragment.processed("knot-b"));
            assertTrue(fragment.failed());
            testContext.completeNow();
          } catch (Exception e) {
            testContext.failNow(e);
          }
        }, error -> fail("Should not fail")));

    assertTrue(testContext.awaitCompletion(30, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  public void handle_whenKnotResponseWithUnknownTransition_expectErrorResponseStatusCode(
      VertxTestContext testContext, Vertx vertx)
      throws Throwable {
    // given
    createSuccessKnot(vertx, "knot-a", "+A", "any");
    createSuccessKnot(vertx, "knot-b", "+B", null);
    fragmentsContext().setFragments(Collections.singletonList(new Fragment("snippet",
        new JsonObject().put("knots", "knot-a"), "")));

    KnotEngine tested = new KnotEngine(vertx, new DeliveryOptions());

    // when
    tested.handleRoute(routingContext, new RoutingEntry().setAddress("knot-a").setOnTransition(
        Collections.singletonMap("next", new RoutingEntry().setAddress("knot-b"))),
        onComplete(snippetFragmentsContext -> {
          // then
          try {
            SnippetFragment fragment = snippetFragmentsContext.getFragments().get(0);
            assertTrue(fragment.processed("knot-a"));
            assertFalse(fragment.processed("knot-b"));
            assertFalse(fragment.failed());
            testContext.completeNow();
          } catch (Exception e) {
            testContext.failNow(e);
          }
        }, error -> fail("Should not fail")));

    assertTrue(testContext.awaitCompletion(30, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  public void handle_whenNoKnotFound_expectError(
      VertxTestContext testContext, Vertx vertx)
      throws Throwable {
    // given
    fragmentsContext().setFragments(Collections.singletonList(new Fragment("snippet",
        new JsonObject().put("knots", "knot-a"), "")));

    KnotEngine tested = new KnotEngine(vertx, new DeliveryOptions());

    // when
    tested.handleRoute(routingContext, new RoutingEntry().setAddress("knot-a"),
        onComplete(snippetFragmentsContext -> fail("Should fail."),
            error -> testContext.completeNow()));

    assertTrue(testContext.awaitCompletion(30, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  private void validateAssertions(VertxTestContext testContext, SnippetFragmentsContext context,
      Consumer<SnippetFragmentsContext> assertions) {
    try {
      assertions.accept(context);
      testContext.completeNow();
    } catch (Exception e) {
      testContext.failNow(e);
    }
  }

  private FragmentsContext fragmentsContext() {
    return routingContextMemory.get(FragmentsContext.KEY);
  }

  private void createSuccessKnot(Vertx vertx, final String address, final String addToBody,
      final String transition) {
    MockKnotProxy.register(vertx.getDelegate(), address, knotContext -> {
      SnippetFragment fragment = knotContext.getFragments().get(0);
      fragment.content(fragment.content() + addToBody);
      fragment.addSuccessLog(address);
      knotContext.setTransition(transition);
    });
  }

  private void createFailingFragmentKnot(Vertx vertx, final String address) {
    MockKnotProxy
        .register(vertx.getDelegate(), address, knotContext -> knotContext.getFragments().forEach(
            fragment -> fragment.addFailedLog(address, new IllegalStateException("Failing knot"))));
  }

  private void createSimpleFailingKnot(Vertx vertx, final String address, final int statusCode,
      final MultiMap headers) {
    MockKnotProxy.register(vertx.getDelegate(), address, knotContext -> {
      knotContext.getClientResponse().setStatusCode(statusCode).setHeaders(headers);
      knotContext.setTransition(null);
    });
  }

  private SingleObserver<SnippetFragmentsContext> onComplete(
      Consumer<SnippetFragmentsContext> onSuccess, Consumer<Throwable> onError) {
    return new SingleObserver<SnippetFragmentsContext>() {

      @Override
      public void onSubscribe(Disposable d) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void onSuccess(SnippetFragmentsContext snippetFragmentsContext) {
        try {
          onSuccess.accept(snippetFragmentsContext);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public void onError(Throwable ex) {
        try {
          onError.accept(ex);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

}
