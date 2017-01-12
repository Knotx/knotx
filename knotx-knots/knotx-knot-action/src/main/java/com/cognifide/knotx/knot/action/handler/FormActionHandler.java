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

import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.Fragment;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.knot.action.ActionKnotConfiguration;
import com.cognifide.knotx.rxjava.proxy.AdapterProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import java.util.NoSuchElementException;
import java.util.Optional;
import rx.Observable;

public class FormActionHandler extends AbstractActionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(FormActionHandler.class);
  private final Vertx vertx;

  public FormActionHandler(Vertx vertx, ActionKnotConfiguration configuration) {
    super(configuration);
    this.vertx = vertx;
  }

  @Override
  public Observable<KnotContext> processContext(KnotContext knotContext) {
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

    return adapter.processObservable(prepareRequest(knotContext, adapterMetadata))
        .map(
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

                return knotContext;
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
              return knotContext;
            }
        )
        .onErrorReturn(error -> {
          knotContext.getClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
          return knotContext;
        });
  }

}
