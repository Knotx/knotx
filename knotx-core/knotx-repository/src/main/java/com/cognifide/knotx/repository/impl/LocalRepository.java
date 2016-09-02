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

import org.apache.commons.lang3.StringUtils;

import java.nio.file.NoSuchFileException;

import io.vertx.core.file.OpenOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.file.AsyncFile;
import io.vertx.rxjava.core.file.FileSystem;
import rx.Observable;

public class LocalRepository implements Repository {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalRepository.class);

    private String path;

    private String catalogue;

    private FileSystem fileSystem;

    private LocalRepository() {
        // Hidden constructor
    }

    static LocalRepository of(String path, String catalogue, FileSystem fileSystem) {
        LocalRepository repository = new LocalRepository();
        repository.path = path;
        repository.catalogue = catalogue;
        repository.fileSystem = fileSystem;
        return repository;
    }

    @Override
    public Observable<RepositoryResponse> get(RepositoryRequest repositoryRequest) {
        final String localFilePath = catalogue + StringUtils.stripStart(repositoryRequest.getPath(), "/");
        LOGGER.trace("Fetching file `{}` from local repository.", localFilePath);

        return fileSystem.openObservable(localFilePath, new OpenOptions())
                .flatMap(AsyncFile::toObservable)
                .flatMap(buffer -> RepositoryResponse.success(buffer.toString(), MultiMap.caseInsensitiveMultiMap()).toObservable())
                .defaultIfEmpty(RepositoryResponse.success("", MultiMap.caseInsensitiveMultiMap()))
                .onErrorReturn(error -> {
                    LOGGER.error("Error reading template file from file system", error);

                    int statusCode;
                    if (error.getCause().getClass().equals(NoSuchFileException.class)) {
                        statusCode = 404;
                    } else {
                        statusCode = 500;
                    }
                    return RepositoryResponse
                            .error(statusCode, error.getMessage(), MultiMap.caseInsensitiveMultiMap());
                });
    }

    @Override
    public boolean support(String path) {
        return path.matches(this.path);
    }
}
