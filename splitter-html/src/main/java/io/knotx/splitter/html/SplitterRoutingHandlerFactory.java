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
package io.knotx.splitter.html;

import io.knotx.fragment.Fragment;
import io.knotx.server.api.context.ClientResponse;
import io.knotx.server.api.context.RequestEvent;
import io.knotx.server.api.handler.RequestEventHandler;
import io.knotx.server.api.handler.RequestEventHandlerResult;
import io.knotx.server.api.handler.RoutingHandlerFactory;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.List;
import java.util.Optional;

public class SplitterRoutingHandlerFactory implements RoutingHandlerFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SplitterRoutingHandlerFactory.class);
  private static final String MISSING_REPOSITORY_PAYLOAD = "repositoryResponse is missing";

  @Override
  public String getName() {
    return "splitterHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    return new KnotxSplitterHandler();
  }

  //ToDo unit tests
  public class KnotxSplitterHandler extends RequestEventHandler {

    private HtmlFragmentSplitter splitter;

    private KnotxSplitterHandler() {
      splitter = new HtmlFragmentSplitter();
    }

    @Override
    protected RequestEventHandlerResult handle(RequestEvent requestEvent) {
      final Optional<ClientResponse> repositoryResponse = getRepositoryResponse(requestEvent);
      final RequestEventHandlerResult result;
      if (repositoryResponse.isPresent()) {
        List<Fragment> fragments = splitter.split(repositoryResponse.get().getBody().toString());
        RequestEvent requestEventWithFragments = new RequestEvent(requestEvent.getClientRequest(), fragments, requestEvent.getPayload());
        result = RequestEventHandlerResult.success(requestEventWithFragments);
      } else {
        LOGGER.error(MISSING_REPOSITORY_PAYLOAD);
        ClientResponse failResponse = new ClientResponse()
            .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
            .setBody(Buffer.buffer(MISSING_REPOSITORY_PAYLOAD));
        result = RequestEventHandlerResult.fail(failResponse);
      }
      return result;
    }

    private Optional<ClientResponse> getRepositoryResponse(RequestEvent requestEvent) {
      final JsonObject repositoryResponse = requestEvent.getPayload()
          .getJsonObject("repositoryResponse");
      return Optional.ofNullable(repositoryResponse).map(ClientResponse::new);
    }

  }

}


