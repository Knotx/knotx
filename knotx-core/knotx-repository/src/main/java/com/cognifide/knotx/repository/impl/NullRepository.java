/*
 * Knot.x - Reactive microservice assembler - Repository Verticle
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
package com.cognifide.knotx.repository.impl;

import com.cognifide.knotx.api.RepositoryRequest;
import com.cognifide.knotx.api.RepositoryResponse;
import com.cognifide.knotx.repository.Repository;

import io.vertx.rxjava.core.MultiMap;
import rx.Observable;

public class NullRepository implements Repository {

    @Override
    public Observable<RepositoryResponse> get(RepositoryRequest repositoryRequest) {
        String reason = String.format("No repository found for given path %s", repositoryRequest.getPath());
        //TODO and what status code should be used here?
        return Observable.just(
                RepositoryResponse.error(404, reason, MultiMap.caseInsensitiveMultiMap()));
    }

    @Override
    public boolean support(String path) {
        return false;
    }
}
