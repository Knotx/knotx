/*
 * Knot.x - Reactive microservice assembler - HTML Fragment Splitter
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
package com.cognifide.knotx.splitter;

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.junit.KnotContextFactory;
import com.cognifide.knotx.junit.Logback;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import rx.Observable;
import rx.functions.Action1;

@RunWith(VertxUnitRunner.class)
public class HtmlFragmentSplitterVerticleTest {

  private final static String ADDRESS = "knotx.core.splitter";

  private final static Integer NUMBER_OF_FRAGMENTS = 8;

  //Test Runner Rule of Verts
  private RunTestOnContext vertx = new RunTestOnContext();

  //Test Runner Rule of Knotx
  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  //Junit Rule, sets up logger, prepares verts, starts verticles according to the config (supplied in annotation of test method)
  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(vertx).around(knotx);

  @Test
  public void callSplitterWithManySnippets_expectNoFragments(TestContext context) throws Exception {
    callFragmentSplitterWithAssertions(context, "",
        knotContext -> {
          context.assertEquals(knotContext.clientResponse().statusCode(), HttpResponseStatus.NOT_FOUND);
          context.assertFalse(knotContext.fragments().isPresent());
        });
  }

  @Test
  public void callSplitterEmptyBody_expectErrorReponse(TestContext context) throws Exception {
    callFragmentSplitterWithAssertions(context, FileReader.readText("test-many-fragments.html"),
        knotContext -> {
          context.assertTrue(knotContext.fragments().isPresent());
          context.assertEquals(knotContext.fragments().get().size(), NUMBER_OF_FRAGMENTS);
        });
  }

  private void callFragmentSplitterWithAssertions(TestContext context, String template, Action1<KnotContext> testFunction) {
    Async async = context.async();

    vertx.vertx().eventBus().<KnotContext>send(ADDRESS, KnotContextFactory.empty(template), ar -> {
      if (ar.succeeded()) {
        Observable
            .just(ar.result().body())
            .map(knot -> Pair.of(async, knot))
            .subscribe(pair -> testFunction.call(pair.getRight()),
                error -> context.fail(error.getMessage()),
                async::complete);
      } else {
        context.fail(ar.cause());
      }
    });
  }

}
