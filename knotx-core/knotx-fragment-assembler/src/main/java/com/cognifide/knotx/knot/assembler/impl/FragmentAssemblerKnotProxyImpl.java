/*
 * Knot.x - Reactive microservice assembler - Fragment Assembler
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
package com.cognifide.knotx.knot.assembler.impl;

import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.knot.assembler.FragmentAssemblerConfiguration;
import com.cognifide.knotx.proxy.KnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class FragmentAssemblerKnotProxyImpl implements KnotProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(FragmentAssemblerKnotProxyImpl.class);

  private FragmentAssemblerConfiguration configuration;

  public FragmentAssemblerKnotProxyImpl(JsonObject config) {
    this.configuration = new FragmentAssemblerConfiguration(config);
  }

  @Override
  public void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result) {
    if (knotContext.getFragments() != null && knotContext.getFragments().size() > 0) {
      try {
        String joinedFragments = knotContext.getFragments().stream()
            .map(configuration.unprocessedFragmentStrategy()::get)
            .collect(Collectors.joining());

        result.handle(Future.succeededFuture(createSuccessResponse(knotContext, joinedFragments)));
      } catch (Exception ex) {
        LOGGER.error("Exception happened during Fragment assembly.", ex);
        result.handle(Future.succeededFuture(processError(knotContext)));
      }
    } else { //no fragments
      LOGGER.error("Fragments are empty or not exists in KnotContext.");
      result.handle(Future.succeededFuture(processError(knotContext)));
    }
  }

  private KnotContext createSuccessResponse(KnotContext inputContext, String renderedContent) {
    ClientResponse clientResponse = inputContext.getClientResponse();
    if (StringUtils.isBlank(renderedContent)) {
      clientResponse.setStatusCode(HttpResponseStatus.NO_CONTENT.code());
    } else {
      MultiMap headers = clientResponse.getHeaders();
      headers.add(HttpHeaders.CONTENT_LENGTH.toString().toLowerCase(), Integer.toString(renderedContent.length()));

      clientResponse.setBody(Buffer.buffer(renderedContent)).setHeaders(headers);
      clientResponse.setStatusCode(HttpResponseStatus.OK.code());
    }

    return new KnotContext()
        .setClientRequest(inputContext.getClientRequest())
        .setClientResponse(clientResponse);
  }

  private KnotContext processError(KnotContext knotContext) {
    ClientResponse errorResponse = new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());

    return new KnotContext()
        .setClientRequest(knotContext.getClientRequest())
        .setClientResponse(errorResponse);
  }

  public String getAddress() {
    return configuration.address();
  }
}
