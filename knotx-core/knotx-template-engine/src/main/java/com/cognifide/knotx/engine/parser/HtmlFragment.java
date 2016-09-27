/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
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
package com.cognifide.knotx.engine.parser;

import com.cognifide.knotx.engine.service.ServiceEntry;

import java.util.Map;

import rx.Observable;

public interface HtmlFragment {
    /**
     * Returns the Fragment content with applied context data. Only have effect if fragment
     * is using templating language (e.g. Handlebars)
     *
     * @param context - Context data
     * @return String - html fragment with applied cotext data
     */
    String getContentWithContext(Map<String, Object> context);

    /**
     * Gets the raw HTML content of the fragment
     */
    String getContent();

    /**
     * Checks if the fragment is Handlebars template or not
     */
    boolean hasHandlebarsTemplate();

    /**
     * Gets Observable emitting Service Entries for given fragment
     */
    Observable<ServiceEntry> getServices();

    /**
     * Gets 'data id' attribute
     */
    String getDataId();
}
