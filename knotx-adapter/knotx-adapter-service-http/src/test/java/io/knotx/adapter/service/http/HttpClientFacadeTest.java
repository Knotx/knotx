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

import com.google.common.collect.Lists;
import io.knotx.adapter.common.configuration.ServiceAdapterOptions;
import io.knotx.adapter.common.configuration.ServiceSettings;
import io.knotx.adapter.common.exception.AdapterServiceContractException;
import io.knotx.adapter.common.exception.UnsupportedServiceException;
import io.knotx.adapter.common.http.HttpClientFacade;
import io.knotx.dataobjects.AdapterRequest;
import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.junit.rule.KnotxConfiguration;
import io.knotx.junit.rule.TestVertxDeployer;
import io.knotx.junit.util.FileReader;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
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
  public RuleChain chain = RuleChain.outerRule(vertx).around(knotx);

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenSupportedStaticPathServiceRequested_expectRequestExecutedAndResponseOKWithBody(
      TestContext context) throws Exception {
    Async async = context.async();
    // given
    final WebClient mockedWebClient = PowerMockito.spy(webClient());
    HttpClientFacade clientFacade = new HttpClientFacade(mockedWebClient,
        getConfiguration());
    final JsonObject expectedResponse = new JsonObject(FileReader.readText("first-response.json"));

    // when
    Single<ClientResponse> result = clientFacade
        .process(payloadMessage(REQUEST_PATH, new ClientRequest()), HttpMethod.GET);

    // then
    result
        .doOnSuccess(response -> {
          context.assertEquals(HttpResponseStatus.OK.code(), response.getStatusCode());
          context.assertEquals(expectedResponse, response.getBody().toJsonObject());
          Mockito.verify(mockedWebClient, Mockito.times(1))
              .request(HttpMethod.GET, PORT, DOMAIN, REQUEST_PATH);
        })
        .subscribe(
            response -> async.complete(),
            error -> context.fail(error.getMessage())
        );
  }

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenSupportedDynamicPathServiceRequested_expectRequestExecutedAndResponseOKWithBody(
      TestContext context) throws Exception {
    Async async = context.async();
    // given
    final WebClient mockedWebClient = PowerMockito.spy(webClient());
    HttpClientFacade clientFacade = new HttpClientFacade(mockedWebClient,
        getConfiguration());
    final JsonObject expectedResponse = new JsonObject(FileReader.readText("first-response.json"));
    final ClientRequest request = new ClientRequest()
        .setParams(MultiMap.caseInsensitiveMultiMap().add("dynamicValue", "first"));

    // when
    Single<ClientResponse> result =
        clientFacade.process(payloadMessage("/services/mock/{param.dynamicValue}.json", request),
            HttpMethod.GET);

    // then
    result
        .doOnSuccess(response -> {
          context.assertEquals(HttpResponseStatus.OK.code(), response.getStatusCode());
          context.assertEquals(expectedResponse, response.getBody().toJsonObject());
          Mockito.verify(mockedWebClient, Mockito.times(1))
              .request(HttpMethod.GET, PORT, DOMAIN, REQUEST_PATH);
        })
        .subscribe(
            response -> async.complete(),
            error -> context.fail(error.getMessage())
        );
  }

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenServiceRequestedWithoutPathParam_expectNoServiceRequestAndBadRequest(
      TestContext context) throws Exception {
    Async async = context.async();
    // given
    final WebClient mockedWebClient = PowerMockito.spy(webClient());
    HttpClientFacade clientFacade = new HttpClientFacade(mockedWebClient,
        getConfiguration());

    // when
    Single<ClientResponse> result = clientFacade.process(new AdapterRequest(), HttpMethod.GET);

    // then
    result
        .doOnError(error -> {
          context.assertEquals(error.getClass().getSimpleName(),
              AdapterServiceContractException.class.getSimpleName());
          Mockito.verify(mockedWebClient, Mockito.times(0))
              .request(Matchers.any(), Matchers.anyInt(), Matchers.anyString(),
                  Matchers.anyString());
        })
        .subscribe(
            response -> context.fail("Error should occur!"),
            error -> async.complete());
  }

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenUnsupportedPathServiceRequested_expectNoServiceRequestAndBadRequest(
      TestContext context) throws Exception {
    Async async = context.async();
    // given
    final WebClient mockedWebClient = PowerMockito.spy(webClient());
    HttpClientFacade clientFacade = new HttpClientFacade(mockedWebClient,
        getConfiguration());

    // when
    Single<ClientResponse> result =
        clientFacade
            .process(payloadMessage("/not/supported/path", new ClientRequest()), HttpMethod.GET);

    // then
    result
        .doOnError(error -> {
          context.assertEquals(UnsupportedServiceException.class, error.getClass());
          Mockito.verify(mockedWebClient, Mockito.times(0))
              .request(Matchers.any(), Matchers.anyInt(), Matchers.anyString(),
                  Matchers.anyString());
        })
        .subscribe(
            response -> context.fail("Error should occur!"),
            error -> async.complete()
        );
  }

  @Test
  @KnotxConfiguration("knotx-service-adapter-http-test.json")
  public void whenServiceEmptyResponse_expectNoFailure(
      TestContext context) throws Exception {
    Async async = context.async();
    // given
    final WebClient mockedWebClient = PowerMockito.spy(webClient());
    HttpClientFacade clientFacade = new HttpClientFacade(mockedWebClient,
        getConfiguration());

    // when
    Single<ClientResponse> result = clientFacade
        .process(payloadMessage("/services/mock/empty.json", new ClientRequest()), HttpMethod.GET);

    // then
    result
        .doOnSuccess(response -> {
          context.assertEquals(HttpResponseStatus.OK.code(), response.getStatusCode());
          context.assertEquals(0, Integer.valueOf(response.getHeaders().get("Content-Length")));
          Mockito.verify(mockedWebClient, Mockito.times(1))
              .request(HttpMethod.GET, PORT, DOMAIN, "/services/mock/empty.json");
        })
        .subscribe(
            response -> async.complete(),
            error -> context.fail(error.getMessage())
        );
  }

  private WebClient webClient() {
    return WebClient.create(Vertx.newInstance(vertx.vertx()));
  }

  private AdapterRequest payloadMessage(String servicePath, ClientRequest request) {
    return new AdapterRequest().setRequest(request)
        .setParams(new JsonObject().put("path", servicePath));
  }

  private ServiceAdapterOptions getConfiguration() {
    return new ServiceAdapterOptions().setServices(getServiceConfigurations());
  }

  private List<ServiceSettings> getServiceConfigurations() {
    return Lists.newArrayList(
        new ServiceSettings()
            .setPort(PORT)
            .setDomain(DOMAIN)
            .setPath(PATH)
            .setAllowedRequestHeaderPatterns(PATTERNS));
  }

}
