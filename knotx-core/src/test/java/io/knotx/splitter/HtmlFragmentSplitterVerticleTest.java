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
package io.knotx.splitter;

import io.knotx.dataobjects.KnotContext;
import io.knotx.junit.rule.KnotxConfiguration;
import io.knotx.junit.rule.TestVertxDeployer;
import io.knotx.junit.util.FileReader;
import io.knotx.junit.util.KnotContextFactory;
import io.knotx.reactivex.proxy.KnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import java.util.function.Consumer;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class HtmlFragmentSplitterVerticleTest {

  private final static String ADDRESS = "knotx.core.splitter";

  //Test Runner Rule of Verts
  private RunTestOnContext vertx = new RunTestOnContext();

  //Test Runner Rule of Knotx
  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  //Junit Rule, sets up logger, prepares verts, starts verticles according to the config (supplied in annotation of test method)
  @Rule
  public RuleChain chain = RuleChain.outerRule(vertx).around(knotx);

  @Test
  @KnotxConfiguration("io/knotx/splitter/knotx-fragment-splitter-test.json")
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
  @KnotxConfiguration("io/knotx/splitter/knotx-fragment-splitter-test.json")
  public void callSplitterWithManySnippets_expectNineFragments(TestContext context)
      throws Exception {
    callFragmentSplitterWithAssertions(context, FileReader.readText(
        "io/knotx/splitter/test-many-fragments.html"),
        knotContext -> {
          context.assertTrue(knotContext.getFragments() != null);
          context.assertEquals(knotContext.getFragments().size(), 9);
        });
  }

  private void callFragmentSplitterWithAssertions(TestContext context, String template,
      Consumer<KnotContext> testFunction) {
    Async async = context.async();
    KnotProxy service = KnotProxy.createProxy(new Vertx(vertx.vertx()), ADDRESS);

    service.rxProcess(KnotContextFactory.empty(template))
        .map(ctx -> Pair.of(async, ctx))
        .doOnSuccess(success -> testFunction.accept(success.getRight()))
        .subscribe(
            success -> async.complete(),
            context::fail
        );
  }

}
