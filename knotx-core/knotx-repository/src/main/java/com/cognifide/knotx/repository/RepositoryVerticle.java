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
package com.cognifide.knotx.repository;

import com.google.common.collect.Lists;

import com.cognifide.knotx.api.RepositoryRequest;
import com.cognifide.knotx.api.RepositoryResponse;
import com.cognifide.knotx.repository.impl.NullRepository;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class RepositoryVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryVerticle.class);

    private RepositoryConfiguration repositoryConfiguration;

    private List<Repository> repositories = Lists.newArrayList();

    private String serviceName;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        JsonObject config = config().getJsonObject("config");

        this.serviceName = config.getString("service.name");
        this.repositoryConfiguration = new RepositoryConfiguration(config);

        repositories = repositoryConfiguration.getRepositories()
                .stream()
                .map(this::getRepositoryByMetadata)
                .collect(Collectors.toList());
    }

    @Override
    public void start() throws Exception {
        LOGGER.debug("Registered <{}>", this.getClass().getSimpleName());

        EventBus eventBus = vertx.eventBus();

        Observable<Message<JsonObject>> messageObservable = eventBus.<JsonObject>consumer(serviceName).toObservable();

        messageObservable
                .doOnNext(this::traceMessage)
                .flatMap(this::getTemplateContent, Pair::of)
                .subscribe(
                        response -> response.getLeft().reply(response.getRight().toJsonObject()),
                        error -> LOGGER.error("Unable to get template from the repository", error)
                );
    }

    private Observable<RepositoryResponse> getTemplateContent(final Message<JsonObject> repoMessage) {
        final RepositoryRequest repoRequest = new RepositoryRequest(repoMessage.body());

        return Observable.just(findRepository(repoRequest.getPath()))
                .flatMap(repo -> repo.get(repoRequest));
    }

    private Repository findRepository(final String path) {
        return repositories.stream()
                .filter(repo -> repo.support(path))
                .findFirst()
                .orElse(new NullRepository());
    }

    private Repository getRepositoryByMetadata(RepositoryConfiguration.RepositoryMetadata metadata) {
        return metadata.getType().create(metadata, vertx);
    }

    private void traceMessage(Message<JsonObject> message) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body().encodePrettily());
        }
    }
}
