/*
 * Knot.x - Reactive microservice assembler - Handlebars Knot
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
package com.cognifide.knotx.knot.templating;

import com.cognifide.knotx.dataobjects.Fragment;
import com.cognifide.knotx.handlebars.JsonObjectValueResolver;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class HandlebarsFragment {

  private static final Logger LOGGER = LoggerFactory.getLogger(HandlebarsFragment.class);

  private static final int MAX_FRAGMENT_CONTENT_LOG_LENGTH = 256;

  private final Fragment fragment;

  private final String unwrappedContent;

  HandlebarsFragment(Fragment fragment) {
    this.fragment = fragment;
    this.unwrappedContent = getUnwrappedContent(fragment);
  }

  String compileWith(Handlebars handlebars) {
    try {
      Template compiledFragment = handlebars.compileInline(unwrappedContent);
      LOGGER.trace("Applying context [{}] to fragment [{}]", fragment.context(),
          StringUtils
              .abbreviate(fragment.content().replaceAll("[\n\r\t]", ""),
                  MAX_FRAGMENT_CONTENT_LOG_LENGTH));
      return compiledFragment.apply(
          Context.newBuilder(fragment.context())
              .push(JsonObjectValueResolver.INSTANCE)
              .build());
    } catch (IOException e) {
      LOGGER.error("Could not process fragment [{}]", fragment.content(), e);
      throw new IllegalStateException("Handlebars fragment can not be evaluated correctly.");
    }
  }

  private String getUnwrappedContent(Fragment fragment) {
    Document document = Jsoup.parseBodyFragment(fragment.content());
    Element scriptTag = document.body().child(0);
    return scriptTag.unwrap().toString();
  }

}
