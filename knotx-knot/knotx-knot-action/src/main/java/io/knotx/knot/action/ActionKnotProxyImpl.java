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
package io.knotx.knot.action;

import static io.knotx.knot.action.FormConstants.ACTION_FORM_ATTRIBUTES_PATTERN;
import static io.knotx.knot.action.FormConstants.FORM_ACTION_ATTR;
import static io.knotx.knot.action.FormConstants.FORM_NO_REDIRECT_SIGNAL;
import static io.knotx.knot.action.FormConstants.FRAGMENT_KNOT_PREFIX;

import io.knotx.dataobjects.AdapterRequest;
import io.knotx.dataobjects.AdapterResponse;
import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.fragments.FragmentContentExtractor;
import io.knotx.http.AllowedHeadersFilter;
import io.knotx.http.MultiMapCollector;
import io.knotx.knot.AbstractKnotProxy;
import io.knotx.rxjava.proxy.AdapterProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import rx.Single;

public class ActionKnotProxyImpl extends AbstractKnotProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionKnotVerticle.class);

  private final Vertx vertx;
  private final ActionKnotConfiguration configuration;

  ActionKnotProxyImpl(Vertx vertx, ActionKnotConfiguration configuration) {
    this.vertx = vertx;
    this.configuration = configuration;
  }

  @Override
  public Single<KnotContext> processRequest(final KnotContext knotContext) {
    return Single.just(knotContext)
        .map(this::validateForms)
        .flatMap(forms -> {
          if (knotContext.getClientRequest().getMethod() == HttpMethod.GET) {
            return Single.just(handleGetMethod(forms, knotContext));
          } else {
            FormEntity current = currentForm(forms, knotContext);
            return callActionAdapter(knotContext, current)
                .map(response -> processAdapterResponse(knotContext, forms, current, response));
          }
        })
        .onErrorReturn(error -> processError(knotContext, error));
  }

  @Override
  protected boolean shouldProcess(Set<String> knots) {
    return knots.stream().anyMatch(knot -> knot.startsWith(FRAGMENT_KNOT_PREFIX));
  }

  protected KnotContext processError(KnotContext context, Throwable error) {
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

  private KnotContext handleGetMethod(List<FormEntity> forms, KnotContext knotContext) {
    LOGGER.trace("Pass-through {} request", knotContext.getClientRequest().getMethod());
    knotContext.setTransition(DEFAULT_TRANSITION);
    forms.forEach(this::prepareFormsMarkup);
    return knotContext;
  }

  private Single<AdapterResponse> callActionAdapter(KnotContext knotContext, FormEntity current) {
    LOGGER.trace("Process form for {} ", knotContext);
    AdapterProxy adapter = AdapterProxy.createProxy(vertx, current.getAdapter().getAddress());
    return adapter.rxProcess(prepareAdapterRequest(knotContext, current.getAdapter()));
  }

  private AdapterRequest prepareAdapterRequest(KnotContext knotContext,
      ActionKnotConfiguration.AdapterMetadata metadata) {
    ClientRequest request = new ClientRequest().setPath(knotContext.getClientRequest().getPath())
        .setMethod(knotContext.getClientRequest().getMethod())
        .setFormAttributes(knotContext.getClientRequest().getFormAttributes())
        .setHeaders(getFilteredHeaders(knotContext.getClientRequest().getHeaders(),
            metadata.getAllowedRequestHeaders()));

    return new AdapterRequest().setRequest(request).setParams(new JsonObject(metadata.getParams()));
  }

  private KnotContext processAdapterResponse(KnotContext knotContext, List<FormEntity> forms, FormEntity form, AdapterResponse response) {
    final ClientResponse clientResponse = response.getResponse();
    final String signal = response.getSignal();

    if (HttpResponseStatus.OK.code() != clientResponse.getStatusCode()) {
      return errorKnotResponse(clientResponse, knotContext, form);
    } else {
      String redirectLocation = form.getUrl(signal).orElse(FORM_NO_REDIRECT_SIGNAL);
      return shouldRedirect(redirectLocation) ?
          redirectKnotResponse(knotContext, form, clientResponse, redirectLocation) :
          routeToNextKnotResponse(clientResponse, knotContext, forms, form);
    }
  }

  private KnotContext routeToNextKnotResponse(ClientResponse clientResponse, KnotContext knotContext, List<FormEntity> forms, FormEntity form) {
    LOGGER.trace("Request next transition to [{}]", DEFAULT_TRANSITION);
    JsonObject actionContext = new JsonObject()
        .put("_result", new JsonObject(clientResponse.getBody().toString()))
        .put("_response", clientResponse.toMetadataJson());

    form.getFragment().context().put("action", actionContext);
    knotContext.getClientResponse()
        .setHeaders(getFilteredHeaders(clientResponse.getHeaders(),
            form.getAdapter().getAllowedResponseHeaders())
        );
    forms.forEach(this::prepareFormsMarkup);
    knotContext.setTransition(DEFAULT_TRANSITION);
    return knotContext;
  }

  private KnotContext redirectKnotResponse(KnotContext knotContext, FormEntity form, ClientResponse clientResponse, String redirectLocation) {
    LOGGER.trace("Request redirected to [{}]", redirectLocation);
    knotContext.getClientResponse()
        .setStatusCode(HttpResponseStatus.MOVED_PERMANENTLY.code());
    MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    headers.addAll(getFilteredHeaders(clientResponse.getHeaders(),
        form.getAdapter().getAllowedResponseHeaders()));
    headers.add(HttpHeaders.LOCATION.toString(), redirectLocation);

    knotContext.getClientResponse().setHeaders(headers);
    knotContext.clearFragments();
    return knotContext;
  }

  private KnotContext errorKnotResponse(ClientResponse clientResponse, KnotContext knotContext, FormEntity form) {
    knotContext.getClientResponse()
        .setStatusCode(clientResponse.getStatusCode())
        .setHeaders(getFilteredHeaders(clientResponse.getHeaders(),
            form.getAdapter().getAllowedResponseHeaders()))
        .setBody(null);
    knotContext.clearFragments();
    return knotContext;
  }

  private List<FormEntity> validateForms(KnotContext context) {
    List<FormEntity> forms = context.getFragments().stream()
        .filter(f -> f.knots().stream().anyMatch(id -> id.startsWith(FRAGMENT_KNOT_PREFIX)))
        .map(f -> FormEntity.from(f, configuration))
        .collect(Collectors.toList());
    if (areUnique(forms)) {
      throw new IllegalStateException();
    }
    return forms;
  }

  private void prepareFormsMarkup(FormEntity form) {
    Document scriptContentDocument = FragmentContentExtractor.getUnwrappedDocument(form.getFragment());
    Element actionFormElement = scriptContentDocument.getElementsByAttribute(FORM_ACTION_ATTR).first();

    LOGGER.trace("Changing form with identifier [{}]", form.getIdentifier());
    addHiddenInputTag(actionFormElement, form.getIdentifier());
    clearFromActionAttributes(actionFormElement);
    form.getFragment().content(getFragmentContent(form.getFragment(), scriptContentDocument));
  }

  private void clearFromActionAttributes(Element item) {
    item.attributes().asList().stream()
        .filter(attr -> attr.getKey().matches(ACTION_FORM_ATTRIBUTES_PATTERN))
        .forEach(attr -> item.removeAttr(attr.getKey()));
  }

  private void addHiddenInputTag(Element form, String formIdentifier) {
    Attributes attributes = Stream.of(
        new Attribute("type", "hidden"),
        new Attribute("name", configuration.formIdentifierName()),
        new Attribute("value", formIdentifier))
        .collect(Attributes::new, Attributes::put, Attributes::addAll);
    form.prependChild(new Element(Tag.valueOf("input"), "/", attributes));
  }

  private MultiMap getFilteredHeaders(MultiMap headers, List<Pattern> allowedHeaders) {
    return headers.names().stream()
        .filter(AllowedHeadersFilter.create(allowedHeaders))
        .collect(MultiMapCollector.toMultiMap(o -> o, headers::getAll));
  }

  private String getFragmentContent(Fragment fragment, Document scriptContentDocument) {
    Document resultDocument = Jsoup.parse(fragment.content(), "UTF-8", Parser.xmlParser());
    Element scriptTag = resultDocument.child(0).empty();
    scriptContentDocument.childNodesCopy().forEach(scriptTag::appendChild);

    return resultDocument.html();
  }

  private FormEntity currentForm(List<FormEntity> forms, KnotContext knotContext) {
    return forms.stream().filter(form -> isCurrentFormFragment(form, knotContext)).findFirst()
        .orElseThrow(() -> {
          LOGGER.error("Could not form for [{}]", knotContext.getClientRequest().getFormAttributes());
          return new NoSuchElementException();
        });
  }

  private boolean isCurrentFormFragment(FormEntity form, KnotContext knotContext) {
    return getFormIdentifierFromRequest(knotContext)
        .map(formIdentifier -> form.getIdentifier().equals(formIdentifier))
        .orElse(Boolean.FALSE);
  }

  private Optional<String> getFormIdentifierFromRequest(KnotContext knotContext) {
    return Optional.ofNullable(
        knotContext.getClientRequest().getFormAttributes().get(configuration.formIdentifierName()));
  }

  private boolean areUnique(List<FormEntity> forms) {
    return forms.size() != forms.stream().map(FormEntity::getIdentifier).collect(Collectors.toSet()).size();
  }

  private boolean shouldRedirect(String signal) {
    return StringUtils.isNotEmpty(signal) && !FORM_NO_REDIRECT_SIGNAL.equals(signal);
  }

}
