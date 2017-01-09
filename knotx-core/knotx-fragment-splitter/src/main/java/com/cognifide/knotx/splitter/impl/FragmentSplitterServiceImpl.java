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

import com.cognifide.knotx.codec.KnotContextCodec;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.splitter.FragmentSplitterService;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.serviceproxy.ServiceException;

public class FragmentSplitterServiceImpl implements FragmentSplitterService {

  public FragmentSplitterServiceImpl(Vertx vertx) {
    ((io.vertx.core.Vertx)vertx).eventBus().registerDefaultCodec(KnotContext.class, new KnotContextCodec());
  }

  @Override
  public void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result) {
    try {
      knotContext.setFragments(new HtmlFragmentSplitter().split(knotContext.clientResponse().body().toString()));
      knotContext.clientResponse().setStatusCode(HttpResponseStatus.OK).clearBody();

      result.handle(Future.succeededFuture(knotContext));
    } catch (Exception ex) {
      result.handle(ServiceException.fail(100, "message", new JsonObject().put("Exception", ex.getClass().toString())));
    }

//
//
//    eventBus.<KnotContext>consumer(configuration.getAddress()).handler(
//        message -> Observable.just(message)
//            .doOnNext(this::traceMessage)
//            .subscribe(
//                response -> {
//                  KnotContext context = response.body();
//                  context.setFragments(splitter.split(context.clientResponse().body().toString()));
//                  context.clientResponse().setStatusCode(HttpResponseStatus.OK).clearBody();
//                  response.reply(context);
//                },
//                error -> {
//                  LOGGER.error("Exception happened during HTML splitting.", error);
//                  message.reply(processError(message.body(), error));
//                }
//            )
//    );
//
//    return null;
  }

//  private KnotContext processError(KnotContext context, Throwable error) {
//    HttpResponseStatus statusCode;
//    if (error instanceof NoSuchElementException) {
//      statusCode = HttpResponseStatus.NOT_FOUND;
//    } else {
//      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
//    }
//    context.clientResponse().setStatusCode(statusCode);
//    return context;
//  }
//
//  private void traceMessage(Message<KnotContext> message) {
//    if (LOGGER.isTraceEnabled()) {
//      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body());
//    }
//  }
}
