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
package com.cognifide.knotx.monolith;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class SampleApplicationTest {

    private static final String REMOTE_REQUEST_URI = "/content/remote/simple.html";

    @BeforeClass
    public static void setUp() throws Exception {
        ApplicationTestHelper.startKnotx();
    }

    @Test
    public void localSimpleHtmlTest(TestContext context) {
        HttpClient client = ApplicationTestHelper.vertx.createHttpClient();
        Async async = context.async();
        client.getNow(ApplicationTestHelper.knotxPort, ApplicationTestHelper.knotxDomain, "/content/local/simple.html",
                resp -> resp.bodyHandler(body -> {
                    String fileName = "localSimpleResult.html";
                    context.assertEquals(resp.statusCode(), 200);
                    assertBodyIsEqualWitMockedFile(context, body, fileName);
                    client.close();
                    async.complete();
                }));
    }

    @Test
    public void remoteSimpleHtmlTest(TestContext context) {
        HttpClient client = ApplicationTestHelper.vertx.createHttpClient();
        Async async = context.async();
        client.getNow(ApplicationTestHelper.knotxPort, ApplicationTestHelper.knotxDomain, REMOTE_REQUEST_URI,
                resp -> resp.bodyHandler(body -> {
                    String fileName = "remoteSimpleResult.html";
                    context.assertEquals(resp.statusCode(), 200);
                    assertBodyIsEqualWitMockedFile(context, body, fileName);
                    client.close();
                    async.complete();
                }));
    }

    @Test
    public void rewritePreservedHeadersTest(TestContext context) {
        HttpClient client = ApplicationTestHelper.vertx.createHttpClient();
        Async async = context.async();
        HttpClientRequest httpClientRequest = client.get(ApplicationTestHelper.knotxPort, ApplicationTestHelper.knotxDomain, REMOTE_REQUEST_URI,
                resp -> {
                    context.assertEquals("Pl", resp.headers().get("X-Language-Code"));
                    context.assertNull(resp.getHeader("Location"));
                    client.close();
                    async.complete();
                }
        );
        httpClientRequest.putHeader("X-Language-Code", "Pl");
        httpClientRequest.putHeader("Location", "http://localhost/content/remote/simple.html");
        httpClientRequest.end();
    }

    @AfterClass
    public static void tearDown(TestContext context) {
        ApplicationTestHelper.tearDown(context);
    }

    private void assertBodyIsEqualWitMockedFile(TestContext context, Buffer body, String fileName) {
        try {
            context.assertEquals(body.toString(), ApplicationTestHelper.readText(fileName));
        } catch (Exception e) {
            context.fail(e);
        }
    }

}
