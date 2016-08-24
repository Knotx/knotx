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

import com.google.common.collect.Lists;

import com.cognifide.knotx.engine.service.ServiceEntry;
import com.github.jknack.handlebars.Template;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import rx.Observable;

public class HandlebarsHtmlFragment implements HtmlFragment {
    private static final String DATA_CALL_URI = "data-call-uri";

    private Template compiledFragment;

    private List<ServiceEntry> services = Lists.newArrayList();

    public HandlebarsHtmlFragment(Template compiledFragment) {
        this.compiledFragment = compiledFragment;

        Document document = Jsoup.parseBodyFragment(compiledFragment.text());

        services = document.body().child(0).attributes().asList().stream()
                .filter(attribute -> attribute.getKey().startsWith(DATA_CALL_URI))
                .map(ServiceEntry::of)
                .collect(Collectors.toList());
    }

    @Override
    public String getContent(Object data) throws IOException {
        return compiledFragment.apply(data);
    }

    @Override
    public boolean hasHandlebarsTemplate() {
        return true;
    }

    @Override
    public Observable<ServiceEntry> getServices() {
        return Observable.from(services);
    }
}
