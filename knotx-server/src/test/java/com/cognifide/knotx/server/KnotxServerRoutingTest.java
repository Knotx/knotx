/*
 * Knot.x - Reactive microservice assembler - HTTP Server
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
package com.cognifide.knotx.server;


import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.junit.rule.KnotxConfiguration;
import com.cognifide.knotx.junit.rule.Logback;
import com.cognifide.knotx.junit.rule.TestVertxDeployer;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import rx.Observable;
import rx.functions.Action1;

@RunWith(VertxUnitRunner.class)
public class KnotxServerRoutingTest {

  private static final int KNOTX_SERVER_PORT = 8092;
  private static final String KNOTX_SERVER_ADDRESS = "localhost";

  private RunTestOnContext vertx = new RunTestOnContext();

  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(vertx).around(knotx);

  private static Observable<HttpClientResponse> request(HttpClient client, HttpMethod method, int port, String domain, String uri,
      Action1<HttpClientRequest> requestBuilder) {
    return Observable.create(subscriber -> {
      HttpClientRequest req = client.request(method, port, domain, uri);
      Observable<HttpClientResponse> resp = req.toObservable();
      resp.subscribe(subscriber);
      requestBuilder.call(req);
      req.end();
    });
  }

  @Test
  @KnotxConfiguration("test-server.json")
  public void whenRequestingGetLocalPath_expectLocalAC(TestContext context) {
    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("A-engine", "+A", "go-c");
    createSimpleKnot("C-engine", "+C", null);
    testGetRequest(context, "/content/local/simple.html", "local+A+C");
  }

  @Test
  @KnotxConfiguration("test-server.json")
  public void whenRequestingGetGlobalPath_expectGlobalC(TestContext context) {
    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("C-engine", "+C", null);
    testGetRequest(context, "/content/simple.html", "global+C");
  }

  @Test
  @KnotxConfiguration("test-server.json")
  public void whenRequestingPostLocalPathWithFirstTransition_expectLocalApostBC(TestContext context) {
    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("A-post-engine", "+Apost", "go-b");
    createSimpleKnot("B-engine", "+B", "go-c");
    createSimpleKnot("C-engine", "+C", null);
    testPostRequest(context, "/content/local/simple.html", "local+Apost+B+C");
  }

  @Test
  @KnotxConfiguration("test-server.json")
  public void whenRequestingPostLocalPathWithAlternateTransition_expectLocalApostC(TestContext context) {
    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("A-post-engine", "+Apost", "go-c");
    createSimpleKnot("C-engine", "+C", null);
    testPostRequest(context, "/content/local/simple.html", "local+Apost+C");
  }

  @Test
  @KnotxConfiguration("test-server.json")
  public void whenRequestingPostGlobalPath_expectGlobalBC(TestContext context) {
    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("B-engine", "+B", "go-c");
    createSimpleKnot("C-engine", "+C", null);
    testPostRequest(context, "/content/simple.html", "global+B+C");
  }

  private void testPostRequest(TestContext context, String url, String expectedResult) {
    HttpClient client = Vertx.newInstance(vertx.vertx()).createHttpClient();
    String testBody = "a=b";
    Async async = context.async();
    Observable<HttpClientResponse> request = request(client, HttpMethod.POST, KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, url, req -> {
      req.headers().set("content-length", String.valueOf(testBody.length()));
      req.headers().set("content-type", "application/x-www-form-urlencoded");
      req.write(testBody);
    });

    request.subscribe(resp -> resp.bodyHandler(body -> {
      context.assertEquals(resp.statusCode(), HttpResponseStatus.OK.code());
      try {
        context.assertEquals(body.toString(), expectedResult, "Wrong engines processed request, expected " + expectedResult);
      } catch (Exception e) {
        context.fail(e);
      }

      async.complete();
    }));
  }

  private void testGetRequest(TestContext context, String url, String expectedResult) {
    HttpClient client = Vertx.newInstance(vertx.vertx()).createHttpClient();
    Async async = context.async();
    client.getNow(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, url,
        resp -> resp.bodyHandler(body -> {
          context.assertEquals(resp.statusCode(), HttpResponseStatus.OK.code());
          try {
            context.assertEquals(body.toString(),
                expectedResult, "Wrong engines processed request, expected " + expectedResult);
          } catch (Exception e) {
            context.fail(e);
          }
          client.close();
          async.complete();
        }));
  }

  private void createPassThroughKnot(String address) {
    MockKnotProxy.register(vertx.vertx(), address);
  }

  private void createSimpleKnot(final String address, final String addToBody, final String transition) {
    Action1<KnotContext> simpleKnot = knotContext -> {
      Buffer inBody = knotContext.getClientResponse().getBody();
      knotContext.getClientResponse().setBody(inBody.appendString(addToBody));
      knotContext.setTransition(transition);
    };
    MockKnotProxy.register(vertx.vertx(), address, simpleKnot);
  }
}
