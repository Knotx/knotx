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
import io.knotx.junit.util.FileReader;
import io.knotx.junit.util.KnotContextFactory;
import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.knotx.reactivex.proxy.KnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(KnotxExtension.class)
public class FragmentAssemblerTest {

  private final static String ADDRESS = "knotx.core.assembler";
  private final static String RAW = "_raw";
  private static final String SERVICES = "services";
  private static final String HANDLEBARS = "handlebars";

  @Test
  @KnotxApplyConfiguration("io/knotx/assembler/test.unwrap.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithNoSnippets_expectInternalServerError(
      VertxTestContext context, Vertx vertx) {
    callAssemblerWithAssertions(context, vertx,
        null,
        knotContext -> Assertions.assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
            knotContext.getClientResponse().getStatusCode()
        ));
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/assembler/test.unwrap.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithEmptySnippet_expectNoContentStatus(
      VertxTestContext context, Vertx vertx) {
    callAssemblerWithAssertions(context, vertx,
        Collections
            .singletonList(new ImmutablePair<>(Collections.singletonList(RAW), StringUtils.SPACE)),
        knotContext -> Assertions.assertEquals(HttpResponseStatus.NO_CONTENT.code(),
            knotContext.getClientResponse().getStatusCode()));
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/assembler/test.asIs.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithManySnippets_expectAsIsResult(
      VertxTestContext context, Vertx vertx) throws IOException {
    List<Pair<List<String>, String>> fragments = Arrays.asList(
        toPair("io/knotx/assembler/fragment1.txt", RAW),
        toPair("io/knotx/assembler/fragment2.txt", SERVICES, HANDLEBARS),
        toPair("io/knotx/assembler/fragment3.txt", RAW));
    String expectedResult = FileReader.readText("io/knotx/server/expectedAsIsResult.html");
    callAssemblerWithAssertions(context, vertx,
        fragments,
        knotContext -> {
          Assertions.assertEquals(HttpResponseStatus.OK.code(),
              knotContext.getClientResponse().getStatusCode());
          Assertions
              .assertEquals(expectedResult, knotContext.getClientResponse().getBody().toString());
        });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/assembler/test.unwrap.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithManySnippets_expectUnwrapResult(
      VertxTestContext context, Vertx vertx) throws IOException {
    List<Pair<List<String>, String>> fragments = Arrays.asList(
        toPair("io/knotx/assembler/fragment1.txt", RAW),
        toPair("io/knotx/assembler/fragment2.txt", SERVICES, HANDLEBARS),
        toPair("io/knotx/assembler/fragment3.txt", RAW));
    String expectedResult = FileReader.readText("io/knotx/server/expectedUnwrapResult.html");
    callAssemblerWithAssertions(context, vertx,
        fragments,
        knotContext -> {
          Assertions.assertEquals(HttpResponseStatus.OK.code(),
              knotContext.getClientResponse().getStatusCode());
          Assertions
              .assertEquals(expectedResult, knotContext.getClientResponse().getBody().toString());
        });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/assembler/test.ignore.io.knotx.FragmentAssembler.json")
  public void callAssemblerWithManySnippets_expectIgnoreResult(
      VertxTestContext context, Vertx vertx) throws IOException {
    List<Pair<List<String>, String>> fragments = Arrays.asList(
        toPair("io/knotx/assembler/fragment1.txt", RAW),
        toPair("io/knotx/assembler/fragment2.txt", SERVICES, HANDLEBARS),
        toPair("io/knotx/assembler/fragment3.txt", RAW));
    String expectedResult = FileReader.readText("io/knotx/server/expectedIgnoreResult.html");
    callAssemblerWithAssertions(context, vertx,
        fragments,
        knotContext -> {
          Assertions.assertEquals(HttpResponseStatus.OK.code(),
              knotContext.getClientResponse().getStatusCode());
          Assertions
              .assertEquals(expectedResult, knotContext.getClientResponse().getBody().toString());
        });
  }

  private void callAssemblerWithAssertions(
      VertxTestContext context, Vertx vertx,
      List<Pair<List<String>, String>> fragments,
      Consumer<KnotContext> verifyResultFunction) {
    KnotProxy service = KnotProxy.createProxy(vertx, ADDRESS);

    service.rxProcess(KnotContextFactory.create(fragments))
        .doOnSuccess(verifyResultFunction::accept)
        .subscribe(
            success -> context.completeNow(),
            context::failNow
        );
  }

  private Pair<List<String>, String> toPair(String filePath, String... knots) throws IOException {
    return new ImmutablePair<>(Arrays.asList(knots), FileReader.readText(filePath));
  }

}
