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
package io.knotx.adapter;

import io.knotx.dataobjects.AdapterRequest;
import io.knotx.dataobjects.AdapterResponse;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.proxy.AdapterProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;

public abstract class BlockingAdapterProxy<T> implements AdapterProxy {

  private Vertx vertx;

  public BlockingAdapterProxy(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void process(AdapterRequest request, Handler<AsyncResult<AdapterResponse>> result) {
    try {
      executeBlockingOperation(request, result);
    } catch (Exception e) {
      AdapterResponse adapterResponse = new AdapterResponse().setResponse(new ClientResponse()
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .setBody(Buffer.buffer(e.getMessage())));
      result.handle(Future.succeededFuture(adapterResponse));
    }
  }

  /**
   * Here a blocking code should appear.
   */
  protected abstract T processBlocking(AdapterRequest request);

  /**
   * This method transforms a result from {@link BlockingAdapterProxy#processBlocking(AdapterRequest)}
   * method and creates a client response.
   */
  protected abstract ClientResponse createClientResponse(AsyncResult<T> res);

  private void executeBlockingOperation(AdapterRequest request,
      Handler<AsyncResult<AdapterResponse>> result) {
    vertx.<T>executeBlocking(
        future -> {
          T response = processBlocking(request);
          future.complete(response);
        },
        res -> {
          AdapterResponse adapterResponse = new AdapterResponse();
          adapterResponse.setResponse(createClientResponse(res));
          result.handle(Future.succeededFuture(adapterResponse));
        }
    );
  }

}
