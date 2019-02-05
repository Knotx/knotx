/*
 * Copyright (C) 2018 Cognifide Limited
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
package io.knotx.server.api.handler;

import io.knotx.server.api.context.ClientResponse;
import io.knotx.server.api.context.RequestEvent;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Optional;

public class RequestEventResult {

  private RequestEvent requestEvent;
  private ClientResponse clientResponse;

  private RequestEventResult() {
    //private
  }

  public static RequestEventResult success(RequestEvent requestEvent) {
    final RequestEventResult result = new RequestEventResult();
    result.requestEvent = requestEvent;
    result.clientResponse = new ClientResponse().setStatusCode(HttpResponseStatus.OK.code());
    return result;
  }

  public static RequestEventResult fail(ClientResponse clientResponse) {
    final RequestEventResult result = new RequestEventResult();
    result.clientResponse = clientResponse;
    return result;
  }

  //ToDo -> finishes immediately handler routing
  public static RequestEventResult end(ClientResponse clientResponse) {
    throw new UnsupportedOperationException("Implement me!");
  }

  public Optional<RequestEvent> getRequestEvent() {
    return Optional.ofNullable(requestEvent);
  }

  public ClientResponse getClientResponse() {
    return clientResponse;
  }
}
