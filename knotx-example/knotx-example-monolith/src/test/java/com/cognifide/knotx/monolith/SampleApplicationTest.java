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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;


@RunWith(VertxUnitRunner.class)
public class SampleApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(SampleApplicationTest.class);

    @BeforeClass
    public static void setUp() throws Exception {
        ApplicationTestHelper.startKnotx();
    }

    @Test
    public void localSimpleHtmlTest(TestContext context) {
        testGetRequest(context, "/content/local/simple.html", "localSimpleResult.html");

    }

    @Test
    public void remoteSimpleHtmlTest(TestContext context) {
        testGetRequest(context, "/content/remote/simple.html", "remoteSimpleResult.html");

    }

    @Test
    public void localMultipleFormWithGetTest(TestContext context) {
        testGetRequest(context, "/content/local/multiple-forms.html", "multipleFormWithGetResult.html");
    }


    @Test
    public void localMultipleFormWithPostTest(TestContext context) {
        tesPostRequest(context, "/content/local/multiple-forms.html", "multipleFormWithPostResult.html", false);
    }

    @Test
    @Ignore
    public void localMultipleFormWithAjaxPostTest(TestContext context) {
        tesPostRequest(context, "/content/local/multiple-forms.html", "multipleFormWithAjaxPostResult.html", true);
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
            context.assertEquals(resp.statusCode(), 200);
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

    private void testGetRequest(TestContext context, String url, String expectedResponseFile) {
        HttpClient client = ApplicationTestHelper.vertx.createHttpClient();
        Async async = context.async();
        client.getNow(ApplicationTestHelper.knotxPort, ApplicationTestHelper.knotxDomain, url,
                resp -> resp.bodyHandler(body -> {
                    context.assertEquals(resp.statusCode(), 200);
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

    @AfterClass
    public static void tearDown(TestContext context) {
        ApplicationTestHelper.tearDown(context);
    }

}
