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
package com.cognifide.knotx.knot.assembler;

import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rx.Observable;

public class FragmentAssemblerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(FragmentAssemblerVerticle.class);

  private static final String SNIPPET_IDENTIFIER_NAME = "data-knot-types";

  private static final String ANY_SNIPPET_PATTERN =
      "(?is).*<script\\s+" + SNIPPET_IDENTIFIER_NAME + ".*";

  private FragmentAssemblerConfiguration configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.configuration = new FragmentAssemblerConfiguration(config());
  }

  @Override
  public void start() throws IOException, URISyntaxException {
    LOGGER.debug("Starting <{}>", this.getClass().getName());
    EventBus eventBus = vertx.eventBus();

    eventBus.<KnotContext>consumer(configuration.getAddress()).handler(
        message -> Observable.just(message)
            .doOnNext(this::traceMessage)
            .subscribe(
                response -> {
                  KnotContext context = response.body();
                  response.reply(createSuccessResponse(context, getTemplateContent(context)));
                },
                error -> {
                  LOGGER.error("Exception happened during HTML splitting.", error);
                  message.reply(processError(message.body(), error));
                }
            )
    );
  }

  private String getTemplateContent(KnotContext context) {
    return context.fragments().map(fragments -> fragments.stream()
        .map(Fragment::getContent)
        .filter(StringUtils::isNotBlank)
        .map(value -> {
          if (value.matches(ANY_SNIPPET_PATTERN)) {
            Document document = Jsoup.parseBodyFragment(value);
            Element scriptTag = document.body().child(0);
            return scriptTag.unwrap().toString();
          } else {
            return value;
          }
        })
        .collect(Collectors.joining())).orElse(StringUtils.EMPTY);
  }

  private KnotContext processError(KnotContext knotContext, Throwable error) {
    LOGGER.error("Error happened during Template processing", error);
    ClientResponse errorResponse = new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);

    return new KnotContext()
        .setClientRequest(knotContext.clientRequest())
        .setClientResponse(errorResponse);
  }

  private KnotContext createSuccessResponse(KnotContext inputContext, String renderedContent) {
    ClientResponse clientResponse = inputContext.clientResponse();
    MultiMap headers = clientResponse.headers();
    headers.set(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(renderedContent.length()));
    clientResponse.setBody(Buffer.buffer(renderedContent)).setHeaders(headers);

    return new KnotContext()
        .setClientRequest(inputContext.clientRequest())
        .setClientResponse(clientResponse);
  }

  private void traceMessage(Message<KnotContext> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body());
    }
  }

}
