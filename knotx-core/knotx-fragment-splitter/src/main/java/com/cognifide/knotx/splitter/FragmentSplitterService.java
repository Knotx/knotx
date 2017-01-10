/*
 * Knot.x - Reactive microservice assembler - HTML Fragment Splitter
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
package com.cognifide.knotx.splitter;

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.splitter.impl.FragmentSplitterServiceImpl;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

@ProxyGen
@VertxGen
public interface FragmentSplitterService {

  static FragmentSplitterService create(Vertx vertx) {
    return new FragmentSplitterServiceImpl();
  }

  static FragmentSplitterService createProxy(Vertx vertx, String address) {
    return new FragmentSplitterServiceVertxEBProxy(vertx, address);
  }

  void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result);
}
