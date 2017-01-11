/*
 * Knot.x - Reactive microservice assembler - Fragment Assembler
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
package com.cognifide.knotx.knot.assembler.impl;

import com.cognifide.knotx.dataobjects.Fragment;
import com.cognifide.knotx.fragments.FragmentConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public enum UnprocessedFragmentStrategy {

  AS_IS {
    @Override
    protected String get(Fragment fragment) {
      return fragment.content();
    }
  },

  UNWRAP {
    @Override
    protected String get(Fragment fragment) {
      if (fragment.content().matches(FragmentConstants.ANY_SNIPPET_PATTERN)) {
        Document document = Jsoup.parseBodyFragment(fragment.content());
        Element scriptTag = document.body().child(0);
        return "<!-- SNIPPET UNWRAPED START -->" + scriptTag.unwrap().toString()
            + "<!-- SNIPPET UNWRAPED STOP -->";
      } else {
        return fragment.content();
      }
    }
  },

  IGNORE {
    @Override
    protected String get(Fragment fragment) {
      return fragment.content().matches(FragmentConstants.ANY_SNIPPET_PATTERN)
          ? "<!-- SNIPPET IGNORED -->" : fragment.content();
    }
  };

  protected abstract String get(Fragment fragment);

}
