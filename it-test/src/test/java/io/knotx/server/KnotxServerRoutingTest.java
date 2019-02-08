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

import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.vertx.junit5.VertxTestContext;
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
  public void requestUnsupportedPath_expectNotFound(VertxTestContext context, Vertx vertx) {
    WebClient client = WebClient.create(vertx);
    Single<HttpResponse<Buffer>> httpResponseSingle = client
        .get(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, "/notexisting.html")
        .rxSend();

    subscribeToResult_shouldSucceed(context, httpResponseSingle, result -> {
      assertEquals(HttpResponseStatus.NOT_FOUND.code(), result.statusCode());
    });
  }

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

}
