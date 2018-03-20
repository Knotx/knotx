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

import static io.knotx.knot.action.domain.FormConstants.FORM_ACTION_ATTR;
import static io.knotx.knot.action.domain.FormConstants.FORM_ADAPTER_PARAMS;
import static io.knotx.knot.action.domain.FormConstants.FORM_DEFAULT_IDENTIFIER;
import static io.knotx.knot.action.domain.FormConstants.FORM_SIGNAL_ATTR_PREFIX;
import static io.knotx.knot.action.domain.FormConstants.FRAGMENT_KNOT_PATTERN;
import static io.knotx.knot.action.domain.FormConstants.FRAGMENT_KNOT_PREFIX;

import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.exceptions.ConfigurationException;
import io.knotx.fragments.FragmentContentExtractor;
import io.knotx.knot.action.ActionSettings;
import io.knotx.knot.action.ActionKnotOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

public class FormEntity {

  private static final Logger LOGGER = LoggerFactory.getLogger(FormEntity.class);

  private Fragment fragment;

  private String identifier;

  private ActionSettings adapter;

  private JsonObject adapterParams;

  private Map<String, String> signalToUrl;

  public static FormEntity from(Fragment fragment, ActionKnotOptions options) {
    Element scriptDocument = FragmentContentExtractor.unwrapFragmentContent(fragment);
    return new FormEntity()
        .fragment(fragment)
        .identifier(getFormIdentifier(fragment))
        .adapterParams(getAdapterParams(scriptDocument))
        .adapter(getAdapterMetadata(options, getAdapterName(fragment, scriptDocument)))
        .signalToUrlMapping(getSignalToUrlMapping(scriptDocument));

  }

  public Fragment fragment() {
    return fragment;
  }

  public String identifier() {
    return identifier;
  }

  public ActionSettings adapter() {
    return adapter;
  }

  public JsonObject adapterParams() {
    return adapterParams;
  }

  public Optional<String> url(String signal) {
    return Optional.ofNullable(signalToUrl.get(signal));
  }

  public boolean current(KnotContext knotContext, String formIdAttrName) {
    return getFormIdentifierFromRequest(knotContext, formIdAttrName)
        .map(formIdentifier -> identifier().equals(formIdentifier))
        .orElse(Boolean.FALSE);
  }

  private static Optional<String> getFormIdentifierFromRequest(KnotContext knotContext,
      String formIdAttrName) {
    return Optional.ofNullable(
        knotContext.getClientRequest().getFormAttributes().get(formIdAttrName));
  }

  private FormEntity fragment(Fragment fragment) {
    this.fragment = fragment;
    return this;
  }

  private FormEntity identifier(String formIdentifier) {
    this.identifier = formIdentifier;
    return this;
  }

  private FormEntity adapter(ActionSettings adapterMetadata) {
    this.adapter = adapterMetadata;
    return this;
  }

  private FormEntity adapterParams(JsonObject adapterParams) {
    this.adapterParams = adapterParams;
    return this;
  }

  private FormEntity signalToUrlMapping(Map<String, String> signalToUrlMapping) {
    this.signalToUrl = signalToUrlMapping;
    return this;
  }

  private static String getFormIdentifier(Fragment fragment) {
    return fragment.knots().stream()
        .filter(knot -> knot.startsWith(FRAGMENT_KNOT_PREFIX))
        .map(knot -> StringUtils.removePattern(knot, FRAGMENT_KNOT_PATTERN))
        .map(id -> StringUtils.isBlank(id) ? FORM_DEFAULT_IDENTIFIER : id)
        .findFirst().orElseThrow(() -> {
          LOGGER.error("Could not find action adapter name in fragment [{}].",
              fragment);
          return new NoSuchElementException("Could not find action adapter name");
        });
  }

  private static String getAdapterName(Fragment fragment, Element scriptDocument) {
    return Optional.ofNullable(scriptDocument
        .getElementsByAttribute(FORM_ACTION_ATTR).first())
        .map(element -> element.attr(FORM_ACTION_ATTR))
        .orElseThrow(() -> {
          LOGGER.error("Could not find action adapter name in fragment [{}].",
              fragment);
          return new NoSuchElementException("Could not find action adapter name");
        });
  }

  private static JsonObject getAdapterParams(Element scriptDocument) {
    return Optional.ofNullable(scriptDocument
        .getElementsByAttribute(FORM_ADAPTER_PARAMS).first())
        .map(element -> element.attr(FORM_ADAPTER_PARAMS))
        .map(JsonObject::new)
        .orElse(null);
  }

  private static ActionSettings getAdapterMetadata(ActionKnotOptions options,
      String adapter) {
    return options.getAdapters().stream()
        .filter(metadata -> metadata.getName().equals(adapter))
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("Could not find adapter name [{}] mapping in ActionKnotOptions [{}].",
              adapter, options.getAdapters());
          return new ConfigurationException("Could not find action adapter name!");
        });
  }

  private static Map<String, String> getSignalToUrlMapping(Element scriptDocument) {
    return scriptDocument.getElementsByAttributeStarting(FORM_SIGNAL_ATTR_PREFIX).stream()
        .flatMap(element -> element.attributes().asList().stream())
        .filter(allAttr -> allAttr.getKey().startsWith(FORM_SIGNAL_ATTR_PREFIX))
        .collect(
            Collectors.toMap(e -> e.getKey().replace(FORM_SIGNAL_ATTR_PREFIX, StringUtils.EMPTY),
                Entry::getValue));
  }
}
