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

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.knot.action.ActionKnotConfiguration;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.Optional;
import rx.Observable;

public class GetActionHandler extends AbstractActionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetActionHandler.class);

  public GetActionHandler(ActionKnotConfiguration configuration) {
    super(configuration);
  }

  @Override
  public Observable<KnotContext> processContext(KnotContext knotContext) {
    LOGGER.trace("Pass-through {} request", knotContext.getClientRequest().getMethod());
    knotContext.setTransition(DEFAULT_TRANSITION);
    Optional.ofNullable(knotContext.getFragments()).ifPresent(this::processFragments);

    return Observable.just(knotContext);
  }
}
