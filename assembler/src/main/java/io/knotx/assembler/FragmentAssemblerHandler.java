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

import io.knotx.fragment.Fragment;
import io.knotx.server.api.context.ClientResponse;
import io.knotx.server.api.context.RequestEvent;
import io.knotx.server.api.handler.RequestEventHandler;
import io.knotx.server.api.handler.RequestEventHandlerResult;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

class FragmentAssemblerHandler extends RequestEventHandler {

  @Override
  protected RequestEventHandlerResult handle(RequestEvent requestEvent) {
    final String responseBody = requestEvent.getFragments().stream()
        .map(Fragment::getBody)
        .collect(Collectors.joining());
    return createSuccessResponse(requestEvent, responseBody);
  }

  private RequestEventHandlerResult createSuccessResponse(RequestEvent inputContext, String responseBody) {
    ClientResponse clientResponse = new ClientResponse();
    if (StringUtils.isBlank(responseBody)) {
      clientResponse.setStatusCode(HttpResponseStatus.NO_CONTENT.code());
    } else {
      MultiMap headers = clientResponse.getHeaders();
      headers.add(HttpHeaders.CONTENT_LENGTH.toString().toLowerCase(),
          Integer.toString(responseBody.length()));

      clientResponse.setBody(Buffer.buffer(responseBody))
          .setHeaders(headers)
          .setStatusCode(HttpResponseStatus.OK.code());
    }

    JsonObject payload = inputContext.getPayload().put("assemblerResult", clientResponse.toJson());
    return RequestEventHandlerResult.success(
        new RequestEvent(inputContext.getClientRequest(), inputContext.getFragments(), payload));
  }

}
