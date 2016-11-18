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
package com.cognifide.knotx.templating.wrappers;

import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.handlebars.JsonObjectValueResolver;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import io.vertx.core.json.JsonObject;

public class TemplateHtmlFragment implements HtmlFragment {

  private final String unwrappedContent;
  private final Fragment fragment;
  private final JsonObject context;

  private Template compiledFragment;

  public TemplateHtmlFragment(Fragment fragment) {
    this.fragment = fragment;
    this.context = fragment.getContext();
    Document document = Jsoup.parseBodyFragment(fragment.getContent());
    Element scriptTag = document.body().child(0);
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

  public TemplateHtmlFragment compileWith(Handlebars handlebars) throws IOException {
    this.compiledFragment = handlebars.compileInline(unwrappedContent);
    return this;
  }
}