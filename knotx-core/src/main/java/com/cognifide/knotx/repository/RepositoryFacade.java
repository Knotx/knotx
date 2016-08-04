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
package com.cognifide.knotx.repository;

import io.vertx.core.AsyncResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class RepositoryFacade implements Repository<String, URI> {

    @Autowired
    private RepositoryConfiguration repositoryConfiguration;

    @Override
    public void get(URI uri, AsyncResultHandler<Template<String, URI>> handler) throws IOException {
        repositoryConfiguration.getRepositories().stream()
                .map(this::getRepositoryByMetadata)
                .filter(repository -> repository.support(uri))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Can't obtain repository for uri %s", uri)))
                .get(uri, handler);
    }

    @Override
    public boolean support(URI uri) {
        return repositoryConfiguration.getRepositories().stream()
                .map(this::getRepositoryByMetadata)
                .filter(repository -> repository.support(uri))
                .findFirst()
                .isPresent();
    }

    private Repository<String, URI> getRepositoryByMetadata(RepositoryConfiguration.RepositoryMetadata metadata) {
        return metadata.getType().create(metadata, repositoryConfiguration.getVerticle());
    }
}
