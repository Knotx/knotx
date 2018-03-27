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


import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.junit.rule.KnotxConfiguration;
import io.knotx.junit.rule.TestVertxDeployer;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Observable;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.core.http.HttpClientRequest;
import io.vertx.reactivex.core.http.HttpClientResponse;
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class KnotxServerRoutingTest {

  private static final int KNOTX_SERVER_PORT = 9092;
  private static final String KNOTX_SERVER_ADDRESS = "localhost";
  public static final String EXPECTED_XSERVER_HEADER_VALUE = "Knot.x";
  public static final String EXPECTED_RESPONSE_HEADER = "X-Server";

  private RunTestOnContext vertx = new RunTestOnContext();

  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  @Rule
  public RuleChain chain = RuleChain.outerRule(vertx).around(knotx);

  private static Observable<HttpClientResponse> request(HttpClient client, HttpMethod method,
      int port, String domain, String uri,
      Consumer<HttpClientRequest> requestBuilder) {
    return Observable.unsafeCreate(subscriber -> {
      HttpClientRequest req = client.request(method, port, domain, uri);
      Observable<HttpClientResponse> resp = req.toObservable();
      resp.subscribe(subscriber);
      requestBuilder.accept(req);
      req.end();
    });
  }

  @Test
  @KnotxConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingWithInvalidQuery_expectBadRequest(TestContext context) {
    HttpClient client = Vertx.newInstance(vertx.vertx()).createHttpClient();
    Async async = context.async();
    client.getNow(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, "/content/local/simple.html?q=~!@\\||$%^&*()_=-%22;;%27%22:%3C%3E/?]}{",
        resp -> {
          context.assertEquals(HttpResponseStatus.BAD_REQUEST.code(), resp.statusCode());
          client.close();
          async.complete();
        });
  }

  @Test
  @KnotxConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingGetLocalPath_expectLocalAC(TestContext context) {
    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("A-engine", "+A", "go-c");
    createSimpleKnot("C-engine", "+C", null);
    testGetRequest(context, "/content/local/simple.html", "local+A+C");
  }

  @Test
  @KnotxConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingGetGlobalPath_expectGlobalC(TestContext context) {
    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("C-engine", "+C", null);
    testGetRequest(context, "/content/simple.html", "global+C");
  }

  @Test
  @KnotxConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingPostLocalPathWithFirstTransition_expectLocalApostBC(
      TestContext context) {
    Async async = context.async();

    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("A-post-engine", "+Apost", "go-b");
    createSimpleKnot("B-engine", "+B", "go-c");
    createSimpleKnot("C-engine", "+C", null);
    testPostRequest("/content/local/simple.html",
        resp -> {
          context.assertEquals(resp.statusCode(), HttpResponseStatus.OK.code());
          context.assertTrue(resp.getHeader(EXPECTED_RESPONSE_HEADER) != null);
          context.assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
              resp.getHeader(EXPECTED_RESPONSE_HEADER));
          resp.bodyHandler(body -> {
            try {
              context.assertEquals(body.toString(), "local+Apost+B+C",
                  "Wrong engines processed request, expected " + "local+Apost+B+C");
            } catch (Exception e) {
              context.fail(e);
            }
            async.complete();
          });
        });
  }

  @Test
  @KnotxConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingPostLocalPathWithAlternateTransition_expectLocalApostC(
      TestContext context) {
    Async async = context.async();

    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("A-post-engine", "+Apost", "go-c");
    createSimpleKnot("C-engine", "+C", null);
    testPostRequest("/content/local/simple.html",
        resp -> {
          context.assertEquals(resp.statusCode(), HttpResponseStatus.OK.code());
          context.assertTrue(resp.getHeader(EXPECTED_RESPONSE_HEADER) != null);
          context.assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
              resp.getHeader(EXPECTED_RESPONSE_HEADER));
          resp.bodyHandler(body -> {
            try {
              context.assertEquals(body.toString(), "local+Apost+C",
                  "Wrong engines processed request, expected " + "local+Apost+C");
            } catch (Exception e) {
              context.fail(e);
            }
            async.complete();
          });
        });
  }

  @Test
  @KnotxConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingPostGlobalPath_expectGlobalBC(TestContext context) {
    Async async = context.async();

    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleKnot("B-engine", "+B", "go-c");
    createSimpleKnot("C-engine", "+C", null);

    testPostRequest("/content/simple.html", resp -> {
      context.assertEquals(resp.statusCode(), HttpResponseStatus.OK.code());
      context.assertTrue(resp.getHeader(EXPECTED_RESPONSE_HEADER) != null);
      context.assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
          resp.getHeader(EXPECTED_RESPONSE_HEADER));
      resp.bodyHandler(body -> {
        try {
          context.assertEquals(body.toString(), "global+B+C",
              "Wrong engines processed request, expected " + "global+B+C");
        } catch (Exception e) {
          context.fail(e);
        }
        async.complete();
      });
    });
  }

  @Test
  @KnotxConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingPostGlobalPathAndActionDoRedirect_expectRedirectResponse(
      TestContext context) {
    Async async = context.async();

    createPassThroughKnot("test-splitter");
    createPassThroughKnot("test-assembler");
    createSimpleFailingKnot("A-post-engine", HttpResponseStatus.MOVED_PERMANENTLY.code(),
        MultiMap.caseInsensitiveMultiMap().add("location", "/content/failed.html"));

    testPostRequest("/content/local/simple.html", resp -> {
      context.assertEquals(resp.statusCode(), HttpResponseStatus.MOVED_PERMANENTLY.code());
      context.assertEquals(resp.getHeader("location"), "/content/failed.html");
      context.assertTrue(resp.getHeader(EXPECTED_RESPONSE_HEADER) != null);
      context.assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
          resp.getHeader(EXPECTED_RESPONSE_HEADER));
      async.complete();
    });
  }

  @Test
  @KnotxConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingGetWithCustomFlowProcessing(TestContext context) {
    createPassThroughKnot("responseprovider");
    createSimpleGatewayKnot("gateway", "next");
    createSimpleKnot("requestprocessor", "message", null);
    testGetRequest(context, "/customFlow/remote/simple.json", "message");
  }

  private void testPostRequest(String url, Consumer<HttpClientResponse> expectedResponse) {
    HttpClient client = Vertx.newInstance(vertx.vertx()).createHttpClient();
    String testBody = "a=b";
    Observable<HttpClientResponse> request = request(client, HttpMethod.POST, KNOTX_SERVER_PORT,
        KNOTX_SERVER_ADDRESS, url, req -> {
          req.headers().set("content-length", String.valueOf(testBody.length()));
          req.headers().set("content-type", "application/x-www-form-urlencoded");
          req.write(testBody);
        });

    request.subscribe(expectedResponse::accept);
  }

  private void testGetRequest(TestContext context, String url, String expectedResult) {
    HttpClient client = Vertx.newInstance(vertx.vertx()).createHttpClient();
    Async async = context.async();
    client.getNow(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, url,
        resp -> resp.bodyHandler(body -> {
          context.assertEquals(HttpResponseStatus.OK.code(), resp.statusCode());
          context.assertTrue(resp.getHeader(EXPECTED_RESPONSE_HEADER) != null);
          context.assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
              resp.getHeader(EXPECTED_RESPONSE_HEADER));
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

  private void createSimpleKnot(final String address, final String addToBody,
      final String transition) {
    Consumer<KnotContext> simpleKnot = knotContext -> {
      Buffer inBody = knotContext.getClientResponse().getBody();
      knotContext.getClientResponse().setBody(inBody.appendString(addToBody));
      knotContext.setTransition(transition);
    };
    MockKnotProxy.register(vertx.vertx(), address, simpleKnot);
  }

  private void createSimpleGatewayKnot(final String address, final String transition) {
    Consumer<KnotContext> simpleKnot = knotContext -> {
      ClientResponse clientResponse = new ClientResponse();
      clientResponse.setBody(Buffer.buffer());
      clientResponse.setStatusCode(200);
      knotContext.setClientResponse(clientResponse);
      knotContext.setTransition(transition);
    };
    MockKnotProxy.register(vertx.vertx(), address, simpleKnot);
  }

  private void createSimpleFailingKnot(final String address, final int statusCode,
      final MultiMap headers) {
    Consumer<KnotContext> simpleKnot = knotContext -> {
      knotContext.getClientResponse().setStatusCode(statusCode).setHeaders(headers);
      knotContext.setTransition(null);
    };
    MockKnotProxy.register(vertx.vertx(), address, simpleKnot);
  }
}
