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
package com.cognifide.knotx.knot.action.impl;

import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.Fragment;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.http.AllowedHeadersFilter;
import com.cognifide.knotx.http.MultiMapCollector;
import com.cognifide.knotx.knot.action.ActionKnotConfiguration;
import com.cognifide.knotx.knot.action.ActionKnotVerticle;
import com.cognifide.knotx.knot.action.FormConfigurationException;
import com.cognifide.knotx.proxy.KnotProxy;
import com.cognifide.knotx.rxjava.proxy.AdapterProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import java.util.List;
import java.util.NoSuchElementException;
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

public class ActionKnotProxyImpl implements KnotProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionKnotVerticle.class);

  private static final String DEFAULT_TRANSITION = "next";
  private static final String DEFAULT_FORM_IDENTIFIER = "_default_";
  private static final String ACTION_FRAGMENT_KNOT = "form";
  private static final String ACTION_FRAGMENT_KNOT_REGEXP = "form(-([A-Za-z0-9]+))*";
  private static final Pattern ACTION_FRAGMENT_KNOT_PATTERN = Pattern
      .compile(ACTION_FRAGMENT_KNOT_REGEXP);
  private static final String ACTION_FORM_ATTRIBUTES_PATTERN = "data-knotx-.*";
  private static final String ACTION_FORM_ACTION_ATTRIBUTE = "data-knotx-action";

  private final Vertx vertx;
  private final ActionKnotConfiguration configuration;

  public ActionKnotProxyImpl(Vertx vertx, ActionKnotConfiguration configuration) {
    this.vertx = vertx;
    this.configuration = configuration;
  }

  @Override
  public void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result) {
    try {
      if (HttpMethod.POST.equals(knotContext.getClientRequest().getMethod())) {
        handleFormAction(knotContext, result);
      } else {
        handleGetMethod(knotContext, result);
      }
    } catch (Exception ex) {
      LOGGER.error("Error occurred in " + this.getClass().getName() + ".", ex);
      result.handle(Future.succeededFuture(processError(knotContext, ex)));
    }
  }

  private KnotContext processError(KnotContext context, Throwable error) {
    KnotContext errorResponse = new KnotContext().setClientResponse(context.getClientResponse());
    HttpResponseStatus statusCode;
    if (error instanceof NoSuchElementException) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else if (error instanceof FormConfigurationException) {
      LOGGER.error("Form incorrectly configured [{}]", context.getClientRequest());
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    errorResponse.getClientResponse().setStatusCode(statusCode.code());
    return errorResponse;
  }

  private void traceMessage(Message<KnotContext> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body());
    }
  }

  private void handleFormAction(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result) {
    LOGGER.trace("Process form for {} ", knotContext);
    Fragment currentFragment = Optional.ofNullable(knotContext.getFragments())
        .flatMap(fragments -> fragments.stream()
            .filter(fragment -> isCurrentFormFragment(fragment, knotContext))
            .findFirst())
        .orElseThrow(() -> {
          String formIdentifier = getFormIdentifierFromRequest(knotContext).orElse("EMPTY");
          LOGGER.error("Could not find fragment with id [{}] in fragments [{}]", formIdentifier,
              knotContext.getFragments());
          return new NoSuchElementException("Fragment for [" + formIdentifier + "] not found");
        });

    String actionAdapterName = Optional.ofNullable(getScriptContentDocument(currentFragment)
        .getElementsByAttribute(ACTION_FORM_ACTION_ATTRIBUTE).first())
        .map(element -> element.attr(ACTION_FORM_ACTION_ATTRIBUTE))
        .orElseThrow(() -> {
          LOGGER.error("Could not find action adapter name in current fragment [{}].",
              currentFragment);
          return new NoSuchElementException("Could not find action adapter name");
        });

    ActionKnotConfiguration.AdapterMetadata adapterMetadata = configuration.adapterMetadatas()
        .stream()
        .filter(item -> item.getName().equals(actionAdapterName))
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("Could not find adapter name [{}] in configuration [{}]", actionAdapterName,
              configuration.adapterMetadatas());
          return new NoSuchElementException("Action adapter not found!");
        });

    AdapterProxy adapter = AdapterProxy.createProxy(vertx, adapterMetadata.getAddress());
    adapter.processObservable(prepareRequest(knotContext, adapterMetadata))
        .subscribe(
            adapterResp -> {
              final ClientResponse clientResponse = adapterResp.getResponse();
              final String signal = adapterResp.getSignal();

              if (isNotOkStatus(clientResponse)) {
                knotContext.getClientResponse()
                    .setStatusCode(clientResponse.getStatusCode())
                    .setHeaders(getFilteredHeaders(clientResponse.getHeaders(),
                        adapterMetadata.getAllowedResponseHeaders()))
                    .setBody(null);
                knotContext.clearFragments();

                result.handle(Future.succeededFuture(knotContext));
              }

              String redirectLocation = Optional
                  .ofNullable(getScriptContentDocument(currentFragment)
                      .getElementsByAttribute("data-knotx-on-" + signal).first())
                  .map(element -> element.attr("data-knotx-on-" + signal))
                  .orElseThrow(() -> {
                    LOGGER.error("Could not find signal name [{}] in fragment [{}].", signal,
                        currentFragment);
                    return new NoSuchElementException("Could not find signal in configuration!");
                  });

              if (shouldRedirect(redirectLocation)) {
                LOGGER.trace("Request redirected to [{}]", redirectLocation);
                knotContext.getClientResponse().setStatusCode(HttpResponseStatus.MOVED_PERMANENTLY.code());
                MultiMap headers = MultiMap.caseInsensitiveMultiMap();
                headers.addAll(getFilteredHeaders(clientResponse.getHeaders(),
                    adapterMetadata.getAllowedResponseHeaders()));
                headers.add(HttpHeaders.LOCATION.toString(), redirectLocation);

                knotContext.getClientResponse().setHeaders(headers);
                knotContext.clearFragments();
              } else {
                LOGGER.trace("Request next transition to [{}]", DEFAULT_TRANSITION);
                JsonObject actionContext = new JsonObject()
                    .put("_result", new JsonObject(clientResponse.getBody().toString()))
                    .put("_response", clientResponse.toMetadataJson());

                currentFragment.context().put("action", actionContext);
                knotContext.getClientResponse().setHeaders(getFilteredHeaders(clientResponse.getHeaders(),
                    adapterMetadata.getAllowedResponseHeaders())
                );
                Optional.ofNullable(knotContext.getFragments()).ifPresent(this::processFragments);
                knotContext.setTransition(DEFAULT_TRANSITION);
              }
              result.handle(Future.succeededFuture(knotContext));
            },
            err -> {
              knotContext.getClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
              result.handle(Future.succeededFuture(knotContext));
            }
        );
  }

  private boolean isNotOkStatus(ClientResponse response) {
    return HttpResponseStatus.OK.code() != response.getStatusCode();
  }

  private void handleGetMethod(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result) {
    LOGGER.trace("Pass-through {} request", knotContext.getClientRequest().getMethod());
    knotContext.setTransition(DEFAULT_TRANSITION);
    Optional.ofNullable(knotContext.getFragments()).ifPresent(this::processFragments);
    result.handle(Future.succeededFuture(knotContext));
  }

  private AdapterRequest prepareRequest(KnotContext knotContext,
      ActionKnotConfiguration.AdapterMetadata metadata) {
    ClientRequest request = new ClientRequest().setPath(knotContext.getClientRequest().getPath())
        .setMethod(knotContext.getClientRequest().getMethod())
        .setFormAttributes(knotContext.getClientRequest().getFormAttributes())
        .setHeaders(getFilteredHeaders(knotContext.getClientRequest().getHeaders(),
            metadata.getAllowedRequestHeaders()));

    return new AdapterRequest().setRequest(request).setParams(new JsonObject(metadata.getParams()));
  }

  private boolean shouldRedirect(String signal) {
    return StringUtils.isNotEmpty(signal) && !"_self".equals(signal);
  }

  private boolean isCurrentFormFragment(Fragment fragment, KnotContext knotContext) {
    return getFormIdentifierFromRequest(knotContext)
        .map(this::buildFragmentId)
        .map(fragmentId -> fragment.knots().contains(fragmentId))
        .orElse(Boolean.FALSE);
  }

  private String buildFragmentId(String requestedFormId) {
    if (requestedFormId.equalsIgnoreCase(DEFAULT_FORM_IDENTIFIER)) {
      return ACTION_FRAGMENT_KNOT;
    } else {
      return ACTION_FRAGMENT_KNOT + "-" + requestedFormId;
    }
  }

  private Optional<String> getFormIdentifierFromRequest(KnotContext knotContext) {
    return Optional.ofNullable(
        knotContext.getClientRequest().getFormAttributes().get(configuration.formIdentifierName()));
  }

  private void processFragments(List<Fragment> fragments) {
    fragments.stream()
        .filter(fragment -> fragment.knots().stream()
            .anyMatch(id -> id.startsWith(ACTION_FRAGMENT_KNOT)))
        .forEach(this::processFragment);
  }

  private void processFragment(Fragment fragment) {
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

  private String getFragmentContent(Fragment fragment, Document scriptContentDocument) {
    Document resultDocument = Jsoup.parse(fragment.content(), "UTF-8", Parser.xmlParser());
    Element scriptTag = resultDocument.child(0).empty();
    scriptContentDocument.childNodesCopy().forEach(scriptTag::appendChild);

    return resultDocument.html();
  }

  private Document getScriptContentDocument(Fragment fragment) {
    Element scriptTag = Jsoup.parseBodyFragment(fragment.content()).body().child(0);
    return Jsoup.parse(scriptTag.unwrap().toString(), "UTF-8", Parser.xmlParser());
  }

  private void clearFromActionAttributes(Element item) {
    item.attributes().asList().stream()
        .filter(attr -> attr.getKey().matches(ACTION_FORM_ATTRIBUTES_PATTERN))
        .forEach(attr -> item.removeAttr(attr.getKey()));
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

  private MultiMap getFilteredHeaders(MultiMap headers, List<Pattern> allowedHeaders) {
    return headers.names().stream()
        .filter(AllowedHeadersFilter.create(allowedHeaders))
        .collect(MultiMapCollector.toMultimap(o -> o, headers::getAll));
  }
}
