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
package com.cognifide.knotx.service;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

@Component
public class MockServiceHandler implements Handler<HttpServerRequest> {

    private final Logger LOGGER = LoggerFactory.getLogger(MockServiceHandler.class);

    @SuppressWarnings("unused")
    @Value("${service.mock.catalogue}")
    private String catalogue;

    @Override
    public void handle(HttpServerRequest event) {
        String resourcePath = catalogue + event.path();
        String htmlContent = "";
        try {
            URL resourceUrl = this.getClass().getClassLoader().getResource(resourcePath);
            if (resourceUrl != null) {
                URL url = Resources.getResource(resourcePath);
                htmlContent = Resources.toString(url, Charsets.UTF_8);
                LOGGER.info("Mocked request [{}] fetch data from file [{}]", event.path(), resourcePath);
            }
        } catch (IOException e) {
            LOGGER.error("Could not read content!", e);
        }
        event.response().end(htmlContent);
    }

}
