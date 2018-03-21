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
import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public abstract class AbstractAdapterProxy implements AdapterProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAdapterProxy.class);

  protected abstract Single<AdapterResponse> processRequest(AdapterRequest message);

  @Override
  public void process(AdapterRequest request, Handler<AsyncResult<AdapterResponse>> result) {
    processRequest(request)
        .subscribe(
            adapterResponse -> result.handle(Future.succeededFuture(adapterResponse)),
            error -> {
              LOGGER.error("Error happened during Adapter Request processing", error);
              result.handle(Future.succeededFuture(getErrorResponse(error)));
            }
        );
  }

  /**
   * Method generates error {@link AdapterResponse} in case of processing failure.
   *
   * @param error - error that occurred.
   * @return - error response (e.g. with 500 status code and other info).
   */
  protected AdapterResponse getErrorResponse(Throwable error) {
    return new AdapterResponse().setResponse(new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .setBody(Buffer.buffer(error.getMessage())));
  }
}
