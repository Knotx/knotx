/*
 * Knot.x - Reactive microservice assembler - Action Knot Verticle
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
package com.cognifide.knotx.knot.action.handler;

import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.Fragment;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.http.AllowedHeadersFilter;
import com.cognifide.knotx.http.MultiMapCollector;
import com.cognifide.knotx.knot.action.ActionKnotConfiguration;
import com.cognifide.knotx.knot.action.FormConfigurationException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import rx.Observable;

public abstract class AbstractActionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractActionHandler.class);

  protected static final String DEFAULT_TRANSITION = "next";
  protected static final String DEFAULT_FORM_IDENTIFIER = "_default_";
  protected static final String ACTION_FRAGMENT_KNOT = "form";
  protected static final String ACTION_FRAGMENT_KNOT_REGEXP = "form(-([A-Za-z0-9]+))*";
  protected static final Pattern ACTION_FRAGMENT_KNOT_PATTERN = Pattern
      .compile(ACTION_FRAGMENT_KNOT_REGEXP);
  protected static final String ACTION_FORM_ATTRIBUTES_PATTERN = "data-knotx-.*";
  protected static final String ACTION_FORM_ACTION_ATTRIBUTE = "data-knotx-action";

  protected final ActionKnotConfiguration configuration;

  public AbstractActionHandler(ActionKnotConfiguration configuration) {
    this.configuration = configuration;
  }

  protected Optional<String> getFormIdentifierFromRequest(KnotContext knotContext) {
    return Optional.ofNullable(
        knotContext.getClientRequest().getFormAttributes().get(configuration.formIdentifierName()));
  }

  protected boolean isCurrentFormFragment(Fragment fragment, KnotContext knotContext) {
    return getFormIdentifierFromRequest(knotContext)
        .map(this::buildFragmentId)
        .map(fragmentId -> fragment.knots().contains(fragmentId))
        .orElse(Boolean.FALSE);
  }

  protected String buildFragmentId(String requestedFormId) {
    if (requestedFormId.equalsIgnoreCase(DEFAULT_FORM_IDENTIFIER)) {
      return ACTION_FRAGMENT_KNOT;
    } else {
      return ACTION_FRAGMENT_KNOT + "-" + requestedFormId;
    }
  }

  protected Document getScriptContentDocument(Fragment fragment) {
    Element scriptTag = Jsoup.parseBodyFragment(fragment.content()).body().child(0);
    return Jsoup.parse(scriptTag.unwrap().toString(), "UTF-8", Parser.xmlParser());
  }

  protected AdapterRequest prepareRequest(KnotContext knotContext,
      ActionKnotConfiguration.AdapterMetadata metadata) {
    ClientRequest request = new ClientRequest().setPath(knotContext.getClientRequest().getPath())
        .setMethod(knotContext.getClientRequest().getMethod())
        .setFormAttributes(knotContext.getClientRequest().getFormAttributes())
        .setHeaders(getFilteredHeaders(knotContext.getClientRequest().getHeaders(),
            metadata.getAllowedRequestHeaders()));

    return new AdapterRequest().setRequest(request).setParams(new JsonObject(metadata.getParams()));
  }

  protected MultiMap getFilteredHeaders(MultiMap headers, List<Pattern> allowedHeaders) {
    return headers.names().stream()
        .filter(AllowedHeadersFilter.create(allowedHeaders))
        .collect(MultiMapCollector.toMultimap(o -> o, headers::getAll));
  }

  protected boolean isNotOkStatus(ClientResponse response) {
    return HttpResponseStatus.OK.code() != response.getStatusCode();
  }

  protected boolean shouldRedirect(String signal) {
    return StringUtils.isNotEmpty(signal) && !"_self".equals(signal);
  }

  protected void processFragments(List<Fragment> fragments) {
    fragments.stream()
        .filter(fragment -> fragment.knots().stream()
            .anyMatch(id -> id.startsWith(ACTION_FRAGMENT_KNOT)))
        .forEach(this::processFragment);
  }

  protected void processFragment(Fragment fragment) {
    Document scriptContentDocument = getScriptContentDocument(fragment);
    Element actionFormElement = Optional.ofNullable(
        scriptContentDocument.getElementsByAttribute(ACTION_FORM_ACTION_ATTRIBUTE).first())
        .orElseThrow(() -> {
          LOGGER.error("Attribute {} not found!", ACTION_FORM_ACTION_ATTRIBUTE);
          return new FormConfigurationException(fragment);
        });
    checkActionFormNameDefinition(fragment, actionFormElement);

    LOGGER.trace("Changing fragment [{}]", fragment.knots());
    addHiddenInputTag(actionFormElement, fragment.knots());
    clearFromActionAttributes(actionFormElement);
    fragment.content(getFragmentContent(fragment, scriptContentDocument));
  }


  private void checkActionFormNameDefinition(Fragment fragment, Element actionFormElement) {
    String formActionName = actionFormElement.attr(ACTION_FORM_ACTION_ATTRIBUTE);
    configuration.adapterMetadatas().stream()
        .filter(adapterMetadata -> adapterMetadata.getName().equals(formActionName))
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("Form action name [{}] not found in configuration [{}]",
              configuration.adapterMetadatas());
          return new FormConfigurationException(fragment);
        });
  }

  private void addHiddenInputTag(Element form, List<String> fragmentKnots) {
    fragmentKnots.stream().
        filter(knot -> knot.startsWith(ACTION_FRAGMENT_KNOT)).
        findFirst().
        ifPresent(fragmentKnot -> {
          Matcher matcher = ACTION_FRAGMENT_KNOT_PATTERN.matcher(fragmentKnot);
          if (matcher.find()) {
            String formIdentifier = matcher.group(2);
            addHiddenInputTag(form, formIdentifier);
          }
        });
  }

  private void addHiddenInputTag(Element form, String formIdentifier) {
    Attributes attributes = Stream.of(
        new Attribute("type", "hidden"),
        new Attribute("name", configuration.formIdentifierName()),
        new Attribute("value", StringUtils.isNotBlank(formIdentifier) ? formIdentifier
            : DEFAULT_FORM_IDENTIFIER))
        .collect(Attributes::new, Attributes::put, Attributes::addAll);
    form.prependChild(new Element(Tag.valueOf("input"), "/", attributes));
  }

  private void clearFromActionAttributes(Element item) {
    item.attributes().asList().stream()
        .filter(attr -> attr.getKey().matches(ACTION_FORM_ATTRIBUTES_PATTERN))
        .forEach(attr -> item.removeAttr(attr.getKey()));
  }

  private String getFragmentContent(Fragment fragment, Document scriptContentDocument) {
    Document resultDocument = Jsoup.parse(fragment.content(), "UTF-8", Parser.xmlParser());
    Element scriptTag = resultDocument.child(0).empty();
    scriptContentDocument.childNodesCopy().forEach(scriptTag::appendChild);

    return resultDocument.html();
  }

  protected abstract Observable<KnotContext> processContext(KnotContext knotContext);

  public Observable<KnotContext> process(KnotContext knotContext) {
    try {
      return processContext(knotContext);
    } catch (Exception ex) {
      return Observable.error(ex);
    }
  }

}
