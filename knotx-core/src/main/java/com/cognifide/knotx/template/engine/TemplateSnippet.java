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
package com.cognifide.knotx.template.engine;

import com.google.common.collect.Lists;

import com.github.jknack.handlebars.Template;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import rx.Observable;

public class TemplateSnippet {
    private static final String DATA_CALL_URI = "data-call-uri";

    private Element snippet;

    private Template compiledSnippet;

    private List<ServiceEntry> services = Lists.newArrayList();

    private TemplateSnippet() {
        // No constructor
    }

    public static TemplateSnippet raw(Element element) {
        TemplateSnippet snippet = new TemplateSnippet();
        snippet.snippet = element;

        snippet.services = element.attributes().asList().stream()
                .filter(attribute -> attribute.getKey().startsWith(DATA_CALL_URI))
                .map(ServiceEntry::of)
                .collect(Collectors.toList());
        return snippet;
    }

    public TemplateSnippet setCompiledSnippet(Template compiledSnippet) {
        this.compiledSnippet = compiledSnippet;
        return this;
    }

    public Element getSnippet() {
        return snippet;
    }

    public Template getCompiledSnippet() {
        return compiledSnippet;
    }

    public Observable<ServiceEntry> getServices() {
        return Observable.from(services);
    }

    public static class ServiceEntry {
        private String relatedAttribute;
        private String serviceUri;
        private Map<String, Object> serviceResult;

        private ServiceEntry() {
            //Hidden constructors
        }

        public static ServiceEntry of(Attribute serviceAttribute) {
            ServiceEntry entry = new ServiceEntry();
            entry.relatedAttribute = serviceAttribute.getKey();
            entry.serviceUri = serviceAttribute.getValue();
            return entry;
        }

        public ServiceEntry setResult(Map<String, Object> serviceResult) {
            this.serviceResult = serviceResult;
            return this;
        }

        public String getRelatedAttribute() {
            return relatedAttribute;
        }

        public String getServiceUri() {
            return serviceUri;
        }

        public Map<String, Object> getServiceResult() {
            return serviceResult;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ServiceEntry that = (ServiceEntry) o;

            return serviceUri.equals(that.serviceUri);

        }

        @Override
        public int hashCode() {
            return serviceUri.hashCode();
        }
    }
}
