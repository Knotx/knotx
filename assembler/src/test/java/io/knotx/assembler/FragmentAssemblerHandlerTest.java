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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.Lists;
import io.knotx.fragment.Fragment;
import io.knotx.server.api.context.ClientRequest;
import io.knotx.server.api.context.ClientResponse;
import io.knotx.server.api.context.RequestEvent;
import io.knotx.server.api.handler.RequestEventResult;
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

  @Test
  public void callAssemblerWithNoFragments_expectNoContentStatus() {
    // given
    FragmentAssemblerHandler assemblerHandler = new FragmentAssemblerHandler();
    RequestEvent requestEvent = new RequestEvent(clientRequest);

    // when
    final RequestEventResult result = assemblerHandler.handle(requestEvent);

    // then
    assertTrue(result.getRequestEvent().isPresent());
    final ClientResponse assemblerResult = extractAssemblerResult(result.getRequestEvent().get());
    assertEquals(HttpResponseStatus.NO_CONTENT.code(), assemblerResult.getStatusCode());
  }

  @Test
  public void callAssemblerWithFragment_expectAssemblerResultWithBodyAndOkStatus() {
    // given
    String expectedBody = "<h1>Some text</h1>\n"
        + "<p>Some text</p>";
    FragmentAssemblerHandler assemblerHandler = new FragmentAssemblerHandler();

    RequestEvent requestEvent = new RequestEvent(clientRequest,
        Lists.newArrayList(new Fragment("_STATIC", new JsonObject(), expectedBody)),
        new JsonObject());

    // when
    final RequestEventResult result = assemblerHandler.handle(requestEvent);

    // then
    assertTrue(result.getRequestEvent().isPresent());
    final ClientResponse assemblerResult = extractAssemblerResult(result.getRequestEvent().get());
    assertEquals(Buffer.buffer(expectedBody), assemblerResult.getBody());
    assertEquals(HttpResponseStatus.OK.code(), assemblerResult.getStatusCode());
    assertEquals(Integer.toString((expectedBody.length())),
        assemblerResult.getHeaders().get(HttpHeaders.CONTENT_LENGTH));
  }

  private ClientResponse extractAssemblerResult(RequestEvent requestEvent) {
    return new ClientResponse(
        requestEvent.getPayload().getJsonObject("assemblerResult"));
  }

}
