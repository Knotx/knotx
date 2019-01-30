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

import io.knotx.server.api.context.FragmentsContext;
import io.knotx.server.api.handler.FragmentContextHandler;
import io.knotx.server.api.handler.RoutingHandlerFactory;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;


public class SplitterRoutingHandlerFactory implements RoutingHandlerFactory {

  @Override
  public String getName() {
    return "splitterHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    return new KnotxSplitterHandler();
  }

  public class KnotxSplitterHandler extends FragmentContextHandler {

    private HtmlFragmentSplitter splitter;

    private KnotxSplitterHandler() {
      splitter = new HtmlFragmentSplitter();
    }

    @Override
    protected FragmentsContext handle(RoutingContext context, FragmentsContext fragmentsContext) {
      fragmentsContext
          .setFragments(splitter.split(fragmentsContext.getClientResponse().getBody().toString()));
      fragmentsContext.getClientResponse().setStatusCode(HttpResponseStatus.OK.code()).clearBody();
      return fragmentsContext;
    }


  }

}


