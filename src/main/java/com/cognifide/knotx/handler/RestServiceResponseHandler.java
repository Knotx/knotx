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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.cognifide.knotx.cache.ServiceResponseCache;

import org.apache.commons.lang3.BooleanUtils;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerRequest;

public class RestServiceResponseHandler implements Handler<HttpClientResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceResponseHandler.class);

    private final HttpServerRequest request;

    private final String dataCallUri;

    private final ResponseHandlerHelper handlerHelper;

    private final Element snippet;

    private final ServiceResponseCache cache;

    public RestServiceResponseHandler(HttpServerRequest request, String dataCallUri, ResponseHandlerHelper handlerHelper, Element snippet, ServiceResponseCache cache) {
        this.request = request;
        this.dataCallUri = dataCallUri;
        this.handlerHelper = handlerHelper;
        this.snippet = snippet;
        this.cache = cache;
    }

    @Override
    public void handle(HttpClientResponse response) {
        response.bodyHandler(buffer -> {
            String responseContent = buffer.getString(0, buffer.length());
            LOGGER.debug("Request in: " + request.absoluteURI() + " for " + dataCallUri);
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> map = new Gson().fromJson(responseContent, mapType);
            cacheIfAllowed(map);
            handlerHelper.applyData(map);
        });
    }

    private void cacheIfAllowed(Map<String, Object> map) {
        if (!BooleanUtils.toBoolean(snippet.attr("data-no-cache"))) {
            cache.put(dataCallUri, map);
        }
    }
}
