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

import java.io.Serializable;

import com.cognifide.knotx.repository.template.Template;

import io.vertx.core.AsyncResultHandler;
import io.vertx.core.MultiMap;

public interface Repository<T, ID extends Serializable> {

    /**
     * Method allow obtain a template from the repository.
     *
     * @param id template identifier
     * @param requestHeaders headers that are going to be rewritten to template request call
     * @param handler implementation of handler that will handle result of call for template
     */
    void get(ID id, MultiMap requestHeaders, AsyncResultHandler<Template<T, ID>> handler);

    /**
     * Method performs call to the repository and returns if ID is supported
     *
     * @param id template identifier
     * @return true if given identifier is supported by the repository
     */
    boolean support(ID id);

}
