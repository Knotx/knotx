/*
 * Knot.x - reactive microservice assembler
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

import com.cognifide.knotx.handler.IncomingRequestsHandler;
import com.cognifide.knotx.repository.RepositoryFacade;
import com.cognifide.knotx.service.MockServiceHandler;
import com.cognifide.knotx.service.ServiceEndpoint;
import com.cognifide.knotx.service.ServiceEndpointFacade;
import com.cognifide.knotx.template.TemplateHandlerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Component
public class Server extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    @Autowired
    private RepositoryFacade repositoryFacade;

    @Autowired
    private ServiceEndpointFacade serviceEndpointFacade;

    @Autowired
    private MockServiceHandler mockServiceHandler;

    @Autowired
    private TemplateHandlerFactory templateHandlerFactory;

    @Value("#{'${service.call.headers}'.split(',')}")
    private List<String> serviceCallHeaders;

    @Value("${service.mock.enabled}")
    private Boolean mockServiceEnabled;

    @Value("${service.mock.port}")
    private Integer mockServicePort;

    @Value("${server.requestHandler.port}")
    private Integer requestHandlerPort;

    @Override
    public void start() throws IOException, URISyntaxException {
        vertx.createHttpServer()
                .requestHandler(new IncomingRequestsHandler(templateHandlerFactory, repositoryFacade))
                .listen(requestHandlerPort);
        if (mockServiceEnabled) {
            vertx.createHttpServer()
                    .requestHandler(mockServiceHandler)
                    .listen(mockServicePort);
        }
    }

    public void callService(HttpServerRequest request, String dataCallUri, Handler<HttpClientResponse> serviceResponseHandler) {
        HttpClient httpClient = vertx.createHttpClient();
        Optional<? extends ServiceEndpoint> optionalServiceEndpoint = serviceEndpointFacade.getServiceEndpoint(dataCallUri);
        if (optionalServiceEndpoint.isPresent()) {
            final ServiceEndpoint serviceEndpoint = optionalServiceEndpoint.get();
            HttpClientRequest httpClientRequest =
                    httpClient.get(serviceEndpoint.getPort(), serviceEndpoint.getDomain(), dataCallUri, serviceResponseHandler);
            rewriteHeaders(request, httpClientRequest);
            httpClientRequest.end();
        } else {
            LOGGER.error("No provider found! Request can't be processed.");
        }
    }

    private void rewriteHeaders(HttpServerRequest request, HttpClientRequest httpClientRequest) {
        request.headers().entries().stream()
                .filter(entry -> serviceCallHeaders.contains(entry.getKey()))
                .forEach(entry -> httpClientRequest.putHeader(entry.getKey(), entry.getValue()));
    }

}