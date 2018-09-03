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
package io.knotx.example.monolith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(KnotxExtension.class)
public class SampleApplicationHeadersIT {

  private static final String REMOTE_REQUEST_URI = "/content/remote/simple.html";
  private static final int KNOTX_SERVER_PORT = 9092;
  private static final String KNOTX_SERVER_ADDRESS = "localhost";

  private MultiMap expectedHeaders = MultiMap.caseInsensitiveMultiMap();

  @BeforeEach
  public void before() {
    expectedHeaders.add("Access-Control-Allow-Origin", "*");
    expectedHeaders.add("Content-Type", "text/html; charset=UTF-8");
    expectedHeaders.add("content-length", "3020");
    expectedHeaders.add("X-Server", "Knot.x");
  }

  @Test
  @KnotxApplyConfiguration("knotx-test-app.json")
  public void whenRequestingRemoteRepository_expectOnlyAllowedResponseHeaders(
      VertxTestContext context, Vertx vertx) {
    testGetRequest(context, vertx, REMOTE_REQUEST_URI);
  }

  private void testGetRequest(VertxTestContext context, Vertx vertx, String url) {
    HttpClient client = vertx.createHttpClient();
    client.getNow(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, url,
        resp -> {
          MultiMap headers = resp.headers();
          headers.names().forEach(name -> {
            assertEquals(resp.statusCode(), 200, "Wrong status code received.");
            assertTrue(expectedHeaders.contains(name), "Header " + name + " is not expected.");
            assertEquals(expectedHeaders.get(name), headers.get(name),
                "Wrong value of " + name + " header.");
          });
          context.completeNow();
        });
  }
}
