/*
 * Knot.x - Reactive microservice assembler - Fragment Assembler
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
package com.cognifide.knotx.assembler;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import rx.Observable;
import rx.functions.Action1;

@RunWith(VertxUnitRunner.class)
public class FragmentAssemblerTest {

  private final static String ADDRESS = "knotx.core.assembler";

  //Test Runner Rule of Verts
  private RunTestOnContext vertx = new RunTestOnContext();

  //Test Runner Rule of Knotx
  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  //Junit Rule, sets up logger, prepares verts, starts verticles according to the config (supplied in annotation of test method)
  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(vertx).around(knotx);

  @Test
  @KnotxConfiguration("test.extract.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithNoSnippets_expectInternalServerError(TestContext context) throws Exception {
    callAssemblerWithAssertions(context, null,
        knotContext -> context.assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR,
            knotContext.clientResponse().statusCode()
        ));
  }

  @Test
  @KnotxConfiguration("test.extract.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithEmptySnippet_expectNoContentStatus(TestContext context)
      throws Exception {
    callAssemblerWithAssertions(context, Collections.singletonList(" "),
        knotContext -> context.assertEquals(HttpResponseStatus.NO_CONTENT,
            knotContext.clientResponse().statusCode()));
  }

  @Test
  @KnotxConfiguration("test.simple.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithManySnippets_expectSimpleResult(TestContext context)
      throws Exception {
    List<String> fragments = Arrays
        .asList(FileReader.readText("fragment1.txt"), FileReader.readText("fragment2.txt"),
            FileReader.readText("fragment3.txt"));
    String expectedResult = FileReader.readText("expectedSimpleResult.html");
    callAssemblerWithAssertions(context, fragments,
        knotContext -> {
          context.assertEquals(HttpResponseStatus.OK, knotContext.clientResponse().statusCode());
          context.assertEquals(expectedResult, knotContext.clientResponse().body().toString());
        });
  }

  @Test
  @KnotxConfiguration("test.extract.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithManySnippets_expectExtractResult(TestContext context)
      throws Exception {
    List<String> fragments = Arrays
        .asList(FileReader.readText("fragment1.txt"), FileReader.readText("fragment2.txt"),
            FileReader.readText("fragment3.txt"));
    String expectedResult = FileReader.readText("expectedExtractResult.html");
    callAssemblerWithAssertions(context, fragments,
        knotContext -> {
          context.assertEquals(HttpResponseStatus.OK, knotContext.clientResponse().statusCode());
          context.assertEquals(expectedResult, knotContext.clientResponse().body().toString());
        });
  }

  @Test
  @KnotxConfiguration("test.clear.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithManySnippets_expectClearResult(TestContext context)
      throws Exception {
    List<String> fragments = Arrays
        .asList(FileReader.readText("fragment1.txt"), FileReader.readText("fragment2.txt"),
            FileReader.readText("fragment3.txt"));
    String expectedResult = FileReader.readText("expectedClearResult.html");
    callAssemblerWithAssertions(context, fragments,
        knotContext -> {
          context.assertEquals(HttpResponseStatus.OK, knotContext.clientResponse().statusCode());
          context.assertEquals(expectedResult, knotContext.clientResponse().body().toString());
        });
  }

  private void callAssemblerWithAssertions(TestContext context, List<String> fragments,
      Action1<KnotContext> testFunction) {
    Async async = context.async();

    vertx.vertx().eventBus().<KnotContext>send(ADDRESS, KnotContextFactory.create(fragments),
        ar -> {
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
