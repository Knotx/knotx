/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.knot.templating.impl;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.knotx.dataobjects.Fragment;
import io.knotx.fragments.FragmentContentExtractor;
import io.knotx.knot.templating.handlebars.JsonObjectValueResolver;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

class HandlebarsFragment {

  private static final Logger LOGGER = LoggerFactory.getLogger(HandlebarsFragment.class);

  private static final int MAX_FRAGMENT_CONTENT_LOG_LENGTH = 256;

  private final Fragment fragment;

  private final String unwrappedContent;

  HandlebarsFragment(Fragment fragment) {
    this.fragment = fragment;
    this.unwrappedContent = FragmentContentExtractor.unwrappedContent(fragment);
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

}
