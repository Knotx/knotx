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
package com.cognifide.knotx.knot.action;

import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.AdapterResponse;
import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.http.AllowedHeadersFilter;
import com.cognifide.knotx.http.MultiMapCollector;
import com.cognifide.knotx.knot.api.AbstractKnot;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.eventbus.Message;

public class ActionKnotVerticle extends AbstractKnot<ActionKnotConfiguration> {

  private static final String DEFAULT_TRANSITION = "next";
  private static final String DEFAULT_FORM_IDENTIFIER = "_default_";
  private static final String ACTION_FRAGMENT_IDENTIFIER = "form";
  private static final String ACTION_FRAGMENT_IDENTIFIER_REGEXP = "form(-([A-Za-z0-9]+))*";
  private static final Pattern ACTION_FRAGMENT_IDENTIFIER_PATTERN = Pattern.compile(ACTION_FRAGMENT_IDENTIFIER_REGEXP);
  private static final String ACTION_FORM_ATTRIBUTES_PATTERN = "data-knotx-.*";
  private static final String ACTION_FORM_ACTION_ATTRIBUTE = "data-knotx-action";
  private static final Logger LOGGER = LoggerFactory.getLogger(ActionKnotVerticle.class);

  private ActionKnotConfiguration configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new ActionKnotConfiguration(config());
  }

  @Override
  protected ActionKnotConfiguration initConfiguration(JsonObject config) {
    return new ActionKnotConfiguration(config);
  }

  @Override
  protected void process(Message<KnotContext> message, Handler<KnotContext> handler) {
    KnotContext knotContext = message.body();
    if (HttpMethod.POST.equals(knotContext.clientRequest().method())) {
      handleFormAction(knotContext, handler);
    } else {
      handleGetMethod(handler, knotContext);
    }
  }

  @Override
  protected KnotContext processError(KnotContext context, Throwable error) {
    KnotContext errorResponse = new KnotContext().setClientResponse(context.clientResponse());
    HttpResponseStatus statusCode;
    if (error instanceof NoSuchElementException) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else if (error instanceof FormConfigurationException) {
      LOGGER.error("Form incorrectly configured [{}]", context.clientRequest());
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    errorResponse.clientResponse().setStatusCode(statusCode);
    return errorResponse;
  }

  private void handleFormAction(KnotContext knotContext, Handler<KnotContext> handler) {
    LOGGER.trace("Process form for {} ", knotContext);
    Fragment currentFragment = knotContext.fragments()
        .flatMap(fragments -> fragments.stream()
            .filter(fragment -> isCurrentFormFragment(fragment, knotContext))
            .findFirst())
        .orElseThrow(() -> {
          String formIdentifier = getFormIdentifierFromRequest(knotContext).orElse("EMPTY");
          LOGGER.error("Could not find fragment with id [{}] in fragments [{}]", formIdentifier, knotContext.fragments());
          return new NoSuchElementException("Fragment for [" + formIdentifier + "] not found");
        });


    String actionAdapterName = Optional.ofNullable(getScriptContentDocument(currentFragment)
        .getElementsByAttribute(ACTION_FORM_ACTION_ATTRIBUTE).first())
        .map(element -> element.attr(ACTION_FORM_ACTION_ATTRIBUTE))
        .orElseThrow(() -> {
          LOGGER.error("Could not find action adapter name in current fragment [{}].", currentFragment);
          return new NoSuchElementException("Could not find action adapter name");
        });

    ActionKnotConfiguration.AdapterMetadata adapterMetadata = configuration.adapterMetadatas().stream()
        .filter(item -> item.getName().equals(actionAdapterName))
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("Could not find adapter name [{}] in configuration [{}]", actionAdapterName, configuration.adapterMetadatas());
          return new NoSuchElementException("Action adapter not found!");
        });

    vertx.eventBus().<AdapterResponse>sendObservable(adapterMetadata.getAddress(), prepareRequest(knotContext, adapterMetadata))
        .subscribe(
            msg -> {
              final ClientResponse clientResponse = msg.body().response();
              final String signal = msg.body().signal();

              if (isNotOkStatus(clientResponse)) {
                knotContext.clientResponse()
                    .setStatusCode(clientResponse.statusCode())
                    .setHeaders(getFilteredHeaders(clientResponse.headers(), adapterMetadata.getAllowedResponseHeaders()))
                    .setBody(null);
                knotContext.clearFragments();

                handler.handle(knotContext);
              }

              String redirectLocation = Optional.ofNullable(getScriptContentDocument(currentFragment)
                  .getElementsByAttribute("data-knotx-on-" + signal).first())
                  .map(element -> element.attr("data-knotx-on-" + signal))
                  .orElseThrow(() -> {
                    LOGGER.error("Could not find signal name [{}] in fragment [{}].", signal, currentFragment);
                    return new NoSuchElementException("Could not find signal in configuration!");
                  });

              if (shouldRedirect(redirectLocation)) {
                LOGGER.trace("Request redirected to [{}]", redirectLocation);
                knotContext.clientResponse().setStatusCode(HttpResponseStatus.MOVED_PERMANENTLY);
                MultiMap headers = MultiMap.caseInsensitiveMultiMap();
                headers.addAll(getFilteredHeaders(clientResponse.headers(), adapterMetadata.getAllowedResponseHeaders()));
                headers.add(HttpHeaders.LOCATION.toString(), redirectLocation);

                knotContext.clientResponse().setHeaders(headers);
                knotContext.clearFragments();
              } else {
                LOGGER.trace("Request next transition to [{}]", DEFAULT_TRANSITION);
                JsonObject actionContext = new JsonObject()
                    .put("_result", new JsonObject(clientResponse.body().toString()))
                    .put("_response", clientResponse.toMetadataJson());

                currentFragment.getContext().put("action", actionContext);
                knotContext.clientResponse().setHeaders(getFilteredHeaders(clientResponse.headers(), adapterMetadata.getAllowedResponseHeaders())
                );
                knotContext.fragments().ifPresent(this::processFragments);
                knotContext.setTransition(DEFAULT_TRANSITION);
              }
              handler.handle(knotContext);
            },
            err -> {
              knotContext.clientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);
              handler.handle(knotContext);
            }
        );
  }

  private boolean isNotOkStatus(ClientResponse response) {
    return !HttpResponseStatus.OK.equals(response.statusCode());
  }

  private void handleGetMethod(Handler<KnotContext> handler, KnotContext knotContext) {
    LOGGER.trace("Pass-through {} request", knotContext.clientRequest().method());
    knotContext.setTransition(DEFAULT_TRANSITION);
    knotContext.fragments().ifPresent(this::processFragments);
    handler.handle(knotContext);
  }

  private AdapterRequest prepareRequest(KnotContext knotContext, ActionKnotConfiguration.AdapterMetadata metadata) {
    ClientRequest request = new ClientRequest().setPath(knotContext.clientRequest().path())
        .setMethod(knotContext.clientRequest().method())
        .setFormAttributes(knotContext.clientRequest().formAttributes())
        .setHeaders(getFilteredHeaders(knotContext.clientRequest().headers(), metadata.getAllowedRequestHeaders()));

    return new AdapterRequest().setRequest(request).setParams(new JsonObject(metadata.getParams()));
  }

  private boolean shouldRedirect(String signal) {
    return StringUtils.isNotEmpty(signal) && !"_self".equals(signal);
  }

  private boolean isCurrentFormFragment(Fragment fragment, KnotContext knotContext) {
    return getFormIdentifierFromRequest(knotContext)
        .map(this::buildFragmentId)
        .map(fragmentId -> fragment.identifiers().contains(fragmentId))
        .orElse(Boolean.FALSE);
  }

  private String buildFragmentId(String requestedFormId) {
    if (requestedFormId.equalsIgnoreCase(DEFAULT_FORM_IDENTIFIER)) {
      return ACTION_FRAGMENT_IDENTIFIER;
    } else {
      return ACTION_FRAGMENT_IDENTIFIER + "-" + requestedFormId;
    }
  }

  private Optional<String> getFormIdentifierFromRequest(KnotContext knotContext) {
    return Optional.ofNullable(knotContext.clientRequest().formAttributes().get(configuration.formIdentifierName()));
  }

  private void processFragments(List<Fragment> fragments) {
    fragments.stream()
        .filter(fragment -> fragment.identifiers().stream().filter(id -> id.startsWith(ACTION_FRAGMENT_IDENTIFIER)).findFirst().isPresent())
        .forEach(this::processFragment);
  }

  private void processFragment(Fragment fragment) {
    Document scriptContentDocument = getScriptContentDocument(fragment);
    Element actionFormElement = Optional.ofNullable(scriptContentDocument.getElementsByAttribute(ACTION_FORM_ACTION_ATTRIBUTE).first())
        .orElseThrow(() -> {
          LOGGER.error("Attribute {} not found!", ACTION_FORM_ACTION_ATTRIBUTE);
          return new FormConfigurationException(fragment);
        });
    checkActionFormNameDefinition(fragment, actionFormElement);

    LOGGER.trace("Changing fragment [{}]", fragment.identifiers());
    addHiddenInputTag(actionFormElement, fragment.identifiers());
    clearFromActionAttributes(actionFormElement);
    fragment.setContent(getFragmentContent(fragment, scriptContentDocument));
  }

  private void checkActionFormNameDefinition(Fragment fragment, Element actionFormElement) {
    String formActionName = actionFormElement.attr(ACTION_FORM_ACTION_ATTRIBUTE);
    configuration.adapterMetadatas().stream()
        .filter(adapterMetadata -> adapterMetadata.getName().equals(formActionName))
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("Form action name [{}] not found in configuration [{}]", configuration.adapterMetadatas());
          return new FormConfigurationException(fragment);
        });
  }

  private String getFragmentContent(Fragment fragment, Document scriptContentDocument) {
    Document resultDocument = Jsoup.parse(fragment.getContent(), "UTF-8", Parser.xmlParser());
    Element scriptTag = resultDocument.child(0).empty();
    scriptContentDocument.childNodesCopy().forEach(scriptTag::appendChild);

    return resultDocument.html();
  }

  private Document getScriptContentDocument(Fragment fragment) {
    Element scriptTag = Jsoup.parseBodyFragment(fragment.getContent()).body().child(0);
    return Jsoup.parse(scriptTag.unwrap().toString(), "UTF-8", Parser.xmlParser());
  }

  private void clearFromActionAttributes(Element item) {
    item.attributes().asList().stream()
        .filter(attr -> attr.getKey().matches(ACTION_FORM_ATTRIBUTES_PATTERN))
        .forEach(attr -> item.removeAttr(attr.getKey()));
  }

  private void addHiddenInputTag(Element form, List<String> fragmentIdentifiers) {
    fragmentIdentifiers.stream().filter(id -> id.startsWith(ACTION_FRAGMENT_IDENTIFIER)).findFirst().ifPresent(fragmentIdentifier -> {
      Matcher matcher = ACTION_FRAGMENT_IDENTIFIER_PATTERN.matcher(fragmentIdentifier);
      if (matcher.find()) {
        String formIdentifier = matcher.group(2);

        Attributes attributes = Stream.of(
            new Attribute("type", "hidden"),
            new Attribute("name", configuration.formIdentifierName()),
            new Attribute("value", StringUtils.isNotBlank(formIdentifier) ? formIdentifier : DEFAULT_FORM_IDENTIFIER))
            .collect(Attributes::new, Attributes::put, Attributes::addAll);
        form.prependChild(new Element(Tag.valueOf("input"), "/", attributes));
      }
    });
  }

  private MultiMap getFilteredHeaders(MultiMap headers, List<Pattern> allowedHeaders) {
    return headers.names().stream()
        .filter(AllowedHeadersFilter.create(allowedHeaders))
        .collect(MultiMapCollector.toMultimap(o -> o, headers::getAll));
  }

}
