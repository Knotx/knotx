/*
 * Knot.x - Reactive microservice assembler - HTTP Server
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.knotx.server;

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.proxy.KnotProxy;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ProxyHelper;
import rx.functions.Action1;


public class MockKnotProxy implements KnotProxy {

  private Action1<KnotContext> knot;

  private MockKnotProxy(Action1<KnotContext> knot) {
    this.knot = knot;
  }

  public static void register(Vertx vertx, String address) {
    register(vertx, address, null);
  }

  public static void register(Vertx vertx, String address, Action1<KnotContext> knot) {
    ProxyHelper.registerService(KnotProxy.class, vertx, new MockKnotProxy(knot), address);
  }

  @Override
  public void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result) {
    if (knot != null) {
      knot.call(knotContext);
    }
    result.handle(Future.succeededFuture(knotContext));
  }
}

