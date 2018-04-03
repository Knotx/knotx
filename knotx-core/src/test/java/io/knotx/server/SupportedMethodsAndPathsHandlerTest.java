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


import io.knotx.junit.util.FileReader;
import io.knotx.server.configuration.KnotxFlowSettings;
import io.knotx.server.configuration.KnotxServerOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SupportedMethodsAndPathsHandlerTest {

  private KnotxFlowSettings defaultFlow;
  private KnotxFlowSettings customFlow;
  private SupportedMethodsAndPathsHandler tested;

  @Before
  public void before() throws Exception {
    defaultFlow = new KnotxFlowSettings(
        new JsonObject(FileReader.readText("io/knotx/server/GETOnlyDefaultFlow.json")));
    customFlow = new KnotxFlowSettings(
        new JsonObject(FileReader.readText("io/knotx/server/GETOnlyCustomFlow.json")));
    KnotxServerOptions configuration = Mockito.mock(KnotxServerOptions.class);
    Mockito.when(configuration.getDefaultFlow()).thenReturn(defaultFlow);
    Mockito.when(configuration.getCustomFlow()).thenReturn(customFlow);

    tested = SupportedMethodsAndPathsHandler.create(configuration);
  }

  @Test
  public void handle_whenRequestPathAndMethodAllowedInDefaultFlow_expectRequestAccepted()
      throws Exception {
    RoutingContext context = mockContext("/content/page.html", HttpMethod.GET);
    tested.handle(context);

    Mockito.verify(context, Mockito.times(1)).next();
  }

  @Test
  public void handle_whenRequestPathNotAllowedInDefaultFlowAndAllowedInCustomFlow_expectRequestAccepted()
      throws Exception {
    RoutingContext context = mockContext("/services/feature.json", HttpMethod.GET);
    tested.handle(context);

    Mockito.verify(context, Mockito.times(1)).next();
  }

  @Test
  public void handle_whenRequestPathAllowedButMethodNotAllowedInDefaultFlow_expectRequestFailedMethodNotAllowed()
      throws Exception {
    RoutingContext context = mockContext("/content/page.html", HttpMethod.POST);
    tested.handle(context);

    Mockito.verify(context, Mockito.times(1)).fail(405);
  }

  @Test
  public void handle_whenRequestPathAllowedButMethodNotAllowedInCustomFlow_expectRequestFailedMethodNotAllowed()
      throws Exception {
    RoutingContext context = mockContext("/content/page.html", HttpMethod.POST);
    tested.handle(context);

    Mockito.verify(context, Mockito.times(1)).fail(405);
  }

  @Test
  public void handle_whenRequestPathNotAllowed_expectRequestFailedNotFound() throws Exception {
    RoutingContext context = mockContext("/notallowed/page.html", HttpMethod.GET);
    tested.handle(context);

    Mockito.verify(context, Mockito.times(1)).fail(404);
  }

  private RoutingContext mockContext(String requestPath, HttpMethod method) {
    RoutingContext context = Mockito.mock(RoutingContext.class);
    HttpServerRequest httpRequest = Mockito.mock(HttpServerRequest.class);
    Mockito.when(context.request()).thenReturn(httpRequest);
    Mockito.when(httpRequest.path()).thenReturn(requestPath);
    Mockito.when(httpRequest.method()).thenReturn(method);
    return context;
  }
}
