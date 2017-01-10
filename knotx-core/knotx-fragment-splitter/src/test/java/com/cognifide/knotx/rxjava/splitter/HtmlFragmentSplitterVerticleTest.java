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
package com.cognifide.knotx.rxjava.splitter;

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.junit.KnotContextFactory;
import com.cognifide.knotx.junit.Logback;
import com.cognifide.knotx.launcher.junit.FileReader;
import com.cognifide.knotx.launcher.junit.KnotxConfiguration;
import com.cognifide.knotx.launcher.junit.TestVertxDeployer;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
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
  public void callSplitterWithEmptyBody_expectNotFoundResponse(TestContext context) throws Exception {
    callFragmentSplitterWithAssertions(context, "",
        knotContext -> {
          context.assertEquals(knotContext.getClientResponse().getStatusCode(), HttpResponseStatus.NOT_FOUND.code());
          context.assertFalse(knotContext.getFragments() != null);
        });
  }

  @Test
  @KnotxConfiguration("knotx-fragment-splitter-test.json")
  public void callSplitterWithManySnippets_expectMultipleFragments(TestContext context) throws Exception {
    callFragmentSplitterWithAssertions(context, FileReader.readText("test-many-fragments.html"),
        knotContext -> {
          context.assertTrue(knotContext.getFragments() != null);
          context.assertEquals(knotContext.getFragments().size(), NUMBER_OF_FRAGMENTS);
        });
  }

  private void callFragmentSplitterWithAssertions(TestContext context, String template, Action1<KnotContext> testFunction) {
    Async async = context.async();
    io.vertx.rxjava.core.Vertx vertx = new io.vertx.rxjava.core.Vertx(this.vertx.vertx());

    FragmentSplitterService service = FragmentSplitterService.createProxy(vertx, ADDRESS);

    service.processObservable(KnotContextFactory.empty(template))
        .map(ctx -> Pair.of(async, ctx))
        .subscribe(
            next -> testFunction.call(next.getRight()),
            error -> context.fail(error),
            () -> async.complete()
        );
  }

}
