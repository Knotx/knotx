/*
 * Knot.x - Reactive microservice assembler - Action Knot Verticle
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
package com.cognifide.knotx.knot.action;

import com.google.common.collect.Lists;

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.junit.FileReader;
import com.cognifide.knotx.junit.KnotxConfiguration;
import com.cognifide.knotx.junit.Logback;
import com.cognifide.knotx.junit.TestVertxDeployer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Objects;

import io.netty.handler.codec.http.HttpResponseStatus;
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
public class ActionKnotVerticleTest {

  private final static String ADDRESS = "knotx.knot.action";

  private final static String HIDDEN_INPUT_TAG_NAME = "snippet-identifier";

  private final static String FRAGMENT_REDIRECT_IDENTIFIER = "redirect";

  private final static String FRAGMENT_SELF_IDENTIFIER = "self";

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
  @KnotxConfiguration("knotx-knot-action-test.json")
  public void callGetWithNoActionFragments_expectResponseOkNoFragmentChanges(TestContext context) throws Exception {
    String expectedTempatingFragment = FileReader.readText("fragment_templating_out.txt");
    KnotContext knotContext = crerateKnotContext("templating", "fragment_templating_in.txt");

    callActionKnotWithAssertions(context, knotContext,
        clientResponse -> {
          context.assertEquals(HttpResponseStatus.OK, knotContext.clientResponse().statusCode());
          List<Fragment> fragments = knotContext.fragments().get();
          context.assertEquals(FIRST_FRAGMENT.getContent(), fragments.get(0).getContent());
          context.assertEquals(expectedTempatingFragment, fragments.get(1).getContent());
          context.assertEquals(LAST_FRAGMENT.getContent(), fragments.get(2).getContent());

        },
        error -> context.fail(error.getMessage()));
  }

  @Test
  @KnotxConfiguration("knotx-knot-action-test.json")
  public void callGetWithTwoActionFragments_expectResponseOkTwoFragmentChanges(TestContext context) throws Exception {
    String expectedRedirectFormFragment = clean(FileReader.readText("fragment_form_redirect_out.txt"));
    String expectedSelfFormFragment = clean(FileReader.readText("fragment_form_self_out.txt"));
    KnotContext knotContext = crerateKnotContext("form-identifier", "fragment_form_redirect_in.txt", "fragment_form_self_in.txt");

    callActionKnotWithAssertions(context, knotContext,
        clientResponse -> {
          context.assertEquals(HttpResponseStatus.OK, knotContext.clientResponse().statusCode());
          List<Fragment> fragments = knotContext.fragments().get();
          context.assertEquals(FIRST_FRAGMENT.getContent(), fragments.get(0).getContent());
          context.assertEquals(expectedRedirectFormFragment, clean(fragments.get(1).getContent()));
          context.assertEquals(expectedSelfFormFragment, clean(fragments.get(2).getContent()));
          context.assertEquals(LAST_FRAGMENT.getContent(), fragments.get(3).getContent());
        },
        error -> context.fail(error.getMessage()));
  }

  @Test
  @KnotxConfiguration("knotx-knot-action-test.json")
  public void callPostWithTwoActionFragments_expectResponseOkWithServiceContextNoTransition(TestContext context) throws Exception {
    String expectedFirstFormFragment = clean(FileReader.readText("fragment_form_redirect_out.txt"));
    String expectedSecondFormFragment = clean(FileReader.readText("fragment_form_self_out.txt"));
    KnotContext knotContext = crerateKnotContext("form-identifier", "fragment_form_redirect_in.txt", "fragment_form_self_in.txt");

    knotContext.clientRequest()
        .setMethod(HttpMethod.POST)
        .setFormAttributes(MultiMap.caseInsensitiveMultiMap().add(HIDDEN_INPUT_TAG_NAME, FRAGMENT_SELF_IDENTIFIER));

    callActionKnotWithAssertions(context, knotContext,
        clientResponse -> {
          context.assertEquals(HttpResponseStatus.OK, knotContext.clientResponse().statusCode());
          context.assertTrue(knotContext.transition().isPresent());
          Fragment selfFragment = knotContext.fragments().get().stream().filter(item -> FRAGMENT_SELF_IDENTIFIER.equals(item.getId())).findFirst().get();
          context.assertTrue(Objects.nonNull(selfFragment.getContext().getJsonObject("_response")));

          List<Fragment> fragments = knotContext.fragments().get();
          context.assertEquals(FIRST_FRAGMENT.getContent(), fragments.get(0).getContent());
          context.assertEquals(expectedFirstFormFragment, clean(fragments.get(1).getContent()));
          context.assertEquals(expectedSecondFormFragment, clean(fragments.get(2).getContent()));
          context.assertEquals(LAST_FRAGMENT.getContent(), fragments.get(3).getContent());
        },
        error -> context.fail(error.getMessage()));
  }

  @Test
  @KnotxConfiguration("knotx-knot-action-test.json")
  public void callPostWithTwoActionFragments_expectResponseOkWithTransitionStep2(TestContext context) throws Exception {
    KnotContext knotContext = crerateKnotContext("form-identifier", "fragment_form_redirect_in.txt", "fragment_form_self_in.txt");
    knotContext.clientRequest()
        .setMethod(HttpMethod.POST)
        .setFormAttributes(MultiMap.caseInsensitiveMultiMap().add(HIDDEN_INPUT_TAG_NAME, FRAGMENT_REDIRECT_IDENTIFIER));

    callActionKnotWithAssertions(context, knotContext,
        clientResponse -> {
          context.assertEquals(HttpResponseStatus.MOVED_PERMANENTLY, knotContext.clientResponse().statusCode());
          context.assertEquals("/content/form/step2.html", knotContext.clientResponse().headers().get("Location"));
          context.assertFalse(knotContext.transition().isPresent());
          context.assertFalse(knotContext.fragments().isPresent());
        },
        error -> context.fail(error.getMessage()));
  }

  private void callActionKnotWithAssertions(TestContext context, KnotContext knotContext, Action1<KnotContext> onSuccess, Action1<Throwable> onError) {
    Async async = context.async();

    vertx.vertx().eventBus().<JsonObject>send(ADDRESS, knotContext.toJson(), ar -> {
      if (ar.succeeded()) {
        Observable
            .just(new KnotContext(ar.result().body()))
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

  private KnotContext crerateKnotContext(String id, String... fragmentFilename) throws Exception {
    List<Fragment> fragments = Lists.newArrayList(FIRST_FRAGMENT);
    for (String file : fragmentFilename) {
      fragments.add(Fragment.snippet(id, FileReader.readText(file)));
    }
    fragments.add(LAST_FRAGMENT);

    return KnotContext.empty(fragments);
  }

  private String clean(String text) {
    // TODO correct html formatting
    return Jsoup.parse(text).outputSettings(new Document.OutputSettings().prettyPrint(true)).html();
  }

}
