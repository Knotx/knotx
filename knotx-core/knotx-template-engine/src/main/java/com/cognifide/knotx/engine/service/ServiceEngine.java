/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
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
package com.cognifide.knotx.engine.service;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import com.cognifide.knotx.engine.TemplateEngineConfiguration;

import java.util.Collections;
import java.util.Map;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

public class ServiceEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEngine.class);

    private TemplateEngineConfiguration configuration;

    private Vertx vertx;

    public ServiceEngine(Vertx vertx, TemplateEngineConfiguration serviceConfiguration) {
        this.vertx = vertx;
        this.configuration = serviceConfiguration;
    }

    public Observable<Map<String, Object>> doServiceCall(ServiceEntry serviceEntry, MultiMap headers, HttpMethod httpMethod, MultiMap formAttributes) {
        Observable<HttpClientResponse> serviceResponse = KnotxRxHelper.request(vertx.createHttpClient(), httpMethod, serviceEntry.getPort(), serviceEntry.getDomain(), serviceEntry.getServiceUri(),
                req -> buildRequestBody(req, headers, formAttributes, httpMethod));

        return serviceResponse.flatMap(this::collectBuffers);
    }

    private Observable<Map<String, Object>> collectBuffers(HttpClientResponse response) {
        return Observable.just(Buffer.buffer())
                .mergeWith(response.toObservable())
                .reduce(Buffer::appendBuffer)
                .flatMap(buffer -> Observable.just(buffer.toJsonObject().getMap()))
                .map(results -> {
                    results.put("_response", Collections.singletonMap("statusCode", response.statusCode()));
                    traceServiceCall(results);
                    return results;
                });
    }

    private void buildRequestBody(HttpClientRequest request, MultiMap headers, MultiMap formAttributes, HttpMethod httpMethod) {
        request.headers().addAll(headers);
        if (!formAttributes.isEmpty() && HttpMethod.POST.equals(httpMethod)) {
            Buffer buffer = createFormPostBody(formAttributes);
            request.headers().set("content-length", String.valueOf(buffer.length()));
            request.headers().set("content-type", "application/x-www-form-urlencoded");
            request.write(buffer);
        }
    }


    private Buffer createFormPostBody(MultiMap formAttributes) {
        Buffer buffer = Buffer.buffer();
        String formPostContent = Joiner.on("&").withKeyValueSeparator("=").join((Iterable<Map.Entry<String, String>>) formAttributes.getDelegate());
        buffer.appendString(formPostContent, Charsets.UTF_8.toString());
        return buffer;
    }

    public Observable<ServiceEntry> findServiceLocation(final ServiceEntry serviceEntry) {
        return Observable.from(configuration.getServices())
                .filter(service -> serviceEntry.getServiceUri().matches(service.getPath()))
                .first()
                .map(metadata -> serviceEntry.setServiceMetadata(metadata));
    }

    private void traceServiceCall(Map<String, Object> results) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Service call returned <{}>", buffer.toJsonObject().encodePrettily());
        }
    }
}
