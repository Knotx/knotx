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
package io.knotx.knot.action.domain;

import static io.knotx.knot.action.domain.FormConstants.ACTION_FORM_ATTRIBUTES_PATTERN;
import static io.knotx.knot.action.domain.FormConstants.FORM_ACTION_ATTR;

import io.knotx.fragments.FragmentContentExtractor;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;

public class DefaultFormTransformer implements FormTransformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFormTransformer.class);

  @Override
  public String transform(String content, String formIdAttrName, String formIdAttrValue) {
    Element scriptContentDocument = FragmentContentExtractor.unwrapContent(content);
    Element actionFormElement = scriptContentDocument.getElementsByAttribute(FORM_ACTION_ATTR).first();

    LOGGER.debug("Changing form with identifier [{}]", formIdAttrValue);
    addHiddenInputTag(actionFormElement, formIdAttrName, formIdAttrValue);
    clearFromActionAttributes(actionFormElement);
    return getFragmentContent(content, scriptContentDocument);
  }

  private void clearFromActionAttributes(Element item) {
    item.attributes().asList().stream()
        .filter(attr -> attr.getKey().matches(ACTION_FORM_ATTRIBUTES_PATTERN))
        .forEach(attr -> item.removeAttr(attr.getKey()));
  }

  private void addHiddenInputTag(Element form, String formIdAttrName, String formIdAttrValue) {
    Attributes attributes = Stream.of(
        new Attribute("type", "hidden"),
        new Attribute("name", formIdAttrName),
        new Attribute("value", formIdAttrValue))
        .collect(Attributes::new, Attributes::put, Attributes::addAll);
    form.prependChild(new Element(Tag.valueOf("input"), "/", attributes));
  }


  private String getFragmentContent(String content, Element scriptContentDocument) {
    Document resultDocument = Jsoup.parse(content, "UTF-8", Parser.xmlParser());
    Element scriptTag = resultDocument.child(0).empty();
    scriptContentDocument.childNodesCopy().forEach(scriptTag::appendChild);

    return resultDocument.html();
  }

}
