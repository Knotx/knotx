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

import com.cognifide.knotx.KnotxVerticle;

import com.cognifide.knotx.repository.template.BasicTemplate;
import com.cognifide.knotx.repository.template.Template;
import com.cognifide.knotx.result.TemplateAsyncResult;

import io.vertx.core.MultiMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import io.vertx.core.AsyncResultHandler;
import io.vertx.core.buffer.Buffer;

public class LocalRepository implements Repository<String, URI> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalRepository.class);

    private String path;

    private String catalogue;

    private KnotxVerticle verticle;

    private LocalRepository() {
        // hidden constructor
    }

    static LocalRepository of(String path, String catalogue, KnotxVerticle verticle) {
        LocalRepository repository = new LocalRepository();
        repository.path = path;
        repository.catalogue = catalogue;
        repository.verticle = verticle;
        return repository;
    }

    @Override
    public void get(URI uri, MultiMap headers, AsyncResultHandler<Template<String, URI>> handler) {
        final String localFile = catalogue + StringUtils.stripStart(uri.getPath(), "/");
        LOGGER.debug("Fetching file `{}` from local repository.", localFile);
        verticle.getVertx().fileSystem().readFile(localFile,
                (AsyncResultHandler<Buffer>) event -> handler.handle(
                        new TemplateAsyncResult<>(new BasicTemplate(uri, event))));
    }

    @Override
    public boolean support(URI uri) {
        String path = uri.getPath();
        return path.matches(this.path);
    }

}
