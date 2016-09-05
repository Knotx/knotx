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

import com.cognifide.knotx.repository.RepositoryConfiguration;
import com.cognifide.knotx.repository.Repository;
import com.cognifide.knotx.repository.RepositoryBuilder;
import com.cognifide.knotx.repository.RepositoryMetadataValidator;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

import io.vertx.rxjava.core.Vertx;

public enum RepositoryType implements RepositoryBuilder, RepositoryMetadataValidator {

    LOCAL {
        @Override
        public Repository create(RepositoryConfiguration.RepositoryMetadata metadata,
                                 Vertx vertx) {
            return LocalRepository.of(metadata.getPath(), metadata.getCatalogue(), vertx.fileSystem());
        }

        @Override
        public boolean validate(RepositoryConfiguration.RepositoryMetadata metadata) {
            return isNotEmpty(metadata.getPath());
        }
    },

    REMOTE {
        @Override
        public Repository create(RepositoryConfiguration.RepositoryMetadata metadata,
                                 Vertx vertx) {
            return HttpRepository.of(metadata.getPath(), metadata.getDomain(), metadata.getPort(),
                    vertx);
        }

        @Override
        public boolean validate(RepositoryConfiguration.RepositoryMetadata metadata) {
            return isNotEmpty(metadata.getPath(), metadata.getDomain()) && metadata.getPort() != null;
        }
    };

    private static boolean isNotEmpty(String... values) {
        return !Stream.of(values).anyMatch(StringUtils::isBlank);
    }


    public abstract Repository create(RepositoryConfiguration.RepositoryMetadata metadata,
                                      Vertx vertx);

    @Override
    public abstract boolean validate(RepositoryConfiguration.RepositoryMetadata metadata);
}
