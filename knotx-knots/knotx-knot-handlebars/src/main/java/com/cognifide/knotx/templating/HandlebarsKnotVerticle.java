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
package com.cognifide.knotx.templating;

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.handlebars.CustomHandlebarsHelper;
import com.cognifide.knotx.knot.api.AbstractKnot;
import com.cognifide.knotx.templating.helpers.DefaultHandlebarsHelpers;
import com.github.jknack.handlebars.Handlebars;

import java.io.IOException;
import java.util.ServiceLoader;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

public class HandlebarsKnotVerticle extends AbstractKnot<HandlebarsKnotConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(HandlebarsKnotVerticle.class);

  private Handlebars handlebars;

  @Override
  public void start() throws Exception {
    super.start();
    initHandlebars();
  }

  @Override
  protected void process(Message<KnotContext> message, Handler<KnotContext> handler) {

  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    return null;
  }

  @Override
  protected HandlebarsKnotConfiguration initConfiguration(JsonObject config) {
    return null;
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

//  private Observable<HtmlFragment> compileHtmlFragment(Fragment fragment) {
//    if (!fragment.isRaw()) {
//      return Observable.create(subscriber -> {
//        try {
//          subscriber.onNext(new TemplateHtmlFragment(fragment).compileWith(handlebars));
//          subscriber.onCompleted();
//        } catch (IOException e) {
//          subscriber.onError(e);
//        }
//      });
//    } else {
//      return Observable.just(new RawHtmlFragment(fragment));
//    }
//  }

}
