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
package io.knotx.gateway.impl;

import java.util.NoSuchElementException;
import java.util.Set;

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.knot.AbstractKnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import rx.Single;

public class RequestProcessorKnotProxyImpl extends AbstractKnotProxy {

  private final static String RESPONSE = "{\"message\":\"This is a sample custom flow response\"}";

  @Override
  protected Single<KnotContext> processRequest(KnotContext knotContext) {
    return Single.just(createSuccessResponse(knotContext));
  }

  @Override
  protected boolean shouldProcess(Set<String> knots) {
    return true;
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    HttpResponseStatus statusCode;
    if (error instanceof NoSuchElementException) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    knotContext.getClientResponse().setStatusCode(statusCode.code());
    return knotContext;
  }

  private KnotContext createSuccessResponse(KnotContext inputContext) {

    ClientResponse clientResponse = new ClientResponse();

      io.vertx.rxjava.core.MultiMap headers = clientResponse.getHeaders();
      headers.add(HttpHeaders.CONTENT_LENGTH.toString().toLowerCase(),
          Integer.toString(RESPONSE.length()))
          .add("Content-Type", "application/json");

      clientResponse.setBody(Buffer.buffer(RESPONSE)).setHeaders(headers);
      clientResponse.setStatusCode(HttpResponseStatus.OK.code());

    return new KnotContext()
        .setClientRequest(inputContext.getClientRequest())
        .setClientResponse(clientResponse);
  }
}
