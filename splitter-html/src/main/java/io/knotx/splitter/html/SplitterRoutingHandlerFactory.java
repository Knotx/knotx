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
import io.knotx.server.api.context.RequestContext;
import io.knotx.server.api.context.RequestEvent;
import io.knotx.server.api.handler.DefaultRequestContextEngine;
import io.knotx.server.api.handler.RequestContextEngine;
import io.knotx.server.api.handler.RequestEventHandlerResult;
import io.knotx.server.api.handler.RoutingHandlerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class SplitterRoutingHandlerFactory implements RoutingHandlerFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SplitterRoutingHandlerFactory.class);
  private static final String MISSING_REPOSITORY_PAYLOAD = "Template body is missing!";

  @Override
  public String getName() {
    return "splitterHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    return new KnotxSplitterHandler();
  }

  //ToDo unit tests
  public class KnotxSplitterHandler implements Handler<RoutingContext> {

    private HtmlFragmentSplitter splitter;
    private final RequestContextEngine engine;

    private KnotxSplitterHandler() {
      engine = new DefaultRequestContextEngine(getClass().getSimpleName());
      splitter = new HtmlFragmentSplitter();
    }

    @Override
    public void handle(RoutingContext context) {
      RequestContext requestContext = context.get(RequestContext.KEY);
      try {
        RequestEventHandlerResult result = splitBody(requestContext.getRequestEvent(),
            requestContext.getClientResponse());
        engine.processAndSaveResult(result, context, requestContext);
      } catch (Exception e) {
        engine.handleFatal(context, requestContext, e);
      }
    }

    private RequestEventHandlerResult splitBody(RequestEvent requestEvent,
        ClientResponse clientResponse) {
      final RequestEventHandlerResult result;
      final String template = getTemplate(clientResponse);
      if (StringUtils.isNotBlank(template)) {
        List<Fragment> fragments = splitter.split(template);
        RequestEvent requestEventWithFragments = new RequestEvent(requestEvent.getClientRequest(),
            fragments, requestEvent.getPayload());
        result = RequestEventHandlerResult.success(requestEventWithFragments);
        clientResponse.setBody(null);
      } else {
        LOGGER.error(MISSING_REPOSITORY_PAYLOAD);
        result = RequestEventHandlerResult.fail(MISSING_REPOSITORY_PAYLOAD);
      }
      return result;
    }

    private String getTemplate(ClientResponse clientResponse) {
      return Optional.ofNullable(clientResponse.getBody()).map(Buffer::toString)
          .orElse(null);
    }

  }

}


