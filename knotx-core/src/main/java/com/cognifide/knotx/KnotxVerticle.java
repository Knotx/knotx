/*
 * Knot.x - Reactive microservice assembler
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
package com.cognifide.knotx;

import com.cognifide.knotx.api.KnotxConst;
import com.cognifide.knotx.api.RepositoryResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpServer;

@Component
public class KnotxVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnotxVerticle.class);

    @Autowired
    private KnotxConfiguration configuration;

    private HttpServer httpServer;

    @Override
    public void start() throws IOException, URISyntaxException {
        LOGGER.debug("Registered <{0}>", this.getClass().getSimpleName());
        httpServer = vertx.createHttpServer();
        EventBus eventBus = vertx.eventBus();

        httpServer.requestHandler(
                request -> {
                    eventBus.<RepositoryResponse>sendObservable(KnotxConst.TEMPLATE_REPOSITORY_ADDRESS, request.path())
                            .doOnNext(this::traceMessage)
                            .subscribe(
                                    reply -> {
                                        RepositoryResponse repository = reply.body();
                                        if (repository.isSuccess()) {
                                            eventBus.sendObservable(KnotxConst.TEMPLATE_ENGINE_ADDRESS, repository.getData().getDelegate())
                                                    .subscribe(
                                                            result -> {
                                                                Object body = result.body();
                                                                request.response().end(body.toString());
                                                            },
                                                            error -> {
                                                                LOGGER.error("Error happened", error);
                                                                request.response().setStatusCode(500).end(error.toString());
                                                            }
                                                    );
                                        } else {
                                            request.response().setStatusCode(404).end(repository.getReason());
                                        }
                                    },
                                    error -> LOGGER.error("Error: ", error)
                            );
                }
        ).listen(configuration.requestHandlerPort());
    }

    @Override
    public void stop() throws Exception {
        httpServer.close();
    }

    private void traceMessage(Message<?> message) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Got message from <template-repository> with value <{0}>", message.body());
        }
    }
}