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


import static io.knotx.junit5.util.RequestUtil.subscribeToResult_shouldSucceed;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(KnotxExtension.class)
public class KnotxServerRoutingTest {

  public static final String EXPECTED_XSERVER_HEADER_VALUE = "Knot.x";
  public static final String EXPECTED_RESPONSE_HEADER = "X-Server";
  private static final int KNOTX_SERVER_PORT = 9092;
  private static final String KNOTX_SERVER_ADDRESS = "localhost";


  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.conf")
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
  @KnotxApplyConfiguration("io/knotx/server/test-server.conf")
  public void whenRequestingGetLocalPath_expectLocalAC(
      VertxTestContext context, Vertx vertx) {
    createSimpleKnot(vertx, "A-engine", "+A", "go-c");
    createSimpleKnot(vertx, "C-engine", "+C", null);
    testGetRequest(context, vertx, "/content/routing.html", "+A+C");
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.conf")
  public void whenRequestingGetGlobalPath_expectGlobalC(
      VertxTestContext context, Vertx vertx) {
    createSimpleKnot(vertx, "A-engine", "+A", null);
    testGetRequest(context, vertx, "/content/routing.html", "+A");
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.conf")
  public void whenRequestingPostLocalPathWithFirstTransition_expectLocalApostBC(
      VertxTestContext context, Vertx vertx) {
    createSimpleKnot(vertx, "A-post-engine", "+Apost", "go-b");
    createSimpleKnot(vertx, "B-engine", "+B", "go-c");
    createSimpleKnot(vertx, "C-engine", "+C", null);
    testPostRequest(context, vertx, "/content/routing.html",
        resp -> {
          assertEquals(HttpResponseStatus.OK.code(), resp.statusCode());
          assertNotNull(resp.getHeader(EXPECTED_RESPONSE_HEADER));
          assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
              resp.getHeader(EXPECTED_RESPONSE_HEADER));

          assertEquals("+Apost+B+C", resp.bodyAsString(),
              "Wrong engines processed request, expected " + "+Apost+B+C");
        });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.conf")
  public void whenRequestingPostLocalPathWithAlternateTransition_expectLocalApostC(
      VertxTestContext context, Vertx vertx) {
    createSimpleKnot(vertx, "A-post-engine", "+Apost", "go-c");
    createSimpleKnot(vertx, "C-engine", "+C", null);
    testPostRequest(context, vertx, "/content/routing.html",
        resp -> {
          assertEquals(HttpResponseStatus.OK.code(), resp.statusCode());
          assertNotNull(resp.getHeader(EXPECTED_RESPONSE_HEADER));
          assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
              resp.getHeader(EXPECTED_RESPONSE_HEADER));

          assertEquals("+Apost+C", resp.bodyAsString(),
              "Wrong engines processed request, expected " + "+Apost+C");
        });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.conf")
  public void whenRequestingPostGlobalPathAndActionDoRedirect_expectRedirectResponse(
      VertxTestContext context, Vertx vertx) {
    createSimpleFailingKnot(vertx, "A-post-engine", HttpResponseStatus.MOVED_PERMANENTLY.code(),
        MultiMap.caseInsensitiveMultiMap().add("location", "/content/failed.html"));

    testPostRequest(context, vertx, "/content/routing.html", resp -> {
      assertEquals(HttpResponseStatus.MOVED_PERMANENTLY.code(), resp.statusCode());
      assertEquals("/content/failed.html", resp.getHeader("location"));
      assertNotNull(resp.getHeader(EXPECTED_RESPONSE_HEADER));
      assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
          resp.getHeader(EXPECTED_RESPONSE_HEADER));
    });
  }

  private void testPostRequest(VertxTestContext context, Vertx vertx, String url,
      Consumer<HttpResponse<Buffer>> expectedResponse) {
    WebClient client = WebClient.create(vertx);

    MultiMap formData = MultiMap.caseInsensitiveMultiMap();
    formData.add("a", "b");

    Single<HttpResponse<Buffer>> httpResponseSingle = client
        .post(KnotxServerRoutingTest.KNOTX_SERVER_PORT,
            KnotxServerRoutingTest.KNOTX_SERVER_ADDRESS,
            url)
        .rxSendForm(formData);
    subscribeToResult_shouldSucceed(context, httpResponseSingle, expectedResponse);
  }

  private void testGetRequest(VertxTestContext context, Vertx vertx, String url,
      String expectedResult) {
    WebClient client = WebClient.create(vertx);

    Single<HttpResponse<Buffer>> httpResponseSingle = client
        .get(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, url).rxSend();

    subscribeToResult_shouldSucceed(context, httpResponseSingle,
        resp -> {
          assertEquals(expectedResult, resp.body().toString(),
              "Wrong engines processed request, expected " + expectedResult);
          assertEquals(HttpResponseStatus.OK.code(), resp.statusCode());
          assertNotNull(resp.getHeader(EXPECTED_RESPONSE_HEADER));
          assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
              resp.getHeader(EXPECTED_RESPONSE_HEADER));
        });
  }

  private void createSimpleKnot(Vertx vertx, final String address, final String addToBody,
      final String transition) {
    MockKnotProxy.register(vertx.getDelegate(), address, knotContext -> {
      knotContext.getClientResponse().setBody(
          knotContext.getClientResponse().getBody().appendString(addToBody)
      );
      knotContext.setTransition(transition);
    });
  }

  private void createSimpleFailingKnot(Vertx vertx, final String address, final int statusCode,
      final MultiMap headers) {
    MockKnotProxy.register(vertx.getDelegate(), address, knotContext -> {
      knotContext.getClientResponse().setStatusCode(statusCode).setHeaders(headers);
      knotContext.setTransition(null);
    });
  }
}
