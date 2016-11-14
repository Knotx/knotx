/*
 * Knot.x - Reactive microservice assembler - Auhtorization Knot Verticle
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
package com.cognifide.knotx.knot.authorization;

import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.http.AllowedHeadersFilter;
import com.cognifide.knotx.http.MultiMapCollector;
import com.cognifide.knotx.knot.api.AbstractKnot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.eventbus.Message;

public class AuthorizationKnotVerticle extends AbstractKnot<AuthorizationKnotConfiguration> {

  private static final String DEFAULT_TRANSITION = "next";
  private static final String AUTH_FRAGMENT_ID = "auth";
  private static final String DATA_KNOTX_ON_UNAUTHORIZED = "data-knotx-on-unauthorized";
  private static final String AUTH_SERVICE_ATTRIBUTE = "data-knotx-auth";

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationKnotVerticle.class);

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  @Override
  protected AuthorizationKnotConfiguration initConfiguration(JsonObject config) {
    return new AuthorizationKnotConfiguration(config);
  }

  protected void handle(Message<JsonObject> jsonObject, Handler<JsonObject> handler) {
    final KnotContext knotContext = new KnotContext(jsonObject.body());
    LOGGER.trace("Process authorization for {} ", knotContext);
    Optional<Fragment> authFragment = findAndRemoveAuthFragment(knotContext);

    if (authFragment.isPresent()) {
      AuthorizationKnotConfiguration.AdapterMetadata adapterMetadata = findAdapter(authFragment.get());

      vertx.eventBus().<JsonObject>sendObservable(adapterMetadata.getAddress(), prepareRequest(knotContext, adapterMetadata)).subscribe(
          msg -> {
            ClientResponse clientResponse = new ClientResponse(msg.body());

            if (isAuthorized(clientResponse)) {
              LOGGER.trace("Request authorized. Trigger transition to [{}]", DEFAULT_TRANSITION);
              knotContext.setTransition(DEFAULT_TRANSITION);
            } else {
              String redirectLocation = findRedirectLocation(authFragment);
              knotContext.clientResponse().setStatusCode(HttpResponseStatus.TEMPORARY_REDIRECT);
              knotContext.clientResponse().setHeaders(prepareRedirectHeaders(getFilteredHeaders(clientResponse.headers(), adapterMetadata.getAllowedResponseHeaders()), redirectLocation));
              knotContext.clearFragments();
              LOGGER.debug("Request Unauthorized. Redirect to [{}]", redirectLocation);
            }
            handler.handle(knotContext.toJson());
          },
          err -> {
            knotContext.clientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            handler.handle(knotContext.toJson());
          }
      );
    } else {
      knotContext.setTransition(DEFAULT_TRANSITION);
      handler.handle(knotContext.toJson());
    }
  }

  private Optional<Fragment> findAndRemoveAuthFragment(KnotContext knotContext) {
    Optional<Fragment> authFragment = knotContext.fragments()
        .flatMap(fragments -> fragments.stream()
            .filter(fragment -> fragment.getId().equals(AUTH_FRAGMENT_ID))
            .findFirst());

    authFragment.ifPresent(fragment -> knotContext.fragments().get().remove(fragment));

    return authFragment;
  }

  private AuthorizationKnotConfiguration.AdapterMetadata findAdapter(Fragment authFragment) {
    String actionAdapterName = Optional.ofNullable(getScriptTag(authFragment))
        .map(element -> element.attr(AUTH_SERVICE_ATTRIBUTE))
        .orElseThrow(() -> {
          LOGGER.error("'{}' attribute does not exists in [{}].", AUTH_SERVICE_ATTRIBUTE, authFragment);
          return new NoSuchElementException("Could not find " + AUTH_SERVICE_ATTRIBUTE + " attribute");
        });

    AuthorizationKnotConfiguration.AdapterMetadata adapterMetadata = configuration.adapterMetadatas().stream()
        .filter(item -> item.getName().equals(actionAdapterName))
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("Could not find adapter name [{}] in configuration [{}]", actionAdapterName, configuration.adapterMetadatas());
          return new NoSuchElementException("Auth adapter not found!");
        });

    return adapterMetadata;
  }

  private String findRedirectLocation(Optional<Fragment> authFragment) {
    return Optional.ofNullable(getScriptTag(authFragment.get()).getElementsByAttribute(DATA_KNOTX_ON_UNAUTHORIZED).first())
        .map(element -> element.attr(DATA_KNOTX_ON_UNAUTHORIZED))
        .orElseThrow(() -> {
          LOGGER.error("Could not find Unauthorized signal in fragment [{}].", authFragment);
          return new NoSuchElementException("Could not find unauthorized signal in Snippet");
        });
  }

  private boolean isAuthorized(ClientResponse response) {
    return HttpResponseStatus.OK.equals(response.statusCode());
  }

  private JsonObject prepareRequest(KnotContext knotContext, AuthorizationKnotConfiguration.AdapterMetadata metadata) {
    ClientRequest cr = knotContext.clientRequest();
    return new JsonObject()
        .put("clientRequest", new ClientRequest()
            .setPath(cr.path())
            .setMethod(cr.method())
            .setFormAttributes(cr.formAttributes())
            .setParams(cr.params())
            .setHeaders(getFilteredHeaders(knotContext.clientRequest().headers(), metadata.getAllowedRequestHeaders())).toJson())
        .put("params", new JsonObject(metadata.getParams()));
  }

  protected KnotContext processError(KnotContext context, Throwable error) {
    HttpResponseStatus statusCode;
    if (error instanceof NoSuchElementException) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else if (error instanceof AuthorizationConfigurationException) {
      LOGGER.error("Form incorrectly configured [{}]", context.clientRequest());
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    context.clientResponse().setStatusCode(statusCode);
    context.setFragments(null);
    return context;
  }

  private Element getScriptTag(Fragment fragment) {
    return Jsoup.parseBodyFragment(fragment.getContent()).body().child(0);
  }

  private MultiMap getFilteredHeaders(MultiMap headers, List<Pattern> allowedHeaders) {
    return headers.names().stream()
        .filter(AllowedHeadersFilter.create(allowedHeaders))
        .collect(MultiMapCollector.toMultimap(o -> o, headers::getAll));
  }

  private MultiMap prepareRedirectHeaders(MultiMap responseHeaders, String redirectLocation) {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    headers.addAll(responseHeaders);
    headers.add(HttpHeaders.LOCATION.toString(), redirectLocation);

    return headers;
  }
}
