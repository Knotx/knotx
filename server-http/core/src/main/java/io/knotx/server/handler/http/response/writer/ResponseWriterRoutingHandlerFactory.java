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
package io.knotx.server.handler.http.response.writer;

import io.knotx.server.api.handler.RoutingHandlerFactory;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResponseWriterRoutingHandlerFactory implements RoutingHandlerFactory {

  @Override
  public String getName() {
    return "writerHandler";
  }

  @Override
  public Handler<RoutingContext> create(Vertx vertx, JsonObject config) {
    Set<String> allowedResponseHeaders = getAllowedHeaders(config);
    return new ResponseWriterHandler(allowedResponseHeaders);
  }

  private Set<String> getAllowedHeaders(JsonObject config) {
    List configList = config.getJsonArray("allowedResponseHeaders").getList();
    Set<String> result = new HashSet<>(configList.size());
    configList.forEach(value -> result.add(((String) value).toLowerCase()));
    return result;
  }


}
