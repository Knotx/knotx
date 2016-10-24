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

import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;

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
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class ActionKnotVerticle extends AbstractVerticle {

  private static final String ACTION_FRAGMENT_IDENTIFIER_REGEXP = "form-([A-Za-z0-9]+)*";

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
  public void start() throws Exception {
    LOGGER.debug("Starting <{}>", this.getClass().getName());

    vertx.eventBus().<JsonObject>consumer(configuration.address())
        .handler(message -> Observable.just(message)
            .doOnNext(this::traceMessage)
            .subscribe(
                result -> {
                  message.reply(process(result));
                },
                error -> {
                  LOGGER.error("Error occured in Action Knot.", error);
                  message.reply(processError(new KnotContext(message.body()), error).toJson());
                }
            ));
  }

  private JsonObject process(Message<JsonObject> jsonObject) {
    KnotContext knotContext = new KnotContext(jsonObject.body());

    if (HttpMethod.POST.equals(knotContext.clientRequest().method())) {
      Fragment currentFragment = knotContext.fragments()
          .flatMap(fragments -> fragments.stream()
              .filter(fragment -> isCurrentFormFragment(fragment, knotContext))
              .findFirst())
          .orElseThrow(() -> {
            String formIdentifier = getFormIdentifier(knotContext).orElse("EMPTY");
            LOGGER.error("Could not find fragment with id [{}] in fragments [{}]", formIdentifier, knotContext.fragments());
            return new NoSuchElementException("Fragment for [" + formIdentifier + "] not found");
          });


      String actionAdapterName = Optional.ofNullable(Jsoup.parse(currentFragment.getContent())
          .getElementsByAttribute(ACTION_FORM_ACTION_ATTRIBUTE).first())
          .map(element -> element.attr(ACTION_FORM_ACTION_ATTRIBUTE))
          .orElseThrow(() -> {
            LOGGER.error("Could not find action adapter name in current fragment [{}].", currentFragment);
            return new NoSuchElementException("Could not action adapter name");
          });

      ActionKnotConfiguration.AdapterMetadata adapterMetadata = configuration.adapterMetadatas().stream()
          .filter(item -> item.getName().equals(actionAdapterName))
          .findFirst()
          .orElseThrow(() -> {
            LOGGER.error("Could not find adapter name [{}] in configuration [{}]", actionAdapterName, configuration.adapterMetadatas());
            return new NoSuchElementException("Action adapter not found!");
          });

      vertx.eventBus().<JsonObject>sendObservable(adapterMetadata.getAddress(), prepareRequest(knotContext)).subscribe(
          msg -> {
            JsonObject jsonResponse = msg.body();
            ClientResponse clientResponse = new ClientResponse(jsonResponse.getJsonObject("clientResponse"));
            String transition = jsonResponse.getString("transition");

            // TODO implement POST handling

//            if (jsonResponse.getString("transition") != null) {
//               then redirect
//            } else {
//              currentFragment.get().getContext().put("_result", clientResponse.body().toString());
//              knotContext.setTransition("viewEngine");
//            }
          },
          err -> {

          }
      );
    }

    knotContext.fragments().ifPresent(this::processFragments);
    return knotContext.toJson();
  }

  private boolean isCurrentFormFragment(Fragment fragment, KnotContext knotContext) {
    return getFormIdentifier(knotContext).map(formId -> "form-" + formId).map(fragmentId -> fragmentId.equals(fragment.getId())).orElse(Boolean.FALSE);
  }

  private Optional<String> getFormIdentifier(KnotContext knotContext) {
    return Optional.ofNullable(knotContext.clientRequest().formAttributes().get(configuration.getFormIdentifierName()));
  }

  private JsonObject prepareRequest(KnotContext knotContext) {
    return new JsonObject();
  }

  private KnotContext processError(KnotContext context, Throwable error) {
    HttpResponseStatus statusCode;
    if (error instanceof NoSuchElementException) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else if (error instanceof FormConfigurationException) {
      LOGGER.error("Form incorrectly configured [{}]", context.clientRequest());
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    context.clientResponse().setStatusCode(statusCode);
    context.setFragments(null);
    return context;
  }

  private void processFragments(List<Fragment> fragments) {
    fragments.stream()
        .filter(fragment -> fragment.getId().matches(ACTION_FRAGMENT_IDENTIFIER_REGEXP))
        .forEach(this::processFragment);
  }

  private void processFragment(Fragment fragment) {
    Document scriptContentDocument = getScriptContentDocument(fragment);
    Element actionFormElement = Optional.ofNullable(scriptContentDocument.getElementsByAttribute(ACTION_FORM_ACTION_ATTRIBUTE).first()).orElseThrow(() -> {
      LOGGER.error("Attribute {} not found!", ACTION_FORM_ACTION_ATTRIBUTE);
      return new FormConfigurationException(fragment);
    });
    checkActionFormNameDefinition(fragment, actionFormElement);

    LOGGER.trace("Changing fragment [{}]", fragment.getId());
    addHiddenInputTag(actionFormElement, fragment.getId());
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
    Element scriptTag = resultDocument.child(0);
    scriptTag.children().remove();
    scriptTag.appendChild(scriptContentDocument.child(0));

    return resultDocument.html();
  }

  private Document getScriptContentDocument(Fragment fragment) {
    Element scriptTag = Jsoup.parseBodyFragment(fragment.getContent()).body().child(0);
    return Jsoup.parse(scriptTag.unwrap().toString(), "UTF-8", Parser.xmlParser());
  }

  private void clearFromActionAttributes(Element item) {
    Observable.from(item.attributes())
        .filter(attr -> attr.getKey().matches(ACTION_FORM_ATTRIBUTES_PATTERN))
        .doOnNext(attr -> item.removeAttr(attr.getKey()))
        .subscribe();
  }

  private void addHiddenInputTag(Element form, String fragmentIdentifier) {
    Matcher matcher = ACTION_FRAGMENT_IDENTIFIER_PATTERN.matcher(fragmentIdentifier);
    if (matcher.find()) {
      String formIdentifier = matcher.group(1);

      Attributes attributes = Stream.of(
          new Attribute("type", "hidden"),
          new Attribute("name", configuration.getFormIdentifierName()),
          new Attribute("value", formIdentifier))
          .collect(Attributes::new, Attributes::put, Attributes::addAll);
      form.prependChild(new Element(Tag.valueOf("input"), "/", attributes));
    }
  }

  private void traceMessage(Message<JsonObject> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body().encodePrettily());
    }
  }
}
