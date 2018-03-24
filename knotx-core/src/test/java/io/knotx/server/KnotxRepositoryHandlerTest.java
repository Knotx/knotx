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
package io.knotx.server;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.server.KnotxRepositoryHandler;
import io.knotx.server.configuration.KnotxServerOptions;
import io.knotx.server.configuration.RepositoryEntry;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class KnotxRepositoryHandlerTest {

  @Mock
  private Vertx vertx;
  @Mock
  private KnotxServerOptions configuration;
  @Mock
  private ClientResponse clientResponse;
  @Mock
  private RoutingContext routingContext;
  @Mock
  private RepositoryEntry repositoryEntry;
  @Mock
  private KnotContext knotContext;
  @Mock
  private HttpServerResponse httpServerResponse;

  private KnotxRepositoryHandler tested;

  @Before
  public void setUp() {
    tested = KnotxRepositoryHandler.create(vertx, configuration);
    when(httpServerResponse.setStatusCode(anyInt())).thenReturn(httpServerResponse);
  }

  @Test
  public void handleRepositoryResponse_whenResponseIsSuccessAndShouldBeProcessed_expectContextContinuation() {
    when(clientResponse.getStatusCode()).thenReturn(200);
    when(repositoryEntry.isDoProcessing()).thenReturn(true);

    tested.handleRepositoryResponse(clientResponse, routingContext, repositoryEntry, knotContext);

    verify(knotContext, times(1)).setClientResponse(clientResponse);
    verify(routingContext, times(1)).put(KnotContext.KEY, knotContext);
    verify(routingContext, times(1)).next();
  }

  @Test
  public void handleRepositoryResponse_whenResponseIsSuccessAndShouldNotBeProcessed_expectResponse() {
    when(clientResponse.getStatusCode()).thenReturn(200);
    when(repositoryEntry.isDoProcessing()).thenReturn(false);
    when(clientResponse.getHeaders()).thenReturn(MultiMap.caseInsensitiveMultiMap());
    when(clientResponse.getBody()).thenReturn(Buffer.buffer().getDelegate());
    when(routingContext.response()).thenReturn(httpServerResponse);


    tested.handleRepositoryResponse(clientResponse, routingContext, repositoryEntry, knotContext);

    verify(routingContext, times(0)).next();
    verify(httpServerResponse, times(1)).setStatusCode(200);
    verify(httpServerResponse, times(1)).end(any(Buffer.class));
  }

  @Test
  public void handleRepositoryResponse_whenResponseIsError_expectErrorResponse() {
    when(clientResponse.getStatusCode()).thenReturn(400);
    when(clientResponse.getHeaders()).thenReturn(MultiMap.caseInsensitiveMultiMap());
    when(clientResponse.getBody()).thenReturn(Buffer.buffer().getDelegate());
    when(routingContext.response()).thenReturn(httpServerResponse);


    tested.handleRepositoryResponse(clientResponse, routingContext, repositoryEntry, knotContext);

    verify(routingContext, times(0)).next();
    verify(httpServerResponse, times(1)).setStatusCode(400);
    verify(httpServerResponse, times(1)).end(any(Buffer.class));
  }
}
