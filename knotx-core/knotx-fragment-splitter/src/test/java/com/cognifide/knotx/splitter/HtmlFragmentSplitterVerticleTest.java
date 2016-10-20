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

import com.cognifide.knotx.junit.FileReader;
import com.cognifide.knotx.junit.KnotxConfiguration;
import com.cognifide.knotx.junit.Logback;
import com.cognifide.knotx.junit.TestVertxDeployer;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import rx.Observable;
import rx.functions.Action1;

@RunWith(VertxUnitRunner.class)
public class HtmlFragmentSplitterVerticleTest {

  private final static String ADDRESS = "knotx.core.splitter";

  //Test Runner Rule of Verts
  private RunTestOnContext vertx = new RunTestOnContext();

  //Test Runner Rule of Knotx
  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  //Junit Rule, sets up logger, prepares verts, starts verticles according to the config (supplied in annotation of test method)
  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(vertx).around(knotx);

  @Test
  @KnotxConfiguration("knotx-fragment-splitter-test.json")
  public void callNonExistingService_expectBadRequestResponse(TestContext context) throws Exception {
    callFragmentSplitterWithAssertions(context, FileReader.readText("test-many-fragments.html"),
        pair -> context.assertEquals(pair.size(), 7));
  }

  private void callFragmentSplitterWithAssertions(TestContext context, String template, Action1<JsonArray> testFunction) {
    Async async = context.async();

    vertx.vertx().eventBus().<JsonArray>send(ADDRESS, template, ar -> {
      if (ar.succeeded()) {
        Observable
            .just(ar.result().body())
            .map(jsonArray -> Pair.of(async, jsonArray))
            .subscribe(pair -> testFunction.call(pair.getRight()),
                error -> context.fail(error.getMessage()),
                async::complete);
      } else {
        context.fail(ar.cause());
      }
    });
  }

}
