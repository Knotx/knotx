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
package io.knotx.server;

import io.knotx.dataobjects.KnotContext;
import io.knotx.proxy.KnotProxy;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceBinder;
import java.util.function.Consumer;


public class MockKnotProxy implements KnotProxy {

  private Consumer<KnotContext> knot;

  private MockKnotProxy(Consumer<KnotContext> knot) {
    this.knot = knot;
  }

  public static void register(Vertx vertx, String address) {
    register(vertx, address, null);
  }

  public static void register(Vertx vertx, String address, Consumer<KnotContext> knot) {
    new ServiceBinder(vertx)
        .setAddress(address)
        .register(KnotProxy.class, new MockKnotProxy(knot));
  }

  @Override
  public void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result) {
    if (knot != null) {
      knot.accept(knotContext);
    }
    result.handle(Future.succeededFuture(knotContext));
  }
}

