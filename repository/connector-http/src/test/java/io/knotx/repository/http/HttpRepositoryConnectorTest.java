/*
 * Copyright (C) 2019 Cognifide Limited
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
package io.knotx.repository.http;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.knotx.junit5.util.RequestUtil;
import io.knotx.server.api.context.ClientRequest;
import io.knotx.server.api.context.ClientResponse;
import io.knotx.server.api.context.RequestEvent;
import io.knotx.server.api.handler.RequestEventHandlerResult;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class HttpRepositoryConnectorTest {

  @Mock
  private ClientRequest clientRequest;

  private RequestEvent requestEvent;
  private WireMockServer wireMockServer;
  private HttpRepositoryOptions httpRepositoryOptions;

  @BeforeEach
  void setUp() {
    requestEvent = new RequestEvent(clientRequest);
    this.wireMockServer = new WireMockServer(options().dynamicPort());
    this.wireMockServer.start();

    httpRepositoryOptions = new HttpRepositoryOptions();
    httpRepositoryOptions.setClientDestination(new ClientDestination()
        .setScheme("http")
        .setPort(wireMockServer.port())
        .setDomain("localhost"));
  }

  @Test
  void process_whenPathNotExists_expectNotFoundStatus(VertxTestContext testContext, Vertx vertx) {
    //given
    final String requestPath = "/non-existing-template.html";
    when(clientRequest.getPath()).thenReturn(requestPath);
    when(clientRequest.getHeaders()).thenReturn(MultiMap.caseInsensitiveMultiMap());

    wireMockServer.stubFor(get(urlEqualTo(requestPath))
        .willReturn(aResponse()
            .withHeader("Content-Type", "text/html")
            .withStatus(HttpResponseStatus.NOT_FOUND.code())));

    //when
    HttpRepositoryConnector connector = new HttpRepositoryConnector(vertx, httpRepositoryOptions);
    Single<RequestEventHandlerResult> connectorResult = connector.process(requestEvent);

    //then
    RequestUtil.subscribeToResult_shouldSucceed(testContext, connectorResult,
        result -> {
          assertEquals(HttpResponseStatus.NOT_FOUND.code(),
              result.getClientResponse().getStatusCode());
          this.wireMockServer.stop();
        }
    );
  }

  @Test
  void process_whenTemplateExists_expectContentInBodyAndOkStatus(VertxTestContext testContext,
      Vertx vertx) {
    //given
    final String requestPath = "/test-template.html";
    final String body = "This template exists!";
    when(clientRequest.getPath()).thenReturn(requestPath);
    when(clientRequest.getHeaders()).thenReturn(MultiMap.caseInsensitiveMultiMap());

    wireMockServer.stubFor(get(urlEqualTo(requestPath))
        .willReturn(aResponse()
            .withHeader("Content-Type", "text/html")
            .withHeader("TestName", "Test Value")
            .withStatus(HttpResponseStatus.OK.code())
            .withBody(body)));

    //when
    HttpRepositoryConnector connector = new HttpRepositoryConnector(vertx, httpRepositoryOptions);
    Single<RequestEventHandlerResult> connectorResult = connector.process(requestEvent);

    //then
    RequestUtil.subscribeToResult_shouldSucceed(testContext, connectorResult,
        result -> {
          final ClientResponse clientResponse = result.getClientResponse();
          assertEquals(HttpResponseStatus.OK.code(), clientResponse.getStatusCode());

          assertTrue(result.getRequestEvent().isPresent());
          final ClientResponse repositoryResponse = getRepositoryResponse(result);
          assertEquals(body, repositoryResponse.getBody().toString());
          assertEquals("text/html", repositoryResponse.getHeaders().get("Content-Type"));
          assertEquals("Test Value", repositoryResponse.getHeaders().get("TestName"));
          this.wireMockServer.stop();
        }
    );
  }

  @Test
  void process_whenEmptyTemplate_expectStatusOKAndEmptyBody(VertxTestContext testContext,
      Vertx vertx) {
    //given
    final String requestPath = "/empty-body.html";
    when(clientRequest.getPath()).thenReturn(requestPath);
    when(clientRequest.getHeaders()).thenReturn(MultiMap.caseInsensitiveMultiMap());

    wireMockServer.stubFor(get(urlEqualTo(requestPath))
        .willReturn(aResponse()
            .withHeader("Content-Type", "text/html")
            .withStatus(HttpResponseStatus.OK.code())
            .withBody(StringUtils.EMPTY)));

    //when
    HttpRepositoryConnector connector = new HttpRepositoryConnector(vertx, httpRepositoryOptions);
    Single<RequestEventHandlerResult> connectorResult = connector.process(requestEvent);

    //then
    RequestUtil.subscribeToResult_shouldSucceed(testContext, connectorResult,
        result -> {
          final ClientResponse clientResponse = result.getClientResponse();
          assertEquals(HttpResponseStatus.OK.code(), clientResponse.getStatusCode());

          assertTrue(result.getRequestEvent().isPresent());
          final ClientResponse repositoryResponse = getRepositoryResponse(result);
          assertTrue(repositoryResponse.getBody().toString().isEmpty());
          assertEquals("text/html", repositoryResponse.getHeaders().get("Content-Type"));
          this.wireMockServer.stop();
        }
    );
  }

  @Test
  void process_whenRedirect_expectRedirectStatusAndEmptyBody(VertxTestContext testContext,
      Vertx vertx) {
    //given
    final String requestPath = "/redirect.html";
    when(clientRequest.getPath()).thenReturn(requestPath);
    when(clientRequest.getHeaders()).thenReturn(MultiMap.caseInsensitiveMultiMap());

    wireMockServer.stubFor(get(urlEqualTo(requestPath))
        .willReturn(aResponse()
            .withStatus(HttpResponseStatus.TEMPORARY_REDIRECT.code())));

    //when
    HttpRepositoryConnector connector = new HttpRepositoryConnector(vertx, httpRepositoryOptions);
    Single<RequestEventHandlerResult> connectorResult = connector.process(requestEvent);

    //then
    RequestUtil.subscribeToResult_shouldSucceed(testContext, connectorResult,
        result -> {
          final ClientResponse clientResponse = result.getClientResponse();
          assertEquals(HttpResponseStatus.OK.code(), clientResponse.getStatusCode());

          assertTrue(result.getRequestEvent().isPresent());
          final ClientResponse repositoryResponse = getRepositoryResponse(result);
          assertEquals(HttpResponseStatus.TEMPORARY_REDIRECT.code(),
              repositoryResponse.getStatusCode());
          assertTrue(repositoryResponse.getBody().toString().isEmpty());
          this.wireMockServer.stop();
        }
    );
  }

  @Test
  void process_whenServerError_expectServerErrorStatusAndEmptyBody(VertxTestContext testContext,
      Vertx vertx) {
    //given
    final String requestPath = "/500.html";
    when(clientRequest.getPath()).thenReturn(requestPath);
    when(clientRequest.getHeaders()).thenReturn(MultiMap.caseInsensitiveMultiMap());

    wireMockServer.stubFor(get(urlEqualTo(requestPath))
        .willReturn(aResponse()
            .withStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())));

    //when
    HttpRepositoryConnector connector = new HttpRepositoryConnector(vertx, httpRepositoryOptions);
    Single<RequestEventHandlerResult> connectorResult = connector.process(requestEvent);

    //then
    RequestUtil.subscribeToResult_shouldSucceed(testContext, connectorResult,
        result -> {
          final ClientResponse clientResponse = result.getClientResponse();
          assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), clientResponse.getStatusCode());

          assertFalse(result.getRequestEvent().isPresent());
          this.wireMockServer.stop();
        }
    );
  }

  private ClientResponse getRepositoryResponse(RequestEventHandlerResult result) {
    return new ClientResponse(
        result.getRequestEvent().get().getPayload().getJsonObject("repositoryResponse"));
  }

}
