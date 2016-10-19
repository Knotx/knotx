/*
 * Knot.x - Reactive microservice assembler - View Engine Verticle
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
package com.cognifide.knotx.engine.view.parser;

import com.google.common.collect.Lists;

import com.cognifide.knotx.engine.view.service.ServiceAttributeUtil;
import com.cognifide.knotx.engine.view.service.ServiceEntry;
import com.cognifide.knotx.handlebars.JsonObjectValueResolver;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonObject;
import rx.Observable;

public class TemplateHtmlFragment implements HtmlFragment {

  private static final String DATA_SERVICE = "data-service.*";

  private static final String DATA_PARAMS = "data-params.*";

  private static final String DATA_ID = "data-id";

  private String content;

  private String dataId = StringUtils.EMPTY;

  private Template compiledFragment;

  private List<ServiceEntry> services = Lists.newArrayList();

  public TemplateHtmlFragment(String templateFragment) {
    Document document = Jsoup.parseBodyFragment(templateFragment);
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

    dataId = getDataIdAttr(attributes);

    this.content = scriptTag.unwrap().toString(); //remove outer script tag
  }

  @Override
  public String getContentWithContext(JsonObject model) {
    try {
      return compiledFragment.apply(Context.newBuilder(model).push(JsonObjectValueResolver.INSTANCE).build());
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

  @Override
  public String getDataId() {
    return dataId;
  }

  public TemplateHtmlFragment compileWith(Handlebars handlebars) throws IOException {
    this.compiledFragment = handlebars.compileInline(content);
    return this;
  }

  private String getDataIdAttr(List<Attribute> attributes) {
    Optional<Attribute> dataIdAttribute = attributes.stream().filter(attribute -> attribute.getKey().equals(DATA_ID)).findFirst();
    return dataIdAttribute.map(Attribute::getValue).orElse(StringUtils.EMPTY);
  }
}
