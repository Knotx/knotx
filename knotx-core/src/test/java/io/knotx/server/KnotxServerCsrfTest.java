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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.vertx.ext.web.handler.CSRFHandler;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(KnotxExtension.class)
public class KnotxServerCsrfTest {

  private static final int KNOTX_SERVER_PORT = 9092;
  private static final String KNOTX_SERVER_ADDRESS = "localhost";
  private static final String EXPECTED_XSERVER_HEADER_VALUE = "Knot.x";
  private static final String EXPECTED_RESPONSE_HEADER = "X-Server";
  private static final String CONTENT_CSRF_HTML = "/content/csrf.html";


  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.conf")
  public void whenRequestingGetLocalPath_expectLocalAC(
      VertxTestContext context, Vertx vertx) {
    WebClient client = WebClient.create(vertx);

    Single<HttpResponse<io.vertx.reactivex.core.buffer.Buffer>> httpResponseSingle = client
        .get(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, CONTENT_CSRF_HTML)
        .rxSend();

    subscribeToResult_shouldSucceed(context, httpResponseSingle, response -> {
      assertEquals(HttpResponseStatus.OK.code(), response.statusCode());
      assertTrue(response.getHeader(EXPECTED_RESPONSE_HEADER) != null);
      assertEquals(EXPECTED_XSERVER_HEADER_VALUE,
          response.getHeader(EXPECTED_RESPONSE_HEADER));
      assertTrue(response.cookies().stream()
          .anyMatch(cookie -> cookie.contains(CSRFHandler.DEFAULT_COOKIE_NAME)));
    });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.conf")
  public void whenDoPostSecureWithoutCSRF_expectForbidden(
      VertxTestContext context, Vertx vertx) {
    MultiMap body = MultiMap.caseInsensitiveMultiMap().add("field", "value");

    WebClient client = WebClient.create(vertx);
    Single<HttpResponse<Buffer>> httpResponseSingle = client
        .post(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, CONTENT_CSRF_HTML)
        .rxSendForm(body);

    subscribeToResult_shouldSucceed(context, httpResponseSingle, result -> {
      assertEquals(HttpResponseStatus.FORBIDDEN.code(), result.statusCode());
    });
  }

  @Test
  @KnotxApplyConfiguration("io/knotx/server/test-server.conf")
  public void whenDoPostSecureWithCSRF_expectOK(
      VertxTestContext context, Vertx vertx) {
    MultiMap body = MultiMap.caseInsensitiveMultiMap().add("field", "value");

    WebClient client = WebClient.create(vertx);

    client.get(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, CONTENT_CSRF_HTML).send(
        ar -> {
          if (ar.succeeded()) {
            assertTrue(ar.result().cookies().stream()
                .anyMatch(cookie -> cookie.contains(CSRFHandler.DEFAULT_COOKIE_NAME)));
            String token = getToken(ar.result().cookies());
            client.post(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, CONTENT_CSRF_HTML)
                .putHeader(CSRFHandler.DEFAULT_HEADER_NAME, token)
                .putHeader(HttpHeaderNames.COOKIE.toString(),
                    CSRFHandler.DEFAULT_COOKIE_NAME + "=" + token)
                .sendForm(body, res -> {
                  if (res.succeeded()) {
                    assertEquals(HttpResponseStatus.OK.code(), res.result().statusCode());
                    context.completeNow();
                  } else {
                    context.failNow(ar.cause());
                  }
                });
          } else {
            context.failNow(ar.cause());
          }
        });
  }

  private String getToken(List<String> result) {
    String val = result.get(0);
    return val.split(CSRFHandler.DEFAULT_COOKIE_NAME + "=")[1].split(";")[0];
  }
}
