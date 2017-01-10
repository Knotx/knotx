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

package com.cognifide.knotx.rxjava.repository;

import java.util.Map;
import rx.Observable;
import com.cognifide.knotx.dataobjects.ClientRequest;
import io.vertx.rxjava.core.Vertx;
import io.vertx.core.json.JsonObject;
import com.cognifide.knotx.dataobjects.ClientResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


public class HttpRepositoryConnectorService {

  final com.cognifide.knotx.repository.HttpRepositoryConnectorService delegate;

  public HttpRepositoryConnectorService(com.cognifide.knotx.repository.HttpRepositoryConnectorService delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  public static HttpRepositoryConnectorService create(Vertx vertx, JsonObject configuration) { 
    HttpRepositoryConnectorService ret = HttpRepositoryConnectorService.newInstance(com.cognifide.knotx.repository.HttpRepositoryConnectorService.create((io.vertx.core.Vertx)vertx.getDelegate(), configuration));
    return ret;
  }

  public static HttpRepositoryConnectorService createProxy(Vertx vertx, String address) { 
    HttpRepositoryConnectorService ret = HttpRepositoryConnectorService.newInstance(com.cognifide.knotx.repository.HttpRepositoryConnectorService.createProxy((io.vertx.core.Vertx)vertx.getDelegate(), address));
    return ret;
  }

  public void process(ClientRequest request, Handler<AsyncResult<ClientResponse>> result) { 
    delegate.process(request, result);
  }

  public Observable<ClientResponse> processObservable(ClientRequest request) { 
    io.vertx.rx.java.ObservableFuture<ClientResponse> result = io.vertx.rx.java.RxHelper.observableFuture();
    process(request, result.toHandler());
    return result;
  }


  public static HttpRepositoryConnectorService newInstance(com.cognifide.knotx.repository.HttpRepositoryConnectorService arg) {
    return arg != null ? new HttpRepositoryConnectorService(arg) : null;
  }
}
