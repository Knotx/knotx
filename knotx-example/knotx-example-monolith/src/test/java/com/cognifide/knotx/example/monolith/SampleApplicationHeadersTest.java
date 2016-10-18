/*
 * Knot.x - Sample App with Mock service
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
package com.cognifide.knotx.example.monolith;

import com.cognifide.knotx.junit.KnotxConfiguration;
import com.cognifide.knotx.junit.Logback;
import com.cognifide.knotx.junit.TestVertxDeployer;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;

@Ignore
@RunWith(VertxUnitRunner.class)
public class SampleApplicationHeadersTest {

  private static final String REMOTE_REQUEST_URI = "/content/remote/simple.html";
  private static final int KNOTX_SERVER_PORT = 8092;
  private static final String KNOTX_SERVER_ADDRESS = "localhost";

  private MultiMap expectedHeaders = MultiMap.caseInsensitiveMultiMap();

  private RunTestOnContext vertx = new RunTestOnContext();

  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(vertx).around(knotx);

  @Before
  public void before() {
    expectedHeaders.add("Access-Control-Allow-Origin", "*");
    expectedHeaders.add("Content-Type", "text/html; charset=UTF-8");
    expectedHeaders.add("content-length", "4090");
  }

  @Test
  @KnotxConfiguration("knotx-example-monolith.json")
  public void whenRequestingRemoteRepository_expectOnlyAllowedResponseHeaders(TestContext context) {
    testGetRequest(context, REMOTE_REQUEST_URI);
  }

  private void testGetRequest(TestContext context, String url) {
    HttpClient client = Vertx.newInstance(vertx.vertx()).createHttpClient();
    Async async = context.async();
    client.getNow(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, url,
        resp -> {
          MultiMap headers = resp.headers();
          headers.names().stream()
              .forEach(name -> {
                context.assertTrue(expectedHeaders.contains(name));
                context.assertEquals(expectedHeaders.get(name), headers.get(name));
              });
          async.complete();
        });
  }
}
