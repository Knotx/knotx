/*
 * Knot.x - Reactive microservice assembler - Auhtorization Knot Verticle
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
package com.cognifide.knotx.knot.authorization;

import com.google.common.collect.Lists;

import com.cognifide.knotx.dataobjects.AdapterResponse;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.junit.KnotContextFactory;
import com.cognifide.knotx.junit.Logback;

import org.jsoup.Jsoup;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.MultiMap;
import rx.Observable;
import rx.functions.Action1;

@RunWith(VertxUnitRunner.class)
public class AuthKnotVerticleTest {

  private final static String ADDRESS = "knotx.knot.auth";
  private static final String FRAGMENT_IDENTIFIER = "data-api-type";
  private final static Fragment FIRST_FRAGMENT = Fragment.raw("<html><head></head><body>");
  private final static Fragment LAST_FRAGMENT = Fragment.raw("</body></html>");

  //Test Runner Rule of Verts
  private RunTestOnContext vertx = new RunTestOnContext();

  //Test Runner Rule of Knotx
  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  //Junit Rule, sets up logger, prepares verts, starts verticles according to the config (supplied in annotation of test method)
  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(vertx).around(knotx);

  @Test
  public void callGetWithoutAuthFragment_expectPassThroughWithTheSameAsInput(TestContext context) throws Exception {
    String expectedTemplatingFragment = FileReader.readText("template-templating.txt");
    KnotContext knotContext = createKnotContext(FIRST_FRAGMENT, LAST_FRAGMENT, "template-templating.txt");
    knotContext.clientRequest().setMethod(HttpMethod.GET);

    callActionKnotWithAssertions(context, knotContext,
        clientResponse -> {
          context.assertEquals(HttpResponseStatus.OK, clientResponse.clientResponse().statusCode());
          context.assertTrue(clientResponse.transition().isPresent());
          context.assertEquals("next", clientResponse.transition().get());
          context.assertTrue(clientResponse.fragments().isPresent());

          List<Fragment> fragments = clientResponse.fragments().get();
          context.assertEquals(FIRST_FRAGMENT.getContent(), fragments.get(0).getContent());
          context.assertEquals(expectedTemplatingFragment, fragments.get(1).getContent());
          context.assertEquals(LAST_FRAGMENT.getContent(), fragments.get(2).getContent());
        },
        error -> context.fail(error.getMessage()));
  }

  @Test
  public void callGetWithAuthAndUnuathorizedResponse_expectRedirect(TestContext context) throws Exception {
    createKnotConsumer("auth-handler", HttpResponseStatus.UNAUTHORIZED);
    KnotContext knotContext = createKnotContext(FIRST_FRAGMENT, LAST_FRAGMENT, "template-auth.txt", "template-templating.txt");
    knotContext.clientRequest().setMethod(HttpMethod.GET);

    callActionKnotWithAssertions(context, knotContext,
        ctx -> {
          context.assertEquals(HttpResponseStatus.TEMPORARY_REDIRECT, ctx.clientResponse().statusCode());
          context.assertEquals("/content/local/login/step1.html", ctx.clientResponse().headers().get("Location"));
          context.assertFalse(ctx.fragments().isPresent());
          context.assertFalse(ctx.transition().isPresent());
        },
        error -> context.fail(error.getMessage()));
  }

  @Test
  public void callGetWithAuthAndAuthorizedResponse_expectPassThrough(TestContext context) throws Exception {
    createKnotConsumer("auth-handler", HttpResponseStatus.OK);
    KnotContext knotContext = createKnotContext(FIRST_FRAGMENT, LAST_FRAGMENT, "template-auth.txt", "template-templating.txt");
    knotContext.clientRequest().setMethod(HttpMethod.GET);

    callActionKnotWithAssertions(context, knotContext,
        ctx -> {
          context.assertEquals(HttpResponseStatus.OK, ctx.clientResponse().statusCode());
          context.assertTrue(ctx.fragments().isPresent());
          context.assertEquals(ctx.fragments().get().size(), 3);
          context.assertEquals(ctx.fragments().get().get(0).getId(), "_raw");
          context.assertEquals(ctx.fragments().get().get(1).getId(), "templating");
          context.assertEquals(ctx.fragments().get().get(2).getId(), "_raw");

          context.assertTrue(ctx.transition().isPresent());
          context.assertEquals(ctx.transition().get(), "next");
        },
        error -> context.fail(error.getMessage()));
  }


  private void callActionKnotWithAssertions(TestContext context, KnotContext knotContext, Action1<KnotContext> onSuccess, Action1<Throwable> onError) {
    Async async = context.async();

    vertx.vertx().eventBus().<KnotContext>send(ADDRESS, knotContext, ar -> {
      if (ar.succeeded()) {
        Observable
            .just(ar.result().body())
            .subscribe(
                onSuccess,
                onError,
                async::complete
            );
      } else {
        context.fail(ar.cause());
      }
    });
  }

  private KnotContext createKnotContext(Fragment firstFragment, Fragment lastFragment, String... snippetFilenames) throws Exception {
    List<Fragment> fragments = Lists.newArrayList();
    Optional.ofNullable(firstFragment).ifPresent(fragments::add);
    for (String file : snippetFilenames) {
      String fileContent = FileReader.readText(file);
      String fragmentIdentifier = Jsoup.parse(fileContent).getElementsByAttribute(FRAGMENT_IDENTIFIER).attr(FRAGMENT_IDENTIFIER);
      fragments.add(Fragment.snippet(fragmentIdentifier, fileContent));
    }
    Optional.ofNullable(lastFragment).ifPresent(fragments::add);

    return KnotContextFactory.empty(fragments);
  }

  private void createKnotConsumer(String address, HttpResponseStatus statusCode) {
    EventBus eventBus = vertx.vertx().eventBus();
    eventBus.<JsonObject>consumer(address, msg -> {
      ClientResponse serviceResponse = new ClientResponse();
      serviceResponse.setStatusCode(statusCode);
      serviceResponse.setHeaders(MultiMap.caseInsensitiveMultiMap());
      final AdapterResponse adapterResponse = new AdapterResponse();
      adapterResponse.setResponse(serviceResponse);
      msg.reply(adapterResponse);
    });
  }

}
