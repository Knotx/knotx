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
package io.knotx.server.handler.splitter;

import io.knotx.server.api.FragmentsContext;
import io.knotx.server.handler.api.RoutingHandlerFactory;
import io.knotx.splitter.NewHtmlFragmentSplitter;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;


public class SplitterRoutingHandlerFactory implements RoutingHandlerFactory {

  @Override
  public String getName() {
    return "splitterHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    return new KnotxSplitterHandler(vertx, config);
  }

  public class KnotxSplitterHandler implements Handler<RoutingContext> {

    private final Logger LOGGER = LoggerFactory.getLogger(KnotxSplitterHandler.class);

    private NewHtmlFragmentSplitter splitter;

    private KnotxSplitterHandler(Vertx vertx, JsonObject configuration) {
      splitter = new NewHtmlFragmentSplitter();
    }

    @Override
    public void handle(RoutingContext context) {
      FragmentsContext fragmentsContext = context.get(FragmentsContext.KEY);
      try {
        fragmentsContext
            .setFragments(splitter.split(fragmentsContext.getClientResponse().getBody().toString()));
        fragmentsContext.getClientResponse().setStatusCode(HttpResponseStatus.OK.code()).clearBody();
        traceMessage(fragmentsContext);
        context.put(FragmentsContext.KEY, fragmentsContext);
      } catch (Exception e) {
        context.fail(fragmentsContext.getClientResponse().getStatusCode());
      } finally {
        context.next();
      }
    }

    private void traceMessage(FragmentsContext ctx) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Got message from <fragment-splitter> with value <{}>", ctx);
      }
    }

  }

}


