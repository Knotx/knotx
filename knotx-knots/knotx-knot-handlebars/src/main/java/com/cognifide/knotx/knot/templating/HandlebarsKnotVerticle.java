/*
 * Knot.x - Reactive microservice assembler - Handlebars Knot
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
package com.cognifide.knotx.knot.templating;

import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.handlebars.CustomHandlebarsHelper;
import com.cognifide.knotx.knot.api.AbstractKnot;
import com.cognifide.knotx.knot.templating.helpers.DefaultHandlebarsHelpers;
import com.github.jknack.handlebars.Handlebars;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.ServiceLoader;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;

public class HandlebarsKnotVerticle extends AbstractKnot<HandlebarsKnotConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(HandlebarsKnotVerticle.class);

  private static final String START_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- start compiled snippet -->";

  private static final String END_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- end compiled snippet -->";

  private static final String DEFAULT_HANDLEBARS_TRANSITION = "next";

  private static final String SUPPORTED_FRAGMENT_KNOT = "handlebars";

  private Handlebars handlebars;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    initHandlebars();
  }

  @Override
  protected Observable<KnotContext> process(KnotContext msg) {
    return Observable.create(observer -> {
      try {
        msg.setTransition(DEFAULT_HANDLEBARS_TRANSITION);
        msg.fragments().ifPresent(fragments ->
            fragments.stream()
                .filter(fragment -> fragment.knots().contains(SUPPORTED_FRAGMENT_KNOT))
                .forEach(fragment -> fragment.content(startComment() +
                    new HandlebarsFragment(fragment).compileWith(handlebars)
                    + endComment()))
        );
        observer.onNext(msg);
        observer.onCompleted();
      } catch (Exception e) {
        observer.onError(e);
      }
    });
  }

  @Override
  protected boolean shouldProcess(Set<String> knots) {
    return knots.contains(SUPPORTED_FRAGMENT_KNOT);
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    LOGGER.error("Error happened during Template processing", error);
    ClientResponse errorResponse = new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);

    return new KnotContext()
        .setClientRequest(knotContext.clientRequest())
        .setClientResponse(errorResponse);
  }

  @Override
  protected HandlebarsKnotConfiguration initConfiguration(JsonObject config) {
    return new HandlebarsKnotConfiguration(config);
  }

  private String startComment() {
    return snippetComment(START_WEBSERVICE_CALL_DEBUG_MARKER);
  }

  private String endComment() {
    return snippetComment(END_WEBSERVICE_CALL_DEBUG_MARKER);
  }

  private String snippetComment(String commentTemplate) {
    String debugLine = StringUtils.EMPTY;
    if (configuration.templateDebug()) {
      debugLine = commentTemplate;
    }
    return debugLine;
  }

  private void initHandlebars() {
    handlebars = new Handlebars();
    DefaultHandlebarsHelpers.registerFor(handlebars);

    ServiceLoader.load(CustomHandlebarsHelper.class)
        .iterator().forEachRemaining(helper -> {
      handlebars.registerHelper(helper.getName(), helper);
      LOGGER.info("Registered custom Handlebars helper: {}", helper.getName());
    });
  }

}
