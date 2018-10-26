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
package io.knotx.server.handler.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.server.handler.repository.RepositoryRoutingHandlerFactory.KnotxRepositoryHandler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class KnotxRepositoryHandlerTest {

  @Mock
  private Vertx vertx;
  @Mock
  private JsonObject configuration;
  @Mock
  private ClientResponse clientResponse;
  @Mock
  private RoutingContext routingContext;
  @Mock
  private KnotContext knotContext;
  @Mock
  private HttpServerResponse httpServerResponse;

  private KnotxRepositoryHandler tested;

  @BeforeEach
  public void setUp() {
    tested = new RepositoryRoutingHandlerFactory().create(vertx, configuration);
    when(httpServerResponse.setStatusCode(anyInt())).thenReturn(httpServerResponse);
  }

  @Test
  public void handleRepositoryResponse_whenResponseIsSuccessAndShouldBeProcessed_expectContextContinuation() {
    when(clientResponse.getStatusCode()).thenReturn(200);
    when(routingContext.response()).thenReturn(httpServerResponse);

    tested.handleRepositoryResponse(clientResponse, routingContext, knotContext);
    verify(routingContext, times(1)).next();
  }

  @Test
  public void handleRepositoryResponse_whenResponseIsError_expectErrorResponse() {
    when(clientResponse.getStatusCode()).thenReturn(400);
    when(clientResponse.getHeaders()).thenReturn(MultiMap.caseInsensitiveMultiMap());
    when(clientResponse.getBody()).thenReturn(Buffer.buffer().getDelegate());
    when(routingContext.response()).thenReturn(httpServerResponse);

    tested.handleRepositoryResponse(clientResponse, routingContext, knotContext);

    verify(routingContext, times(0)).next();
    verify(httpServerResponse, times(1)).setStatusCode(400);
    verify(httpServerResponse, times(1)).end(any(Buffer.class));
  }
}
