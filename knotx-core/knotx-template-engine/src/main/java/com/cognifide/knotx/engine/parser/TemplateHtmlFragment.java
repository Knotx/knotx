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
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import rx.Observable;

public class TemplateHtmlFragment implements HtmlFragment {
    private static final String DATA_URI = "data-uri";

    private String content;

    private Template compiledFragment;

    private List<ServiceEntry> services = Lists.newArrayList();

    public TemplateHtmlFragment(String templateFragment) {
        Document document = Jsoup.parseBodyFragment(templateFragment);
        Element scriptTag = document.body().child(0);

        services = scriptTag.attributes().asList().stream()
                .filter(attribute -> attribute.getKey().startsWith(DATA_URI))
                .map(ServiceEntry::of)
                .collect(Collectors.toList());

        this.content = scriptTag.unwrap().toString(); //remove outer script tag
    }

    @Override
    public String getContentWithContext(Map<String, Object> context) {
        try {
            return compiledFragment.apply(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContent() {
        return compiledFragment.text();
    }

    @Override
    public boolean hasHandlebarsTemplate() {
        return true;
    }

    @Override
    public Observable<ServiceEntry> getServices() {
        return Observable.from(services);
    }

    public TemplateHtmlFragment compileWith(Handlebars handlebars) throws IOException {
        this.compiledFragment = handlebars.compileInline(content);
        return this;
    }
}
