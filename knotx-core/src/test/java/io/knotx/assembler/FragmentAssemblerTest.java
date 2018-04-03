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
package io.knotx.assembler;

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
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FragmentAssemblerTest {

  private final static String ADDRESS = "knotx.core.assembler";
  private final static String RAW = "_raw";
  private static final String SERVICES = "services";
  private static final String HANDLEBARS = "handlebars";

  //Test Runner Rule of Verts
  private RunTestOnContext vertx = new RunTestOnContext();

  //Test Runner Rule of Knotx
  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  //Junit Rule, sets up logger, prepares verts, starts verticles according to the config (supplied in annotation of test method)
  @Rule
  public RuleChain chain = RuleChain.outerRule(vertx).around(knotx);

  @Test
  @KnotxConfiguration("io/knotx/assembler/test.unwrap.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithNoSnippets_expectInternalServerError(TestContext context)
      throws Exception {
    callAssemblerWithAssertions(context, null,
        knotContext -> context.assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
            knotContext.getClientResponse().getStatusCode()
        ));
  }

  @Test
  @KnotxConfiguration("io/knotx/assembler/test.unwrap.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithEmptySnippet_expectNoContentStatus(TestContext context)
      throws Exception {
    callAssemblerWithAssertions(context, Collections
            .singletonList(new ImmutablePair<>(Collections.singletonList(RAW), StringUtils.SPACE)),
        knotContext -> context.assertEquals(HttpResponseStatus.NO_CONTENT.code(),
            knotContext.getClientResponse().getStatusCode()));
  }

  @Test
  @KnotxConfiguration("io/knotx/assembler/test.asIs.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithManySnippets_expectAsIsResult(TestContext context)
      throws Exception {
    List<Pair<List<String>, String>> fragments = Arrays.asList(
        toPair("io/knotx/assembler/fragment1.txt", RAW),
        toPair("io/knotx/assembler/fragment2.txt", SERVICES, HANDLEBARS),
        toPair("io/knotx/assembler/fragment3.txt", RAW));
    String expectedResult = FileReader.readText("io/knotx/server/expectedAsIsResult.html");
    callAssemblerWithAssertions(context, fragments,
        knotContext -> {
          context.assertEquals(HttpResponseStatus.OK.code(),
              knotContext.getClientResponse().getStatusCode());
          context
              .assertEquals(expectedResult, knotContext.getClientResponse().getBody().toString());
        });
  }

  @Test
  @KnotxConfiguration("io/knotx/assembler/test.unwrap.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithManySnippets_expectUnwrapResult(TestContext context)
      throws Exception {
    List<Pair<List<String>, String>> fragments = Arrays.asList(
        toPair("io/knotx/assembler/fragment1.txt", RAW),
        toPair("io/knotx/assembler/fragment2.txt", SERVICES, HANDLEBARS),
        toPair("io/knotx/assembler/fragment3.txt", RAW));
    String expectedResult = FileReader.readText("io/knotx/server/expectedUnwrapResult.html");
    callAssemblerWithAssertions(context, fragments,
        knotContext -> {
          context.assertEquals(HttpResponseStatus.OK.code(),
              knotContext.getClientResponse().getStatusCode());
          context
              .assertEquals(expectedResult, knotContext.getClientResponse().getBody().toString());
        });
  }

  @Test
  @KnotxConfiguration("io/knotx/assembler/test.ignore.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithManySnippets_expectIgnoreResult(TestContext context)
      throws Exception {
    List<Pair<List<String>, String>> fragments = Arrays.asList(
        toPair("io/knotx/assembler/fragment1.txt", RAW),
        toPair("io/knotx/assembler/fragment2.txt", SERVICES, HANDLEBARS),
        toPair("io/knotx/assembler/fragment3.txt", RAW));
    String expectedResult = FileReader.readText("io/knotx/server/expectedIgnoreResult.html");
    callAssemblerWithAssertions(context, fragments,
        knotContext -> {
          context.assertEquals(HttpResponseStatus.OK.code(),
              knotContext.getClientResponse().getStatusCode());
          context
              .assertEquals(expectedResult, knotContext.getClientResponse().getBody().toString());
        });
  }

  private void callAssemblerWithAssertions(TestContext context,
      List<Pair<List<String>, String>> fragments,
      Consumer<KnotContext> testFunction) {
    Async async = context.async();
    KnotProxy service = KnotProxy.createProxy(new Vertx(vertx.vertx()), ADDRESS);

    service.rxProcess(KnotContextFactory.create(fragments))
        .map(ctx -> Pair.of(async, ctx))
        .doOnSuccess(success -> testFunction.accept(success.getRight()))
        .subscribe(
            success -> async.complete(),
            context::fail
        );
  }

  private Pair<List<String>, String> toPair(String filePath, String... knots) throws IOException {
    return new ImmutablePair<>(Arrays.asList(knots), FileReader.readText(filePath));
  }

}
