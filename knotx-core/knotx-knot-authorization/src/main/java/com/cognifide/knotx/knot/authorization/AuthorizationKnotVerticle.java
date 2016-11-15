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

import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.AdapterResponse;
import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.http.AllowedHeadersFilter;
import com.cognifide.knotx.http.MultiMapCollector;

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
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class AuthorizationKnotVerticle extends AbstractVerticle {

  private static final String DEFAULT_TRANSITION = "next";
  private static final String AUTH_FRAGMENT_ID = "auth";
  private static final String DATA_KNOTX_ON_UNAUTHORIZED = "data-knotx-on-unauthorized";
  private static final String AUTH_SERVICE_ATTRIBUTE = "data-knotx-auth";

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationKnotVerticle.class);

  private AuthorizationKnotConfiguration configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new AuthorizationKnotConfiguration(config());
  }

  @Override
  public void start() throws Exception {
    LOGGER.debug("Starting <{}>", this.getClass().getName());

    vertx.eventBus().<KnotContext>consumer(configuration.address())
        .handler(message -> Observable.just(message)
            .doOnNext(this::traceMessage)
            .subscribe(
                result -> doAuthorization(result, message::reply),
                error -> {
                  LOGGER.error("Error occurred in Authorization Knot.", error);
                  message.reply(processError(message.body(), error));
                }
            ));
  }

  private void doAuthorization(Message<KnotContext> message, Handler<KnotContext> handler) {
    final KnotContext knotContext = message.body();
    LOGGER.trace("Process authorization for {} ", knotContext);
    Optional<Fragment> authFragment = findAndRemoveAuthFragment(knotContext);

    if (authFragment.isPresent()) {
      AuthorizationKnotConfiguration.AdapterMetadata adapterMetadata = findAdapter(authFragment.get());

      vertx.eventBus().<AdapterResponse>sendObservable(adapterMetadata.getAddress(), prepareRequest(knotContext, adapterMetadata)).subscribe(
          msg -> {
            ClientResponse clientResponse = msg.body().response();

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
            handler.handle(knotContext);
          },
          err -> {
            knotContext.clientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            handler.handle(knotContext);
          }
      );
    } else {
      knotContext.setTransition(DEFAULT_TRANSITION);
      handler.handle(knotContext);
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

  private AdapterRequest prepareRequest(KnotContext knotContext, AuthorizationKnotConfiguration.AdapterMetadata metadata) {
    ClientRequest request = new ClientRequest()
        .setPath(knotContext.clientRequest().path())
        .setMethod(knotContext.clientRequest().method())
        .setFormAttributes(knotContext.clientRequest().formAttributes())
        .setParams(knotContext.clientRequest().params())
        .setHeaders(getFilteredHeaders(knotContext.clientRequest().headers(), metadata.getAllowedRequestHeaders()));

    return new AdapterRequest().setRequest(request).setParams(new JsonObject(metadata.getParams()));
  }

  private KnotContext processError(KnotContext context, Throwable error) {
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

  private void traceMessage(Message<KnotContext> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body());
    }
  }

  private MultiMap prepareRedirectHeaders(MultiMap responseHeaders, String redirectLocation) {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    headers.addAll(responseHeaders);
    headers.add(HttpHeaders.LOCATION.toString(), redirectLocation);

    return headers;
  }
}
