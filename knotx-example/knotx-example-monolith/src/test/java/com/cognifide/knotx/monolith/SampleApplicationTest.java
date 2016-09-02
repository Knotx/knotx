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

import com.cognifide.knotx.engine.service.KnotxRxHelper;

import org.jsoup.Jsoup;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;


@RunWith(VertxUnitRunner.class)
public class SampleApplicationTest {

    public static final String REMOTE_REQUEST_URI = "/content/remote/simple.html";
    public static final String LOCAL_REQUEST_URI = "/content/local/simple.html";
    public static final String LOCAL_MULTIPLE_FORMS_URI = "/content/local/multiple-forms.html";
    private static final Logger LOG = LoggerFactory.getLogger(SampleApplicationTest.class);

    @BeforeClass
    public static void setUp() throws Exception {
        ApplicationTestHelper.startKnotx();
    }

    @AfterClass
    public static void tearDown(TestContext context) {
        ApplicationTestHelper.tearDown(context);
    }

    @Test
    public void localSimpleHtmlTest(TestContext context) {
        testGetRequest(context, LOCAL_REQUEST_URI, "localSimpleResult.html");
    }

    @Test
    public void remoteSimpleHtmlTest(TestContext context) {
        testGetRequest(context, REMOTE_REQUEST_URI, "remoteSimpleResult.html");
    }

    @Test
    public void localMultipleFormWithGetTest(TestContext context) {
        testGetRequest(context, LOCAL_MULTIPLE_FORMS_URI, "multipleFormWithGetResult.html");
    }

    @Test
    public void localMultipleFormWithPostTest(TestContext context) {
        tesPostRequest(context, LOCAL_MULTIPLE_FORMS_URI, "multipleFormWithPostResult.html", false);
    }

    @Test
    public void localMultipleFormWithAjaxPostTest(TestContext context) {
        tesPostRequest(context, LOCAL_MULTIPLE_FORMS_URI, "multipleFormWithAjaxPostResult.html", true);
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

    private void tesPostRequest(TestContext context, String url, String expectedResponseFile, boolean ajaxCall) {
        HttpClient client = ApplicationTestHelper.vertx.createHttpClient();

        Async async = context.async();
        Observable<HttpClientResponse> request = KnotxRxHelper.request(client, HttpMethod.POST, ApplicationTestHelper.knotxPort, ApplicationTestHelper.knotxDomain, url, req -> {
            String bodyForm = "email=email@com.pl&name=John&_id=competition-form";
            req.headers().set("content-length", String.valueOf(bodyForm.length()));
            req.headers().set("content-type", "application/x-www-form-urlencoded");
            if (ajaxCall) {
                req.headers().set("X-Requested-With", "XMLHttpRequest");
            }
            req.write(bodyForm);
        });


        request.subscribe(resp -> resp.bodyHandler(body -> {
            context.assertEquals(resp.statusCode(), HttpResponseStatus.OK.code());
            try {
                context.assertEquals(Jsoup.parse(body.toString()).body().html(), Jsoup.parse(ApplicationTestHelper.readText(expectedResponseFile)).body().html());
            } catch (Exception e) {
                LOG.error("Cannot read file {}", expectedResponseFile, e);
                context.fail();
            }

            client.close();
            async.complete();
        }));
    }

    private void testGetRequest(TestContext context, String url, String expectedResponseFile) {
        HttpClient client = ApplicationTestHelper.vertx.createHttpClient();
        Async async = context.async();
        client.getNow(ApplicationTestHelper.knotxPort, ApplicationTestHelper.knotxDomain, url,
                resp -> resp.bodyHandler(body -> {
                    context.assertEquals(resp.statusCode(), HttpResponseStatus.OK.code());
                    try {
                        context.assertEquals(Jsoup.parse(body.toString()).html(), Jsoup.parse(ApplicationTestHelper.readText(expectedResponseFile)).html());
                    } catch (Exception e) {
                        LOG.error("Cannot read file {}", expectedResponseFile, e);
                        context.fail();
                    }
                    client.close();
                    async.complete();
                }));
    }

}
