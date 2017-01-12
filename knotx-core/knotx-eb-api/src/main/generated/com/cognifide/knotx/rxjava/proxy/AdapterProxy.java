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

package com.cognifide.knotx.rxjava.proxy;

import java.util.Map;
import rx.Observable;
import io.vertx.rxjava.core.Vertx;
import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.AdapterResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


public class AdapterProxy {

  final com.cognifide.knotx.proxy.AdapterProxy delegate;

  public AdapterProxy(com.cognifide.knotx.proxy.AdapterProxy delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  public static AdapterProxy createProxy(Vertx vertx, String address) { 
    AdapterProxy ret = AdapterProxy.newInstance(com.cognifide.knotx.proxy.AdapterProxy.createProxy((io.vertx.core.Vertx)vertx.getDelegate(), address));
    return ret;
  }

  public void process(AdapterRequest request, Handler<AsyncResult<AdapterResponse>> result) { 
    delegate.process(request, result);
  }

  public Observable<AdapterResponse> processObservable(AdapterRequest request) { 
    io.vertx.rx.java.ObservableFuture<AdapterResponse> result = io.vertx.rx.java.RxHelper.observableFuture();
    process(request, result.toHandler());
    return result;
  }


  public static AdapterProxy newInstance(com.cognifide.knotx.proxy.AdapterProxy arg) {
    return arg != null ? new AdapterProxy(arg) : null;
  }
}
