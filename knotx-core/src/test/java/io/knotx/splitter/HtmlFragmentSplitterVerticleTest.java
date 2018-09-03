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

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.knotx.dataobjects.KnotContext;
import io.knotx.junit.util.FileReader;
import io.knotx.junit.util.KnotContextFactory;
import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.knotx.reactivex.proxy.KnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(KnotxExtension.class)
public class HtmlFragmentSplitterVerticleTest {

  private final static String ADDRESS = "knotx.core.splitter";

  @Test
  @KnotxApplyConfiguration("io/knotx/splitter/knotx-fragment-splitter-test.json")
  public void callSplitterWithEmptyBody_expectNotFoundResponse(
      VertxTestContext context, Vertx vertx) {
    callFragmentSplitterWithAssertions(context, vertx, "",
        knotContext -> {
          assertEquals(knotContext.getClientResponse().getStatusCode(),
              HttpResponseStatus.NOT_FOUND.code());
          assertNull(knotContext.getFragments());
        });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/splitter/knotx-fragment-splitter-test.json")
  public void callSplitterWithManySnippets_expectNineFragments(
      VertxTestContext context, Vertx vertx) throws Exception {
    callFragmentSplitterWithAssertions(context, vertx, FileReader.readText(
        "io/knotx/splitter/test-many-fragments.html"),
        knotContext -> {
          assertNotNull(knotContext.getFragments());
          assertEquals(knotContext.getFragments().size(), 9);
        });
  }

  private void callFragmentSplitterWithAssertions(
      VertxTestContext context, Vertx vertx,
      String template,
      Consumer<KnotContext> testFunction) {
    KnotProxy service = KnotProxy.createProxy(vertx, ADDRESS);

    service.rxProcess(KnotContextFactory.empty(template))
        .doOnSuccess(testFunction::accept)
        .subscribe(
            success -> context.completeNow(),
            context::failNow
        );
  }

}
