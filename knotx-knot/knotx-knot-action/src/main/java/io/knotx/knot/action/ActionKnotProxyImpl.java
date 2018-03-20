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

import static io.knotx.knot.action.domain.FormConstants.FORM_NO_REDIRECT_SIGNAL;
import static io.knotx.knot.action.domain.FormConstants.FRAGMENT_KNOT_PREFIX;

import io.knotx.dataobjects.AdapterRequest;
import io.knotx.dataobjects.AdapterResponse;
import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.http.AllowedHeadersFilter;
import io.knotx.http.MultiMapCollector;
import io.knotx.knot.AbstractKnotProxy;
import io.knotx.knot.action.domain.FormEntity;
import io.knotx.knot.action.domain.FormTransformer;
import io.knotx.knot.action.domain.FormsFactory;
import io.knotx.reactivex.proxy.AdapterProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class ActionKnotProxyImpl extends AbstractKnotProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionKnotProxyImpl.class);

  private final Vertx vertx;
  private final ActionKnotOptions options;
  private final FormTransformer simplifier;

  ActionKnotProxyImpl(Vertx vertx, ActionKnotOptions options,
      FormTransformer formTransformer) {
    this.vertx = vertx;
    this.options = options;
    this.simplifier = formTransformer;
  }

  @Override
  public Single<KnotContext> processRequest(final KnotContext knotContext) {
    return Single.just(knotContext)
        .map(context -> FormsFactory.create(context, options))
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

  @Override
  protected KnotContext processError(KnotContext context, Throwable error) {
    LOGGER.error("Could not process template [{}]", error, context.getClientRequest().getPath());
    KnotContext errorResponse = new KnotContext().setClientResponse(context.getClientResponse());
    errorResponse.getClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    return errorResponse;
  }

  private KnotContext handleGetMethod(List<FormEntity> forms, KnotContext knotContext) {
    LOGGER.debug("Pass-through {} request", knotContext.getClientRequest().getMethod());
    knotContext.setTransition(DEFAULT_TRANSITION);
    forms.forEach(form -> form.fragment()
        .content(simplifier.transform(form.fragment().content(), options.getFormIdentifierName(),
            form.identifier())));
    return knotContext;
  }

  private Single<AdapterResponse> callActionAdapter(KnotContext knotContext, FormEntity current) {
    LOGGER.trace("Process form for {} ", knotContext);
    AdapterProxy adapter = AdapterProxy
        .createProxyWithOptions(vertx, current.adapter().getAddress(), options.getDeliveryOptions());
    return adapter.rxProcess(prepareAdapterRequest(knotContext, current));
  }

  private AdapterRequest prepareAdapterRequest(KnotContext knotContext,
      FormEntity formEntity) {
    ActionSettings metadata = formEntity.adapter();
    ClientRequest request = new ClientRequest().setPath(knotContext.getClientRequest().getPath())
        .setMethod(knotContext.getClientRequest().getMethod())
        .setFormAttributes(knotContext.getClientRequest().getFormAttributes())
        .setHeaders(getFilteredHeaders(knotContext.getClientRequest().getHeaders(),
            metadata.getAllowedRequestHeadersPatterns()));

    AdapterRequest adapterRequest = new AdapterRequest()
        .setRequest(request)
        .setParams(metadata.getParams())
        .setAdapterParams(formEntity.adapterParams());
    LOGGER.debug("Adapter [{}] call with request [{}]", metadata.getAddress(), adapterRequest);
    return adapterRequest;
  }

  private KnotContext processAdapterResponse(KnotContext knotContext, List<FormEntity> forms,
      FormEntity form, AdapterResponse response) {
    final ClientResponse clientResponse = response.getResponse();
    final String signal = response.getSignal();

    if (HttpResponseStatus.OK.code() != clientResponse.getStatusCode()) {
      return errorKnotResponse(clientResponse, knotContext, form);
    } else {
      String redirectLocation = form.url(signal).orElse(FORM_NO_REDIRECT_SIGNAL);
      return shouldRedirect(redirectLocation) ?
          redirectKnotResponse(knotContext, form, clientResponse, redirectLocation) :
          routeToNextKnotResponse(clientResponse, knotContext, forms, form);
    }
  }

  private KnotContext routeToNextKnotResponse(ClientResponse clientResponse,
      KnotContext knotContext, List<FormEntity> forms, FormEntity form) {
    LOGGER.debug("Request next transition to [{}]", DEFAULT_TRANSITION);
    JsonObject actionContext = new JsonObject()
        .put("_result", new JsonObject(clientResponse.getBody().toString()))
        .put("_response", clientResponse.toMetadataJson());

    form.fragment().context().put("action", actionContext);
    knotContext.getClientResponse()
        .setHeaders(getFilteredHeaders(clientResponse.getHeaders(),
            form.adapter().getAllowedResponseHeadersPatterns())
        );
    forms.forEach(f -> f.fragment()
        .content(simplifier
            .transform(f.fragment().content(), options.getFormIdentifierName(), f.identifier())));
    knotContext.setTransition(DEFAULT_TRANSITION);
    return knotContext;
  }

  private KnotContext redirectKnotResponse(KnotContext knotContext, FormEntity form,
      ClientResponse clientResponse, String redirectLocation) {
    LOGGER.debug("Request redirected to [{}]", redirectLocation);
    knotContext.getClientResponse()
        .setStatusCode(HttpResponseStatus.MOVED_PERMANENTLY.code());
    MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    headers.addAll(getFilteredHeaders(clientResponse.getHeaders(),
        form.adapter().getAllowedResponseHeadersPatterns()));
    headers.add(HttpHeaders.LOCATION.toString(), redirectLocation);

    knotContext.getClientResponse().setHeaders(headers);
    knotContext.clearFragments();
    return knotContext;
  }

  private KnotContext errorKnotResponse(ClientResponse clientResponse, KnotContext knotContext,
      FormEntity form) {
    knotContext.getClientResponse()
        .setStatusCode(clientResponse.getStatusCode())
        .setHeaders(getFilteredHeaders(clientResponse.getHeaders(),
            form.adapter().getAllowedResponseHeadersPatterns()))
        .setBody(Buffer.buffer());
    knotContext.clearFragments();
    return knotContext;
  }


  private MultiMap getFilteredHeaders(MultiMap headers, List<Pattern> allowedHeaders) {
    return headers.names().stream()
        .filter(AllowedHeadersFilter.create(allowedHeaders))
        .collect(MultiMapCollector.toMultiMap(o -> o, headers::getAll));
  }

  private FormEntity currentForm(List<FormEntity> forms, KnotContext knotContext) {
    return forms.stream()
        .filter(form -> form.current(knotContext, options.getFormIdentifierName())).findFirst()
        .orElseThrow(() -> {
          LOGGER
              .error("No form attribute [{}] matched with forms identifiers [{}]",
                  knotContext.getClientRequest().getFormAttributes(),
                  forms.stream().map(FormEntity::identifier).toArray());
          return new IllegalStateException("Could not match form identifiers!");
        });
  }

  private boolean shouldRedirect(String signal) {
    return StringUtils.isNotEmpty(signal) && !FORM_NO_REDIRECT_SIGNAL.equals(signal);
  }

}
