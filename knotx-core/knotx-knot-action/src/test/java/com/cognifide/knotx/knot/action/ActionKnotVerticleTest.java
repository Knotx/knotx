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

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.junit.FileReader;
import com.cognifide.knotx.junit.KnotxConfiguration;
import com.cognifide.knotx.junit.Logback;
import com.cognifide.knotx.junit.TestVertxDeployer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import rx.Observable;
import rx.functions.Action1;

@RunWith(VertxUnitRunner.class)
public class ActionKnotVerticleTest {

  private final static String ADDRESS = "knotx.knot.action";

  //Test Runner Rule of Verts
  private RunTestOnContext vertx = new RunTestOnContext();

  //Test Runner Rule of Knotx
  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  //Junit Rule, sets up logger, prepares verts, starts verticles according to the config (supplied in annotation of test method)
  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(vertx).around(knotx);

  @Test
  @KnotxConfiguration("knotx-knot-action-test.json")
  public void callWithNoActionFragments_expectResponseOkNoFragmentChanges(TestContext context) throws Exception {
    String firstFragment = "<html><head></head><body>";
    String lastFragment = "</body></html>";
    String expectedFormFragment = FileReader.readText("form_step1_out_fragment.txt");

    KnotContext knotContext = KnotContext.empty(Arrays.asList(
        Fragment.raw(firstFragment),
        Fragment.snippet("form-identifier", FileReader.readText("form_step1_in_fragment.txt")),
        Fragment.raw(lastFragment)));

    callActionKnotWithAssertions(context, knotContext,
        clientResponse -> {
          context.assertEquals(HttpResponseStatus.OK, knotContext.clientResponse().statusCode());
          List<Fragment> fragments = knotContext.fragments().get();
          context.assertEquals(firstFragment, fragments.get(0).getContent());
          context.assertEquals(expectedFormFragment, fragments.get(1).getContent());
          context.assertEquals(lastFragment, fragments.get(2).getContent());

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

}
