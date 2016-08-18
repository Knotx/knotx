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
package com.cognifide.knotx.template.service;

import com.cognifide.knotx.template.ServiceConfiguration;
import com.cognifide.knotx.template.engine.TemplateEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

@Component
public class ServiceEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngine.class);

    private HttpClient httpClient;

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    //FIXME: better way of passing http client is required - probably do a nano service as verticle
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Observable<Map<String, Object>> doServiceCall(ServiceEntry serviceEntry) {
        Observable<HttpClientResponse> serviceResponse = RxHelper.get(httpClient, serviceEntry.getPort(), serviceEntry.getDomain(), serviceEntry.getServiceUri());
        LOGGER.debug("Calling {0}", serviceEntry.getServiceUri());
        return serviceResponse.flatMap(response -> Observable.just(Buffer.buffer())
                .mergeWith(response.toObservable())
                .reduce(Buffer::appendBuffer))
                .doOnNext(this::traceServiceCall)
                .flatMap(buffer -> Observable.just(buffer.toJsonObject().getMap()));
    }

    private void traceServiceCall(Buffer buffer) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Got message from service <{0}>", buffer.toString());
        }
    }

    public Observable<ServiceEntry> findServiceLocation(final ServiceEntry serviceEntry) {
        return Observable.from(serviceConfiguration.getServices())
                .filter(service -> serviceEntry.getServiceUri().matches(service.getPath())).take(1)
                .map(metadata -> serviceEntry.setServiceMetadata(metadata));
    }
}
