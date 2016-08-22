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
package com.cognifide.knotx.api;

import java.io.Serializable;

import io.vertx.rxjava.core.MultiMap;

public class RepositoryRequest implements Serializable {

    private static final long serialVersionUID = -4749993224323164630L;

    private final String path;

    private final MultiMap headers;

    public RepositoryRequest(String path, MultiMap headers) {
        this.path = path;
        this.headers = headers;
    }

    public String getPath() {
        return path;
    }

    public MultiMap getHeaders() {
        return headers;
    }
}

