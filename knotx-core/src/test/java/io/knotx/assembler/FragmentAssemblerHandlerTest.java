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
package io.knotx.assembler;

import static org.mockito.ArgumentMatchers.any;

import com.google.common.collect.Lists;
import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.fragment.NewFragment;
import io.knotx.server.api.RequestContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FragmentAssemblerHandlerTest {

  @Mock
  private RoutingContext routingContext;

  @Mock
  private ClientRequest clientRequest;

  @Mock
  private ClientResponse clientResponse;

  @Test
  public void callAssemblerWithNoFragments_expectNoContentStatus() {
    // given
    FragmentAssemblerHandler assemblerHandler = new FragmentAssemblerHandler(
        new FragmentAssemblerOptions(FragmentAssembleStrategy.AS_IS));

    RequestContext requestContext = new RequestContext();

    requestContext.setClientResponse(clientResponse);
    requestContext.setClientRequest(clientRequest);

    Mockito.when(routingContext.get(any())).thenReturn(requestContext);

    // when
    assemblerHandler.handle(routingContext);

    // then
    Mockito.verify(clientResponse).setStatusCode(HttpResponseStatus.NO_CONTENT.code());
  }

  @Test
  public void callAssemblerWithFragment_expectOkStatus() {
    // given
    String expectedBody = "<h1>Some text</h1>\n"
        + "<p>Some text</p>";
    FragmentAssemblerHandler assemblerHandler = new FragmentAssemblerHandler(
        new FragmentAssemblerOptions(FragmentAssembleStrategy.AS_IS));

    MultiMap headers = Mockito.mock(MultiMap.class);

    RequestContext requestContext = new RequestContext();
    requestContext.setClientResponse(clientResponse);
    requestContext.setClientRequest(clientRequest);
    requestContext.setFragments(
        Lists.newArrayList(new NewFragment("_STATIC", new JsonObject(), expectedBody)));

    Mockito.when(routingContext.get(any())).thenReturn(requestContext);
    Mockito.when(clientResponse.getHeaders()).thenReturn(headers);
    Mockito.when(clientResponse.setBody(any())).thenReturn(clientResponse);
    Mockito.when(clientResponse.setHeaders(any())).thenReturn(clientResponse);

    // when
    assemblerHandler.handle(routingContext);

    // then
    Mockito.verify(clientResponse).setBody(Buffer.buffer(expectedBody));
    Mockito.verify(clientResponse).setStatusCode(HttpResponseStatus.OK.code());
    Mockito.verify(headers).add(HttpHeaders.CONTENT_LENGTH.toString().toLowerCase(),
        Integer.toString(expectedBody.length()));
  }

}
