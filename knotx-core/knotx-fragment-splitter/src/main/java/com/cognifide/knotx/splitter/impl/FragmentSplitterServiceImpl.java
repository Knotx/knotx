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
package com.cognifide.knotx.splitter.impl;

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.splitter.FragmentSplitterService;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.NoSuchElementException;

public class FragmentSplitterServiceImpl implements FragmentSplitterService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FragmentSplitterServiceImpl.class);

  private final FragmentSplitter splitter = new HtmlFragmentSplitter();

  @Override
  public void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result) {
    try {
      knotContext.setFragments(splitter.split(knotContext.getClientResponse().getBody().toString()));
      knotContext.getClientResponse().setStatusCode(HttpResponseStatus.OK.code()).clearBody();

      result.handle(Future.succeededFuture(knotContext));
    } catch (Exception ex) {
      LOGGER.error("Exception happened during HTML splitting.", ex);
      result.handle(Future.succeededFuture(processError(knotContext, ex)));
    }
  }

  private KnotContext processError(KnotContext context, Exception exception) {
    HttpResponseStatus statusCode;
    if (exception instanceof NoSuchElementException) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    context.getClientResponse().setStatusCode(statusCode.code());
    return context;
  }

}
