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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpClient;


@RunWith(VertxUnitRunner.class)
public class SampleApplicationHeadersTest {

  public static final String REMOTE_REQUEST_URI = "/content/remote/simple.html";

  private MultiMap expectedHeaders = MultiMap.caseInsensitiveMultiMap();

  @BeforeClass
  public static void setUp() throws Exception {
    ApplicationTestHelper.startKnotx();
  }

  @AfterClass
  public static void tearDown(TestContext context) {
    ApplicationTestHelper.tearDown(context);
  }

  @Before
  public void before() {
    expectedHeaders.add("Access-Control-Allow-Origin", "*");
    expectedHeaders.add("Content-Type", "text/html; charset=UTF-8");
    expectedHeaders.add("content-length", "4090");
  }

  @Test
  public void whenRequestingRemoteRepository_expectOnlyAllowedResponseHeaders(TestContext context) {
    testGetRequest(context, REMOTE_REQUEST_URI);
  }

  private void testGetRequest(TestContext context, String url) {
    HttpClient client = ApplicationTestHelper.vertx.createHttpClient();
    Async async = context.async();
    client.getNow(ApplicationTestHelper.knotxPort, ApplicationTestHelper.knotxDomain, url,
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
