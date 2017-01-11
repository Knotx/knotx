/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.cognifide.knotx.rxjava.modules;

import java.util.Map;
import rx.Observable;
import com.cognifide.knotx.dataobjects.KnotContext;
import io.vertx.rxjava.core.Vertx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


public class KnotApi {

  final com.cognifide.knotx.modules.KnotApi delegate;

  public KnotApi(com.cognifide.knotx.modules.KnotApi delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  public static KnotApi createProxy(Vertx vertx, String address) { 
    KnotApi ret = KnotApi.newInstance(com.cognifide.knotx.modules.KnotApi.createProxy((io.vertx.core.Vertx)vertx.getDelegate(), address));
    return ret;
  }

  public void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result) { 
    delegate.process(knotContext, result);
  }

  public Observable<KnotContext> processObservable(KnotContext knotContext) { 
    io.vertx.rx.java.ObservableFuture<KnotContext> result = io.vertx.rx.java.RxHelper.observableFuture();
    process(knotContext, result.toHandler());
    return result;
  }


  public static KnotApi newInstance(com.cognifide.knotx.modules.KnotApi arg) {
    return arg != null ? new KnotApi(arg) : null;
  }
}
