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
package io.knotx.server;


import io.knotx.dataobjects.KnotContext;
import io.knotx.junit.rule.KnotxConfiguration;
import io.knotx.junit.rule.TestVertxDeployer;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.handler.CSRFHandler;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class KnotxServerCsrfTest {

  private static final int KNOTX_SERVER_PORT = 9092;
  private static final String KNOTX_SERVER_ADDRESS = "localhost";
  public static final String EXPECTED_XSERVER_HEADER_VALUE = "Knot.x";
  public static final String EXPECTED_RESPONSE_HEADER = "X-Server";

  private RunTestOnContext vertx = new RunTestOnContext();

  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  @Rule
  public RuleChain chain = RuleChain.outerRule(vertx).around(knotx);


  @Test
  @KnotxConfiguration("io/knotx/server/test-server-csrf.json")
  public void whenRequestingGetLocalPath_expectLocalAC(TestContext context) {
    Async async = context.async();
    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("some-knot", "test", null);

    WebClient client = WebClient.create(Vertx.newInstance(vertx.vertx()));

    client.get(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, "/content/local/simple.html").send(
        ar -> {
          if (ar.succeeded()) {
            context.assertEquals(HttpResponseStatus.OK.code(), ar.result().statusCode());
            context.assertTrue(ar.result().getHeader(EXPECTED_RESPONSE_HEADER) != null);
            context.assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
                ar.result().getHeader(EXPECTED_RESPONSE_HEADER));
            context.assertTrue(ar.result().cookies().stream()
                .anyMatch(cookie -> cookie.contains(CSRFHandler.DEFAULT_COOKIE_NAME)));
            client.close();
            async.complete();
          } else {
            context.fail(ar.cause());
            async.complete();
          }
        }
    );
  }

  @Test
  @KnotxConfiguration("io/knotx/server/test-server-csrf.json")
  public void whenDoPostSecureWithoutCSRF_expectForbidden(
      TestContext context) {
    Async async = context.async();

    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("some-knot", "test", null);

    MultiMap body = MultiMap.caseInsensitiveMultiMap().add("field", "value");

    WebClient client = WebClient.create(Vertx.newInstance(vertx.vertx()));
    client.post(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, "/content/local/simple.html")
        .sendForm(body, ar -> {
          if (ar.succeeded()) {
            context.assertEquals(HttpResponseStatus.FORBIDDEN.code(), ar.result().statusCode());
            async.complete();
          } else {
            context.fail(ar.cause());
            async.complete();
          }
        });
  }

  @Test
  @KnotxConfiguration("io/knotx/server/test-server-csrf.json")
  public void whenDoPostPublicWithoutCSRF_expectOk(
      TestContext context) {
    Async async = context.async();

    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("some-knot", "test", null);

    MultiMap body = MultiMap.caseInsensitiveMultiMap().add("field", "value");

    WebClient client = WebClient.create(Vertx.newInstance(vertx.vertx()));
    client.post(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, "/content/local/public.html")
        .sendForm(body, ar -> {
          if (ar.succeeded()) {
            context.assertEquals(HttpResponseStatus.OK.code(), ar.result().statusCode());
            async.complete();
          } else {
            context.fail(ar.cause());
            async.complete();
          }
        });
  }

  @Test
  @KnotxConfiguration("io/knotx/server/test-server-csrf.json")
  public void whenDoPostSecureWithCSRF_expectOK(
      TestContext context) {
    Async async = context.async();

    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("some-knot", "test", null);

    MultiMap body = MultiMap.caseInsensitiveMultiMap().add("field", "value");

    WebClient client = WebClient.create(Vertx.newInstance(vertx.vertx()));

    client.get(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, "/content/local/simple.html").send(
        ar -> {
          if (ar.succeeded()) {
            String token = getToken(ar.result().cookies());

            client.post(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, "/content/local/simple.html")
                .putHeader(CSRFHandler.DEFAULT_HEADER_NAME, token)
                .sendForm(body, res -> {
                  if (res.succeeded()) {
                    context.assertEquals(HttpResponseStatus.OK.code(), res.result().statusCode());
                    async.complete();
                  } else {
                    context.fail(ar.cause());
                    async.complete();
                  }
                });
          } else {
            context.fail(ar.cause());
            async.complete();
          }
        });
  }

  private void createPassThroughKnot(String address) {
    MockKnotProxy.register(vertx.vertx(), address);
  }

  private void createSimpleKnot(final String address, final String addToBody,
      final String transition) {
    Consumer<KnotContext> simpleKnot = knotContext -> {
      Buffer inBody = knotContext.getClientResponse().getBody();
      knotContext.getClientResponse().setBody(inBody.appendString(addToBody));
      knotContext.setTransition(transition);
    };
    MockKnotProxy.register(vertx.vertx(), address, simpleKnot);
  }

  private String getToken(List<String> result) {
    String val = result.get(0);
    return val.split("XSRF-TOKEN=")[1].split(";")[0];
  }
}
