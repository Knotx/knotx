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
package com.cognifide.knotx;

import com.cognifide.knotx.api.RepositoryRequest;
import com.cognifide.knotx.api.RepositoryResponse;
import com.cognifide.knotx.api.TemplateEngineRequest;
import com.cognifide.knotx.repository.RepositoryVerticle;
import com.cognifide.knotx.template.TemplateEngineVerticle;
import com.cognifide.knotx.util.RepositoryRequestCodec;
import com.cognifide.knotx.util.RepositoryResponseCodec;
import com.cognifide.knotx.util.TemplateEngineRequestCodec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.RxHelper;
import rx.Observable;

@Component
public class Knotx extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(Knotx.class);

    @Autowired
    private TemplateEngineVerticle templateEngineVerticle;

    @Autowired
    private RepositoryVerticle repositoryVerticle;

    @Autowired
    private KnotxRequestHandler requestHandlerVerticle;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        vertx.eventBus().registerDefaultCodec(RepositoryResponse.class, new RepositoryResponseCodec());
        vertx.eventBus().registerDefaultCodec(TemplateEngineRequest.class, new TemplateEngineRequestCodec());
        vertx.eventBus().registerDefaultCodec(RepositoryRequest.class, new RepositoryRequestCodec());
    }

    @Override
    public void start() throws Exception {
        StringBuilder result = new StringBuilder();

        Observable.zip(RxHelper.deployVerticle(vertx, templateEngineVerticle),
                RxHelper.deployVerticle(vertx, repositoryVerticle),
                RxHelper.deployVerticle(vertx, requestHandlerVerticle),
                (template, repo, request) -> result.append(template).append(repo).append(request).toString())
                .subscribe(
                        success -> LOGGER.info("Knot.x Successfully Started"),
                        error -> LOGGER.error("Unable to start Knotx", error)
                );
    }
}
