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
package com.cognifide.knotx.handler;

import com.cognifide.knotx.repository.Repository;
import com.cognifide.knotx.repository.Template;
import com.cognifide.knotx.template.TemplateHandlerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

public class IncomingRequestsHandler implements Handler<HttpServerRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncomingRequestsHandler.class);

    private final TemplateHandlerFactory templateHandlerFactory;

    private final Repository<String, URI> repository;

    public IncomingRequestsHandler(TemplateHandlerFactory templateHandlerFactory, Repository<String, URI> repository) {
        this.templateHandlerFactory = templateHandlerFactory;
        this.repository = repository;
    }

    @Override
    public void handle(HttpServerRequest request) {
        LOGGER.debug("Request handled: {}", request.absoluteURI());
        try {
            final URI requestUri = new URI(request.path());
            if (repository.support(requestUri)) {
                repository.get(requestUri, event -> {
                    final Template<String, URI> result = event.result();
                    if (result != null) {
                        LOGGER.trace("Template content: {}", result.get());
                    }
                    templateHandlerFactory.newInstance().handle(result, request);
                });
            } else {
                LOGGER.error("Can't handle request. No matching repository found for request `{}`!", request.absoluteURI());
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to obtain repository", e);
        } catch (IOException e) {
            LOGGER.error("Can't get template file!", e);
        }
    }
}
