/*
 * Knot.x - Reactive microservice assembler - View Knot
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
package com.cognifide.knotx.knot.service.parser;

import com.cognifide.knotx.knot.service.service.ServiceAttributeUtil;
import com.cognifide.knotx.knot.service.service.ServiceEntry;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.handlebars.JsonObjectValueResolver;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonObject;
import rx.Observable;

public class TemplateHtmlFragment implements HtmlFragment {

  private static final String DATA_SERVICE = "data-service.*";
  private static final String DATA_PARAMS = "data-params.*";

  private final String unwrappedContent;
  private final Fragment fragment;
  private final List<ServiceEntry> services;
  private final JsonObject context;

  private Template compiledFragment;

  public TemplateHtmlFragment(Fragment fragment) {
    this.fragment = fragment;
    this.context = fragment.getContext();
    Document document = Jsoup.parseBodyFragment(fragment.getContent());
    Element scriptTag = document.body().child(0);

    List<Attribute> attributes = scriptTag.attributes().asList();

    Map<String, Attribute> serviceAttributes = attributes.stream()
        .filter(attribute -> attribute.getKey().matches(DATA_SERVICE))
        .collect(Collectors.toMap(attribute -> ServiceAttributeUtil.extractNamespace(attribute.getKey()), Function.identity()));

    Map<String, Attribute> paramsAttributes = attributes.stream()
        .filter(attribute -> attribute.getKey().matches(DATA_PARAMS))
        .collect(Collectors.toMap(attribute -> ServiceAttributeUtil.extractNamespace(attribute.getKey()), Function.identity()));

    services = serviceAttributes.entrySet().stream()
        .map(entry -> new ServiceEntry(entry.getValue(), paramsAttributes.get(entry.getKey())))
        .collect(Collectors.toList());

    this.unwrappedContent = scriptTag.unwrap().toString(); //remove outer script tag
  }

  @Override
  public String getContentWithContext(JsonObject model) {
    try {
      return compiledFragment.apply(Context.newBuilder(model.mergeIn(context)).push(JsonObjectValueResolver.INSTANCE).build());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Fragment getFragment() {
    return fragment;
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
    this.compiledFragment = handlebars.compileInline(unwrappedContent);
    return this;
  }

}
