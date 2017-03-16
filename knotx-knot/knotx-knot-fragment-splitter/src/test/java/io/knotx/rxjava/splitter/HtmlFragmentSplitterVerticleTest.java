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
package io.knotx.rxjava.splitter;

import io.knotx.dataobjects.KnotContext;
import io.knotx.junit.rule.KnotxConfiguration;
import io.knotx.junit.rule.Logback;
import io.knotx.junit.rule.TestVertxDeployer;
import io.knotx.junit.util.FileReader;
import io.knotx.junit.util.KnotContextFactory;
import io.knotx.rxjava.proxy.KnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
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
  @KnotxConfiguration("knotx-fragment-splitter-test.json")
  public void callSplitterWithEmptyBody_expectNotFoundResponse(TestContext context)
      throws Exception {
    callFragmentSplitterWithAssertions(context, "",
        knotContext -> {
          context.assertEquals(knotContext.getClientResponse().getStatusCode(),
              HttpResponseStatus.NOT_FOUND.code());
          context.assertFalse(knotContext.getFragments() != null);
        });
  }

  @Test
  @KnotxConfiguration("knotx-fragment-splitter-test.json")
  public void callSplitterWithManySnippets_expectMultipleFragments(TestContext context)
      throws Exception {
    callFragmentSplitterWithAssertions(context, FileReader.readText("test-many-fragments.html"),
        knotContext -> {
          context.assertTrue(knotContext.getFragments() != null);
          context.assertEquals(knotContext.getFragments().size(), NUMBER_OF_FRAGMENTS);
        });
  }

  private void callFragmentSplitterWithAssertions(TestContext context, String template,
      Action1<KnotContext> testFunction) {
    Async async = context.async();
    KnotProxy service = KnotProxy.createProxy(new Vertx(vertx.vertx()), ADDRESS);

    service.rxProcess(KnotContextFactory.empty(template))
        .map(ctx -> Pair.of(async, ctx))
        .subscribe(
            success -> {
              testFunction.call(success.getRight());
              async.complete();
            },
            error -> context.fail(error)
        );
  }

}
