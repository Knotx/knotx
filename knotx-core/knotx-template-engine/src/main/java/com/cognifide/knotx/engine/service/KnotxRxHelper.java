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

import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;
import rx.functions.Action1;

public final class KnotxRxHelper {

    private KnotxRxHelper() {
    }

    public static Observable<HttpClientResponse> request(HttpClient client, HttpMethod method, int port, String domain, String uri, Action1<HttpClientRequest> requestBuilder) {
        return Observable.create(subscriber -> {
            HttpClientRequest req = client.request(method, port, domain, uri);
            Observable<HttpClientResponse> resp = req.toObservable();
            resp.subscribe(subscriber);
            requestBuilder.call(req);
            req.end();
        });
    }
}
