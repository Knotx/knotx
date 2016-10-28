/*
 * Knot.x - Reactive microservice assembler - Http Service Adapter
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
package com.cognifide.knotx.adapter.service.http;

import com.google.common.collect.Lists;

import com.cognifide.knotx.adapter.common.exception.UnsupportedServiceException;
import com.cognifide.knotx.adapter.common.http.HttpClientFacade;
import com.cognifide.knotx.adapter.common.http.ServiceMetadata;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.junit.FileReader;
import com.cognifide.knotx.junit.KnotxConfiguration;
import com.cognifide.knotx.junit.Logback;
import com.cognifide.knotx.junit.TestVertxDeployer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class HttpClientFacadeTest {

  // Configuration

  private static final Integer PORT = 3000;

  private static final String DOMAIN = "localhost";

  private static final String PATH = "/services/mock.*";

  // Request payload

  private static final String REQUEST_PATH = "/services/mock/first.json";

  private static final List<Pattern> PATTERNS = Collections.singletonList(Pattern.compile("X-test*"));

  private RunTestOnContext vertx = new RunTestOnContext();

  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(vertx).around(knotx);

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenSupportedStaticPathServiceRequested_expectRequestExecutedAndResponseOKWithBody(TestContext context) throws Exception {
    Async async = context.async();
    // given
    final HttpClient httpClient = PowerMockito.spy(httpClient());
    HttpClientFacade clientFacade = new HttpClientFacade(httpClient, getServiceConfigurations());
    final JsonObject expectedResponse = new JsonObject(FileReader.readText("first-response.json"));

    // when
    Observable<ClientResponse> result = clientFacade
        .process(payloadMessage(REQUEST_PATH, new JsonObject()), HttpMethod.GET);

    // then
    result.subscribe(
        response -> {
          context.assertEquals(HttpResponseStatus.OK, response.statusCode());
          context.assertEquals(expectedResponse, response.body().toJsonObject());
          Mockito.verify(httpClient, Mockito.times(1)).request(HttpMethod.GET, 3000, "localhost", REQUEST_PATH);
        },
        error -> context.fail(error.getMessage()),
        async::complete);
  }

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenSupportedDynamicPathServiceRequested_expectRequestExecutedAndResponseOKWithBody(TestContext context) throws Exception {
    Async async = context.async();
    // given
    final HttpClient httpClient = PowerMockito.spy(httpClient());
    HttpClientFacade clientFacade = new HttpClientFacade(httpClient, getServiceConfigurations());
    final JsonObject expectedResponse = new JsonObject(FileReader.readText("first-response.json"));
    final JsonObject request = new JsonObject().put("params", new JsonArray().add(new JsonObject().put("dynamicValue", "first")));

    // when
    Observable<ClientResponse> result =
        clientFacade.process(payloadMessage("/services/mock/{param.dynamicValue}.json", request), HttpMethod.GET);

    // then
    result.subscribe(
        response -> {
          context.assertEquals(HttpResponseStatus.OK, response.statusCode());
          context.assertEquals(expectedResponse, response.body().toJsonObject());
          Mockito.verify(httpClient, Mockito.times(1)).request(HttpMethod.GET, 3000, "localhost", REQUEST_PATH);
        },
        error -> context.fail(error.getMessage()),
        async::complete);
  }

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenServiceRequestedWithoutPathParam_expectNoServiceRequestAndBadRequest(TestContext context) throws Exception {
    Async async = context.async();
    // given
    HttpClient mockedHttpClient = Mockito.mock(HttpClient.class);
    HttpClientFacade clientFacade = new HttpClientFacade(mockedHttpClient, getServiceConfigurations());

    // when
    Observable<ClientResponse> result = clientFacade.process(new JsonObject()
            .put("params", new JsonObject())
            .put("clientRequest", new JsonObject())
        , HttpMethod.GET);

    // then
    result.subscribe(
        response -> context.fail("Error should occur!"),
        error -> {
          {
            context.assertEquals("Parameter `path` was not defined in `params`!", error.getMessage());
            Mockito.verify(mockedHttpClient, Mockito.times(0)).request(Matchers.any(), Matchers.anyInt(), Matchers.anyString(), Matchers.anyString());
            async.complete();
          }
        },
        async::complete);
  }

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenUnsupportedPathServiceRequested_expectNoServiceRequestAndBadRequest(TestContext context) throws Exception {
    Async async = context.async();
    // given
    HttpClient mockedHttpClient = Mockito.mock(HttpClient.class);
    HttpClientFacade clientFacade = new HttpClientFacade(mockedHttpClient, getServiceConfigurations());

    // when
    Observable<ClientResponse> result =
        clientFacade.process(payloadMessage("/not/supported/path", new JsonObject()), HttpMethod.GET);

    // then
    result.subscribe(
        response -> context.fail("Error should occur!"),
        error -> {
          {
            context.assertEquals(UnsupportedServiceException.class, error.getClass());
            Mockito.verify(mockedHttpClient, Mockito.times(0)).request(Matchers.any(), Matchers.anyInt(), Matchers.anyString(), Matchers.anyString());
            async.complete();
          }
        },
        async::complete);
  }

  private HttpClient httpClient() {
    return Vertx.newInstance(vertx.vertx()).createHttpClient();
  }

  private JsonObject payloadMessage(String servicePath, JsonObject request) {
    return new JsonObject()
        .put("params", new JsonObject()
            .put("path", servicePath))
        .put("clientRequest", request);
  }

  private List<ServiceMetadata> getServiceConfigurations() {
    return Lists.newArrayList(
        new ServiceMetadata()
            .setPort(PORT)
            .setDomain(DOMAIN)
            .setPath(PATH)
            .setAllowedRequestHeaderPatterns(PATTERNS));
  }

}
