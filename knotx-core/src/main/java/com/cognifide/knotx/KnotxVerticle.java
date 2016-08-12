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

import com.cognifide.knotx.api.RepositoryResponse;
import com.cognifide.knotx.template.service.ServiceEndpoint;
import com.cognifide.knotx.template.service.ServiceEndpointFacade;
import com.cognifide.knotx.template.engine.TemplateHandlerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;

@Component
public class KnotxVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnotxVerticle.class);

    @Autowired
    private KnotxConfiguration configuration;

    @Autowired
    private ServiceEndpointFacade serviceEndpointFacade;

    @Autowired
    private TemplateHandlerFactory templateHandlerFactory;

    private HttpServer httpServer;

    @Override
    public void start() throws IOException, URISyntaxException {
        LOGGER.debug(String.format("Registered <%s>", this.getClass().getSimpleName()));
        httpServer = vertx.createHttpServer();
        EventBus eventBus = vertx.eventBus();

        httpServer.requestHandler(
                request -> {
                    eventBus.<RepositoryResponse>sendObservable("template-repository", request.path())
                            .doOnNext(this::traceMessage)
                            .subscribe(
                                    reply -> {
                                        RepositoryResponse repository = reply.body();
                                        if (repository.isSuccess()) {
                                            request.response().end(repository.getData());
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
            LOGGER.trace(String.format("Got message from <template-repository> with value <%s>", message.body()));
        }
    }

    public void callService(HttpServerRequest request, String dataCallUri,
                            Handler<HttpClientResponse> serviceResponseHandler) {
        HttpClient httpClient = vertx.createHttpClient();
        Optional<? extends ServiceEndpoint> optionalServiceEndpoint = serviceEndpointFacade
                .getServiceEndpoint(dataCallUri);
        if (optionalServiceEndpoint.isPresent()) {
            final ServiceEndpoint serviceEndpoint = optionalServiceEndpoint.get();
            HttpClientRequest httpClientRequest = httpClient.get(serviceEndpoint.getPort(),
                    serviceEndpoint.getDomain(), dataCallUri, serviceResponseHandler);
            rewriteHeaders(request, httpClientRequest);
            httpClientRequest.end();
        } else {
            LOGGER.error("No provider found! Request can't be processed.");
        }
    }

    private void rewriteHeaders(HttpServerRequest request, HttpClientRequest httpClientRequest) {
        request.headers().names().stream()
                .filter(headerName ->
                        configuration.serviceCallHeaders().contains(headerName))
                .forEach(headerName ->
                        httpClientRequest.putHeader(headerName, request.getHeader(headerName))
                );
    }
}