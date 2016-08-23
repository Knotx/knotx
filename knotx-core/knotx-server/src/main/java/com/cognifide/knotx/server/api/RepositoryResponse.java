/*
 * Knot.x - Reactive microservice assembler - HTTP Server
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
package com.cognifide.knotx.server.api;

import java.io.Serializable;

import io.vertx.rxjava.core.buffer.Buffer;
import rx.Observable;

public class RepositoryResponse implements Serializable {

    private static final long serialVersionUID = -5892594714118271978L;

    private boolean success;

    private String reason;

    private Buffer data;

    public static RepositoryResponse success(Buffer data) {
        RepositoryResponse response = new RepositoryResponse();
        response.success = true;
        response.data = data;
        return response;
    }

    public static RepositoryResponse error(String message, Object... args) {
        RepositoryResponse response = new RepositoryResponse();
        response.success = false;
        response.reason = String.format(message, args);
        return response;
    }

    public Observable<RepositoryResponse> toObservable() {
        return Observable.just(this);
    }

    public boolean isSuccess() {
        return success;
    }

    public Buffer getData() {
        return data;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "RepositoryResponse{" +
                "success=" + success +
                ", reason='" + reason + '\'' +
                ", data=" + data +
                '}';
    }
}
