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
package io.knotx.server.handler.http.response.cookie;

import io.knotx.server.handler.api.RoutingHandlerFactory;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.CookieHandler;

public class CookieRoutingHandlerFactory implements RoutingHandlerFactory {

  @Override
  public String getName() {
    return "cookieHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    return CookieHandler.create();
  }
}
