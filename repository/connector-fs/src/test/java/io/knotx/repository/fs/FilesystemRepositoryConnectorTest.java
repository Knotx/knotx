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
package io.knotx.repository.fs;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import io.knotx.junit5.util.RequestUtil;
import io.knotx.server.api.context.ClientRequest;
import io.knotx.server.api.context.ClientResponse;
import io.knotx.server.api.context.RequestEvent;
import io.knotx.server.api.handler.RequestEventResult;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import java.util.Optional;
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
class FilesystemRepositoryConnectorTest {

  @Mock
  private ClientRequest clientRequest;
  private RequestEvent requestEvent;

  @BeforeEach
  void setUp() {
    requestEvent = new RequestEvent(clientRequest);
  }

  @Test
  void process_whenFileDoesNotExits_expectNotFoundStatus(VertxTestContext testContext,
      Vertx vertx) {
    //given
    final String filePath = "not-existing-file";
    when(clientRequest.getPath()).thenReturn(filePath);

    //when
    FilesystemRepositoryConnector connector = new FilesystemRepositoryConnector(
        vertx.fileSystem(), new FilesystemRepositoryOptions());
    Single<RequestEventResult> connectorResult = connector.process(requestEvent);

    //then
    RequestUtil.subscribeToResult_shouldSucceed(testContext, connectorResult,
        result -> assertEquals(HttpResponseStatus.NOT_FOUND.code(),
            result.getClientResponse().getStatusCode())
    );
  }

  @Test
  void process_whenFileExists_expectFileContentInClientResponseBodyAndOKStatus(
      VertxTestContext testContext, Vertx vertx) {
    //given
    final String filePath = "hello.txt";
    when(clientRequest.getPath()).thenReturn(filePath);

    //when
    FilesystemRepositoryConnector connector = new FilesystemRepositoryConnector(
        vertx.fileSystem(), new FilesystemRepositoryOptions());
    Single<RequestEventResult> connectorResult = connector.process(requestEvent);

    //then
    RequestUtil.subscribeToResult_shouldSucceed(testContext, connectorResult,
        result -> {
          final Optional<RequestEvent> requestEvent = result.getRequestEvent();
          assertTrue(requestEvent.isPresent());
          ClientResponse clientResponse = result.getClientResponse();
          assertEquals(HttpResponseStatus.OK.code(), clientResponse.getStatusCode());
          final ClientResponse repositoryResponse = new ClientResponse(requestEvent.get().getPayload()
              .getJsonObject("repositoryResponse"));
          assertEquals("hello", repositoryResponse.getBody().toString().trim());
          assertEquals("text/plain", repositoryResponse.getHeaders().get("Content-Type"));
        }
    );
  }

}
