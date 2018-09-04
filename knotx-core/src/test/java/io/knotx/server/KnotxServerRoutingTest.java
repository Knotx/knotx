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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.knotx.junit5.KnotxTestUtils;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Observable;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.core.http.HttpClientResponse;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(KnotxExtension.class)
public class KnotxServerRoutingTest {

  public static final String EXPECTED_XSERVER_HEADER_VALUE = "Knot.x";
  public static final String EXPECTED_RESPONSE_HEADER = "X-Server";
  private static final int KNOTX_SERVER_PORT = 9092;
  private static final String KNOTX_SERVER_ADDRESS = "localhost";


  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingWithInvalidQuery_expectBadRequest(
      VertxTestContext context, Vertx vertx) {
    HttpClient client = vertx.createHttpClient();
    client.getNow(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS,
        "/content/local/simple.html?q=~!@\\||$%^&*()_=-%22;;%27%22:%3C%3E/?]}{",
        resp -> {
          assertEquals(HttpResponseStatus.BAD_REQUEST.code(), resp.statusCode());
          client.close();
          context.completeNow();
        });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingGetLocalPath_expectLocalAC(
      VertxTestContext context, Vertx vertx) {
    createPassThroughKnot(vertx, "test-splitter");
    createPassThroughKnot(vertx, "test-assembler");
    createSimpleKnot(vertx, "A-engine", "+A", "go-c");
    createSimpleKnot(vertx, "C-engine", "+C", null);
    testGetRequest(context, vertx, "/content/local/simple.html", "local+A+C");
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingGetGlobalPath_expectGlobalC(
      VertxTestContext context, Vertx vertx) {
    createPassThroughKnot(vertx, "test-splitter");
    createPassThroughKnot(vertx, "test-assembler");
    createSimpleKnot(vertx, "C-engine", "+C", null);
    testGetRequest(context, vertx, "/content/simple.html", "global+C");
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingPostLocalPathWithFirstTransition_expectLocalApostBC(
      VertxTestContext context, Vertx vertx) {
    createPassThroughKnot(vertx, "test-splitter");
    createPassThroughKnot(vertx, "test-assembler");
    createSimpleKnot(vertx, "A-post-engine", "+Apost", "go-b");
    createSimpleKnot(vertx, "B-engine", "+B", "go-c");
    createSimpleKnot(vertx, "C-engine", "+C", null);
    testPostRequest(vertx, "/content/local/simple.html",
        resp -> {
          assertEquals(HttpResponseStatus.OK.code(), resp.statusCode());
          assertNotNull(resp.getHeader(EXPECTED_RESPONSE_HEADER));
          assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
              resp.getHeader(EXPECTED_RESPONSE_HEADER));
          resp.bodyHandler(body -> {
            try {
              assertEquals("local+Apost+B+C", body.toString(),
                  "Wrong engines processed request, expected " + "local+Apost+B+C");
            } catch (Exception e) {
              context.failNow(e);
            }
            context.completeNow();
          });
        });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingPostLocalPathWithAlternateTransition_expectLocalApostC(
      VertxTestContext context, Vertx vertx) {
    createPassThroughKnot(vertx, "test-splitter");
    createPassThroughKnot(vertx, "test-assembler");
    createSimpleKnot(vertx, "A-post-engine", "+Apost", "go-c");
    createSimpleKnot(vertx, "C-engine", "+C", null);
    testPostRequest(vertx, "/content/local/simple.html",
        resp -> {
          assertEquals(HttpResponseStatus.OK.code(), resp.statusCode());
          assertNotNull(resp.getHeader(EXPECTED_RESPONSE_HEADER));
          assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
              resp.getHeader(EXPECTED_RESPONSE_HEADER));
          resp.bodyHandler(body -> {
            try {
              assertEquals("local+Apost+C", body.toString(),
                  "Wrong engines processed request, expected " + "local+Apost+C");
            } catch (Exception e) {
              context.failNow(e);
            }
            context.completeNow();
          });
        });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingPostGlobalPath_expectGlobalBC(
      VertxTestContext context, Vertx vertx) {
    createPassThroughKnot(vertx, "test-splitter");
    createPassThroughKnot(vertx, "test-assembler");
    createSimpleKnot(vertx, "B-engine", "+B", "go-c");
    createSimpleKnot(vertx, "C-engine", "+C", null);

    testPostRequest(vertx, "/content/simple.html", resp -> {
      assertEquals(HttpResponseStatus.OK.code(), resp.statusCode());
      assertNotNull(resp.getHeader(EXPECTED_RESPONSE_HEADER));
      assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
          resp.getHeader(EXPECTED_RESPONSE_HEADER));
      resp.bodyHandler(body -> {
        try {
          assertEquals("global+B+C", body.toString(),
              "Wrong engines processed request, expected " + "global+B+C");
        } catch (Exception e) {
          context.failNow(e);
        }
        context.completeNow();
      });
    });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingPostGlobalPathAndActionDoRedirect_expectRedirectResponse(
      VertxTestContext context, Vertx vertx) {
    createPassThroughKnot(vertx, "test-splitter");
    createPassThroughKnot(vertx, "test-assembler");
    createSimpleFailingKnot(vertx, "A-post-engine", HttpResponseStatus.MOVED_PERMANENTLY.code(),
        MultiMap.caseInsensitiveMultiMap().add("location", "/content/failed.html"));

    testPostRequest(vertx, "/content/local/simple.html", resp -> {
      assertEquals(HttpResponseStatus.MOVED_PERMANENTLY.code(), resp.statusCode());
      assertEquals("/content/failed.html", resp.getHeader("location"));
      assertNotNull(resp.getHeader(EXPECTED_RESPONSE_HEADER));
      assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
          resp.getHeader(EXPECTED_RESPONSE_HEADER));
      context.completeNow();
    });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.json")
  public void whenRequestingGetWithCustomFlowProcessing(
      VertxTestContext context, Vertx vertx) {
    createPassThroughKnot(vertx, "responseprovider");
    createSimpleGatewayKnot(vertx, "gateway", "next");
    createSimpleKnot(vertx, "requestprocessor", "message", null);
    testGetRequest(context, vertx, "/customFlow/remote/simple.json", "message");
  }

  private void testPostRequest(Vertx vertx, String url, Consumer<HttpClientResponse> expectedResponse) {
    HttpClient client = vertx.createHttpClient();
    String testBody = "a=b";
    Observable<HttpClientResponse> request = KnotxTestUtils.asyncRequest(client, HttpMethod.POST, KNOTX_SERVER_PORT,
        KNOTX_SERVER_ADDRESS, url, req -> {
          req.headers().set("content-length", String.valueOf(testBody.length()));
          req.headers().set("content-type", "application/x-www-form-urlencoded");
          req.write(testBody);
        });

    request.subscribe(expectedResponse::accept);
  }

  private void testGetRequest(VertxTestContext context, Vertx vertx, String url, String expectedResult) {
    HttpClient client = vertx.createHttpClient();

    client.getNow(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, url,
        resp -> resp.bodyHandler(body -> {
          assertEquals(HttpResponseStatus.OK.code(), resp.statusCode());
          assertNotNull(resp.getHeader(EXPECTED_RESPONSE_HEADER));
          assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
              resp.getHeader(EXPECTED_RESPONSE_HEADER));
          try {
            assertEquals(expectedResult, body.toString(),
                "Wrong engines processed request, expected " + expectedResult);
          } catch (Exception e) {
            context.failNow(e);
          }
          client.close();
          context.completeNow();
        }));
  }

  private void createPassThroughKnot(Vertx vertx, String address) {
    MockKnotProxy.register(vertx.getDelegate(), address);
  }

  private void createSimpleKnot(Vertx vertx, final String address, final String addToBody,
      final String transition) {
    Consumer<KnotContext> simpleKnot = knotContext -> {
      Buffer inBody = knotContext.getClientResponse().getBody();
      knotContext.getClientResponse().setBody(inBody.appendString(addToBody));
      knotContext.setTransition(transition);
    };
    MockKnotProxy.register(vertx.getDelegate(), address, simpleKnot);
  }

  private void createSimpleGatewayKnot(Vertx vertx, final String address, final String transition) {
    Consumer<KnotContext> simpleKnot = knotContext -> {
      ClientResponse clientResponse = new ClientResponse();
      clientResponse.setBody(Buffer.buffer());
      clientResponse.setStatusCode(200);
      knotContext.setClientResponse(clientResponse);
      knotContext.setTransition(transition);
    };
    MockKnotProxy.register(vertx.getDelegate(), address, simpleKnot);
  }

  private void createSimpleFailingKnot(Vertx vertx, final String address, final int statusCode,
      final MultiMap headers) {
    Consumer<KnotContext> simpleKnot = knotContext -> {
      knotContext.getClientResponse().setStatusCode(statusCode).setHeaders(headers);
      knotContext.setTransition(null);
    };
    MockKnotProxy.register(vertx.getDelegate(), address, simpleKnot);
  }
}
