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
import io.vertx.rxjava.core.Vertx;
import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.AdapterResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


public class AdapterApi {

  final com.cognifide.knotx.modules.AdapterApi delegate;

  public AdapterApi(com.cognifide.knotx.modules.AdapterApi delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  public static AdapterApi createProxy(Vertx vertx, String address) { 
    AdapterApi ret = AdapterApi.newInstance(com.cognifide.knotx.modules.AdapterApi.createProxy((io.vertx.core.Vertx)vertx.getDelegate(), address));
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


  public static AdapterApi newInstance(com.cognifide.knotx.modules.AdapterApi arg) {
    return arg != null ? new AdapterApi(arg) : null;
  }
}
