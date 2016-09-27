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

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import rx.Observable;

public class RawHtmlFragment implements HtmlFragment {

    private String fragment;

    private boolean isHandlebarsTemplate;

    public RawHtmlFragment(String fragment, boolean isHandlebarsTemplate) {
        this.fragment = fragment;
        this.isHandlebarsTemplate = isHandlebarsTemplate;
    }

    @Override
    public String getContentWithContext(Map<String, Object> context) {
        return fragment;
    }

    @Override
    public String getContent() {
        return fragment;
    }

    @Override
    public boolean hasHandlebarsTemplate() {
        return isHandlebarsTemplate;
    }

    @Override
    public Observable<ServiceEntry> getServices() {
        return Observable.empty();
    }

    @Override
    public String getDataId() {
        return StringUtils.EMPTY;
    }
}
