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

import com.google.common.collect.Maps;

import com.cognifide.knotx.junit.FileReader;
import com.cognifide.knotx.junit.KnotxConfiguration;
import com.cognifide.knotx.junit.Logback;
import com.cognifide.knotx.junit.TestVertxDeployer;

import org.jsoup.Jsoup;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.Map;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;
import rx.functions.Action1;


@RunWith(VertxUnitRunner.class)
public class SampleApplicationTest {

  private static final String REMOTE_REQUEST_URI = "/content/remote/simple.html";
  private static final String LOCAL_REQUEST_URI = "/content/local/simple.html";
  private static final String LOCAL_MULTIPLE_FORMS_URI = "/content/local/multiple-forms.html";
  private static final int KNOTX_SERVER_PORT = 8092;
  private static final String KNOTX_SERVER_ADDRESS = "localhost";

  private RunTestOnContext vertx = new RunTestOnContext();

  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(vertx).around(knotx);

  private static Observable<HttpClientResponse> request(HttpClient client, HttpMethod method, int port, String domain, String uri, Action1<HttpClientRequest> requestBuilder) {
    return Observable.create(subscriber -> {
      HttpClientRequest req = client.request(method, port, domain, uri);
      Observable<HttpClientResponse> resp = req.toObservable();
      resp.subscribe(subscriber);
      requestBuilder.call(req);
      req.end();
    });
  }

  @Test
  @KnotxConfiguration("knotx-example-monolith.json")
  public void whenRequestingLocalSimplePageWithGet_expectLocalSimpleHtml(TestContext context) {
    testGetRequest(context, LOCAL_REQUEST_URI, "localSimpleResult.html");
  }

  @Test
  @KnotxConfiguration("knotx-example-monolith.json")
  public void whenRequestingRemoteSimplePageWithGet_expectRemoteSimpleHtml(TestContext context) {
    testGetRequest(context, REMOTE_REQUEST_URI, "remoteSimpleResult.html");
  }

  @Test
  @KnotxConfiguration("knotx-example-monolith.json")
  public void whenRequestingLocalMultipleFormsPageWithGet_expectMutlipleFormsWithGetResultHtml(TestContext context) {
    testGetRequest(context, LOCAL_MULTIPLE_FORMS_URI, "multipleFormWithGetResult.html");
  }

  @Test
  @KnotxConfiguration("knotx-example-monolith.json")
  public void whenRequestingWithPostMethodFirstForm_expectFirstFormPresentingFormActionResult(TestContext context) {
    testPostRequest(context, LOCAL_MULTIPLE_FORMS_URI, getFirstTestFormData(), "multipleFormWithPostResult.html", false);
  }

  @Ignore
  @Test
  @KnotxConfiguration("knotx-example-monolith.json")
  public void whenRequestingWithPostFirstFormTwiceWithDifferentData_expectDifferentResultOfFirstFormForEachRequest(TestContext context) {
    testPostRequest(context, LOCAL_MULTIPLE_FORMS_URI, getFirstTestFormData(), "multipleFormWithPostResult.html", false);
    testPostRequest(context, LOCAL_MULTIPLE_FORMS_URI, getSecondTestFormData(), "multipleFormWithPostResult2.html", false);
  }

  private void testPostRequest(TestContext context, String url, Map<String, String> formData, String expectedResponseFile, boolean ajaxCall) {
    HttpClient client = Vertx.newInstance(vertx.vertx()).createHttpClient();

    Async async = context.async();
    Observable<HttpClientResponse> request = request(client, HttpMethod.POST, KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, url, req -> {
      String bodyForm = formData.entrySet().stream()
          .map(entry -> entry.getKey() + "=" + entry.getValue())
          .reduce((p1, p2) -> p1 + "&" + p2).get();
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
        context.assertEquals(Jsoup.parse(body.toString()).body().html(), Jsoup.parse(FileReader.readText(expectedResponseFile)).body().html());
      } catch (Exception e) {
        context.fail(e);
      }

      async.complete();
    }));
  }

  private void testGetRequest(TestContext context, String url, String expectedResponseFile) {
    HttpClient client = Vertx.newInstance(vertx.vertx()).createHttpClient();
    Async async = context.async();
    client.getNow(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, url,
        resp -> resp.bodyHandler(body -> {
          context.assertEquals(resp.statusCode(), HttpResponseStatus.OK.code());
          try {
            context.assertEquals(Jsoup.parse(body.toString()).body().html().trim(),
                Jsoup.parse(FileReader.readText(expectedResponseFile)).body().html().trim());
          } catch (Exception e) {
            context.fail(e);
          }
          client.close();
          async.complete();
        }));
  }

  private Map<String, String> getFirstTestFormData() {
    Map<String, String> data = Maps.newHashMap();
    data.put("name", "test");
    data.put("email", "email@example.com");
    data.put("_frmId", "competition");

    return data;
  }

  private Map<String, String> getSecondTestFormData() {
    Map<String, String> data = Maps.newHashMap();
    data.put("name", "test");
    data.put("email", "email@example.com");
    data.put("_frmId", "competition");

    return data;
  }
}
