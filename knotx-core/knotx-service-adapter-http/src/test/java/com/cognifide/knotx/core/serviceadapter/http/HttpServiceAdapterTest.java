/*
 * Knot.x - Reactive microservice assembler - http service adapter
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
package com.cognifide.knotx.core.serviceadapter.http;

import com.cognifide.knotx.dataobjects.HttpResponseWrapper;
import com.cognifide.knotx.junit.FileReader;
import com.cognifide.knotx.junit.KnotxConfiguration;
import com.cognifide.knotx.junit.Logback;
import com.cognifide.knotx.junit.TestVertxDeployer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import rx.Observable;
import rx.functions.Action1;

@RunWith(VertxUnitRunner.class)
public class HttpServiceAdapterTest {

  private final static String ADAPTER_ADDRESS = "knotx.core.service-adapter";

  //Test Runner Rule of Verts
  private RunTestOnContext vertx = new RunTestOnContext();

  //Test Runner Rule of Knotx
  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  //Junit Rule, sets up logger, prepares verts, starts verticles according to the config (supplied in annotation of test method)
  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(vertx).around(knotx);

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void callNonExistingService_expectBadRequestResponse(TestContext context) {
    callAdapterServiceWithAssertions(context, "not/existing/service/address", HttpMethod.GET, httpResponseWrapper ->
        context.assertTrue(httpResponseWrapper.statusCode().equals(HttpResponseStatus.BAD_REQUEST)));
  }


  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void callExistingServiceUsingGETMethod_expectOKResponseWithServiceDataProvidedByService1(TestContext context) {
    callAdapterServiceWithAssertions(context, "/service/mock/first.json", HttpMethod.GET, httpResponseWrapper -> {
      context.assertTrue(httpResponseWrapper.statusCode().equals(HttpResponseStatus.OK));
      context.assertTrue(httpResponseWrapper.headers().isEmpty());

      try {
        JsonObject serviceResponse = new JsonObject(httpResponseWrapper.body().toString());
        JsonObject expectedResponse = new JsonObject(FileReader.readText("first-response.json"));
        context.assertEquals(serviceResponse, expectedResponse);
      } catch (Exception e) {
        context.fail(e);
      }
    });
  }

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void callExistingServiceUsingPOSTMethod_expectOKResponseWithServiceDataProvidedByService1(TestContext context) {
    callAdapterServiceWithAssertions(context, "/service/mock/first.json", HttpMethod.POST, httpResponseWrapper -> {
      context.assertTrue(httpResponseWrapper.statusCode().equals(HttpResponseStatus.OK));
      context.assertTrue(httpResponseWrapper.headers().isEmpty());

      try {
        JsonObject serviceResponse = new JsonObject(httpResponseWrapper.body().toString());
        JsonObject expectedResponse = new JsonObject(FileReader.readText("first-response.json"));
        context.assertEquals(serviceResponse, expectedResponse);
      } catch (Exception e) {
        context.fail(e);
      }
    });
  }

  private void callAdapterServiceWithAssertions(TestContext context, String servicePath, HttpMethod method, Action1<HttpResponseWrapper> testFunction) {
    JsonObject message = getPayloadMessage(servicePath, method);
    Async async = context.async();

    vertx.vertx().eventBus().<JsonObject>send(ADAPTER_ADDRESS, message, ar -> {
      if (ar.succeeded()) {
        Observable
            .just(new HttpResponseWrapper(ar.result().body()))
            .doOnNext(testFunction);
      } else {
        context.fail(ar.cause());
      }
      async.complete();
    });
  }

  private JsonObject getPayloadMessage(String servicePath, HttpMethod method) {
    return new JsonObject()
        .put("path", servicePath)
        .put("method", method);
  }

}
