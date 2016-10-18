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

import com.google.common.collect.Lists;

import com.cognifide.knotx.dataobjects.HttpResponseWrapper;
import com.cognifide.knotx.junit.KnotxConfiguration;
import com.cognifide.knotx.junit.Logback;
import com.cognifide.knotx.junit.TestVertxDeployer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(VertxUnitRunner.class)
public class HttpClientFacadeTest {

  // Configuration

  private static final Integer PORT = 3000;

  private static final String DOMAIN = "localhost";

  private static final String PATH = "/services/mock.*";

  // Request payload

  private static final String REQUEST_PATH = "/services/mock/first.json";

  private static final HttpMethod REQUEST_METHOD = HttpMethod.GET;

  private static final List<Pattern> patterns = Collections.singletonList(Pattern.compile("X-test*"));

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
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void basicTest_expectResponseOK(TestContext context) throws Exception {
    Async async = context.async();
    // given
    HttpClientFacade clientFacade = spy(new HttpClientFacade(httpClient(), getServiceConfigurations()));

    // when
    Observable<HttpResponseWrapper> result = clientFacade.process(payloadMessage(REQUEST_PATH, REQUEST_METHOD));

    // then
    result.subscribe(
        response -> {
          assertEquals(response.statusCode(), HttpResponseStatus.OK);
        },
        error -> {
          context.fail(error.getMessage());
        },
        () -> async.complete());
  }

  private HttpClient httpClient() {
    return Vertx.newInstance(vertx.vertx()).createHttpClient();
  }

  private JsonObject payloadMessage(String servicePath, HttpMethod method) {
    return new JsonObject()
        .put("params", new JsonObject()
            .put("path", servicePath)
            .put("method", method))
        .put("request", mockHttpRequest());
  }

  private JsonObject mockHttpRequest() {
    return new JsonObject()
        .put("method", HttpMethod.GET);
  }

  private List<HttpServiceAdapterConfiguration.ServiceMetadata> getServiceConfigurations() {
    return Lists.newArrayList(
        new HttpServiceAdapterConfiguration.ServiceMetadata()
            .setPort(PORT)
            .setDomain(DOMAIN)
            .setPath(PATH)
            .setAllowedRequestHeaderPatterns(patterns));
  }

}
