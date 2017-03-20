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
package io.knotx.fragments;

import io.knotx.dataobjects.Fragment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

public final class FragmentContentExtractor {

  private FragmentContentExtractor() {
    //util class
  }

  public static String getUnwrappedContent(Fragment fragment) {
    if (fragment == null) {
      return null;
    }

    String content = fragment.content();
    return fragment.isRaw() ? content : content.substring(content.indexOf('>') + 1, content.lastIndexOf('<'));
  }

  public static Document getUnwrappedDocument(Fragment fragment) {
    Element scriptTag = Jsoup.parseBodyFragment(fragment.content()).body().child(0);
    return Jsoup.parse(scriptTag.unwrap().toString(), "UTF-8", Parser.xmlParser());
  }

  public static Document getUnwrappedDocument(String content) {
    Element scriptTag = Jsoup.parseBodyFragment(content).body().child(0);
    return Jsoup.parse(scriptTag.unwrap().toString(), "UTF-8", Parser.xmlParser());
  }
}
