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
package io.knotx.adapter.service.http;

import io.knotx.adapter.common.exception.AdapterServiceContractException;
import io.knotx.adapter.common.exception.UnsupportedServiceException;
import io.knotx.adapter.common.http.HttpClientFacade;
import io.knotx.adapter.common.http.ServiceMetadata;
import io.knotx.dataobjects.AdapterRequest;
import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.junit.rule.KnotxConfiguration;
import io.knotx.junit.rule.Logback;
import io.knotx.junit.rule.TestVertxDeployer;
import io.knotx.junit.util.FileReader;
import com.google.common.collect.Lists;
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
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class HttpClientFacadeTest {

  // Configuration

  private static final Integer PORT = 3000;

  private static final String DOMAIN = "localhost";

  private static final String PATH = "/services/mock.*";

  // Request payload

  private static final String REQUEST_PATH = "/services/mock/first.json";

  private static final List<Pattern> PATTERNS = Collections
      .singletonList(Pattern.compile("X-test*"));

  private RunTestOnContext vertx = new RunTestOnContext();

  private TestVertxDeployer knotx = new TestVertxDeployer(vertx);

  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(vertx).around(knotx);

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenSupportedStaticPathServiceRequested_expectRequestExecutedAndResponseOKWithBody(
      TestContext context) throws Exception {
    Async async = context.async();
    // given
    final HttpClient httpClient = PowerMockito.spy(httpClient());
    HttpClientFacade clientFacade = new HttpClientFacade(httpClient, getServiceConfigurations());
    final JsonObject expectedResponse = new JsonObject(FileReader.readText("first-response.json"));

    // when
    Observable<ClientResponse> result = clientFacade
        .process(payloadMessage(REQUEST_PATH, new ClientRequest()), HttpMethod.GET);

    // then
    result.subscribe(
        response -> {
          context.assertEquals(HttpResponseStatus.OK.code(), response.getStatusCode());
          context.assertEquals(expectedResponse, response.getBody().toJsonObject());
          Mockito.verify(httpClient, Mockito.times(1))
              .request(HttpMethod.GET, 3000, "localhost", REQUEST_PATH);
        },
        error -> context.fail(error.getMessage()),
        async::complete);
  }

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenSupportedDynamicPathServiceRequested_expectRequestExecutedAndResponseOKWithBody(
      TestContext context) throws Exception {
    Async async = context.async();
    // given
    final HttpClient httpClient = PowerMockito.spy(httpClient());
    HttpClientFacade clientFacade = new HttpClientFacade(httpClient, getServiceConfigurations());
    final JsonObject expectedResponse = new JsonObject(FileReader.readText("first-response.json"));
    final ClientRequest request = new ClientRequest()
        .setParams(MultiMap.caseInsensitiveMultiMap().add("dynamicValue", "first"));

    // when
    Observable<ClientResponse> result =
        clientFacade.process(payloadMessage("/services/mock/{param.dynamicValue}.json", request),
            HttpMethod.GET);

    // then
    result.subscribe(
        response -> {
          context.assertEquals(HttpResponseStatus.OK.code(), response.getStatusCode());
          context.assertEquals(expectedResponse, response.getBody().toJsonObject());
          Mockito.verify(httpClient, Mockito.times(1))
              .request(HttpMethod.GET, 3000, "localhost", REQUEST_PATH);
        },
        error -> context.fail(error.getMessage()),
        async::complete);
  }

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenServiceRequestedWithoutPathParam_expectNoServiceRequestAndBadRequest(
      TestContext context) throws Exception {
    Async async = context.async();
    // given
    HttpClient mockedHttpClient = Mockito.mock(HttpClient.class);
    HttpClientFacade clientFacade = new HttpClientFacade(mockedHttpClient,
        getServiceConfigurations());

    // when
    Observable<ClientResponse> result = clientFacade.process(new AdapterRequest(), HttpMethod.GET);

    // then
    result.subscribe(
        response -> context.fail("Error should occur!"),
        error -> {
          {
            context.assertEquals(error.getClass().getSimpleName(),
                AdapterServiceContractException.class.getSimpleName());
            Mockito.verify(mockedHttpClient, Mockito.times(0))
                .request(Matchers.any(), Matchers.anyInt(), Matchers.anyString(),
                    Matchers.anyString());
            async.complete();
          }
        },
        async::complete);
  }

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenUnsupportedPathServiceRequested_expectNoServiceRequestAndBadRequest(
      TestContext context) throws Exception {
    Async async = context.async();
    // given
    HttpClient mockedHttpClient = Mockito.mock(HttpClient.class);
    HttpClientFacade clientFacade = new HttpClientFacade(mockedHttpClient,
        getServiceConfigurations());

    // when
    Observable<ClientResponse> result =
        clientFacade
            .process(payloadMessage("/not/supported/path", new ClientRequest()), HttpMethod.GET);

    // then
    result.subscribe(
        response -> context.fail("Error should occur!"),
        error -> {
          {
            context.assertEquals(UnsupportedServiceException.class, error.getClass());
            Mockito.verify(mockedHttpClient, Mockito.times(0))
                .request(Matchers.any(), Matchers.anyInt(), Matchers.anyString(),
                    Matchers.anyString());
            async.complete();
          }
        },
        async::complete);
  }

  private HttpClient httpClient() {
    return Vertx.newInstance(vertx.vertx()).createHttpClient();
  }

  private AdapterRequest payloadMessage(String servicePath, ClientRequest request) {
    return new AdapterRequest().setRequest(request)
        .setParams(new JsonObject().put("path", servicePath));
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
