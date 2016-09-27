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
package com.cognifide.knotx.engine.impl;

import com.google.common.collect.Lists;

import com.cognifide.knotx.engine.service.ServiceEntry;
import com.github.jknack.handlebars.Template;

import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

import rx.Observable;

public class TemplateSnippet {
  private static final String DATA_URI = "data-uri";

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
        .filter(attribute -> attribute.getKey().startsWith(DATA_URI))
        .map(ServiceEntry::of)
        .collect(Collectors.toList());
    return snippet;
  }

  public Element getSnippet() {
    return snippet;
  }

  public Template getCompiledSnippet() {
    return compiledSnippet;
  }

  public TemplateSnippet setCompiledSnippet(Template compiledSnippet) {
    this.compiledSnippet = compiledSnippet;
    return this;
  }

  public List<String> getCalledServicesUri() {
    return services.stream().map(ServiceEntry::getServiceUri).collect(Collectors.toList());
  }

  public Observable<ServiceEntry> getServices() {
    return Observable.from(services);
  }

  @Override
  public String toString() {
    return "TemplateSnippet@" + Integer.toHexString(hashCode());
  }
}
