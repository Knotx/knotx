/*
 * Knot.x - Reactive microservice assembler - Repository Verticle
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
package com.cognifide.knotx.repository;

import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;

import org.apache.commons.lang3.tuple.Pair;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

public class HttpRepositoryVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpRepositoryVerticle.class);
  private static final String ERROR_MESSAGE = "Unable to get template from the repository";

  private String address;
  private JsonObject clientOptions;
  private JsonObject clientDestination;
  private HttpClient httpClient;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    address = config().getString("address");
    clientOptions = config().getJsonObject("client.options", new JsonObject());
    clientDestination = config().getJsonObject("client.destination");
  }

  @Override
  public void start() throws Exception {
    LOGGER.info("Registered <{}>", this.getClass().getSimpleName());
    httpClient = createHttpClient();

    vertx.eventBus().<ClientRequest>consumer(address).handler(
        message -> Observable.just(message)
            .doOnNext(this::traceMessage)
            .flatMap(this::getTemplateContent, Pair::of)
            .subscribe(
                response -> response.getLeft().reply(response.getRight()),
                error -> {
                  LOGGER.error(ERROR_MESSAGE, error);
                  message.reply(new ClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR));
                }
            )
    );
  }

  private HttpClient createHttpClient() {
    return clientOptions.isEmpty() ? vertx.createHttpClient() : vertx.createHttpClient(new HttpClientOptions(clientOptions));
  }

  private Observable<ClientResponse> getTemplateContent(final Message<ClientRequest> repoMessage) {
    final ClientRequest repoRequest = repoMessage.body();

    return requestForTemplate(repoRequest)
        .doOnNext(this::traceHttpResponse)
        .flatMap(this::processResponse);
  }

  private Observable<HttpClientResponse> requestForTemplate(ClientRequest repoRequest) {
    return RxHelper.get(httpClient, clientDestination.getInteger("port"), clientDestination.getString("domain"),
        repoRequest.path(), repoRequest.headers());
  }

  private Observable<ClientResponse> processResponse(final HttpClientResponse response) {
    return Observable.just(Buffer.buffer())
        .mergeWith(response.toObservable())
        .reduce(Buffer::appendBuffer)
        .map(buffer -> toRepositoryResponse(buffer, response));
  }

  private ClientResponse toRepositoryResponse(Buffer buffer, final HttpClientResponse httpClientResponse) {
    return new ClientResponse()
        .setStatusCode(HttpResponseStatus.valueOf(httpClientResponse.statusCode()))
        .setHeaders(httpClientResponse.headers())
        .setBody(buffer);
  }

  private void traceHttpResponse(HttpClientResponse response) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got response from remote repository {}", response.statusCode());
    }
  }

  private void traceMessage(Message<ClientRequest> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body());
    }
  }
}
