/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.knot;

import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.proxy.KnotProxy;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import rx.Observable;

/**
 * Abstract class that should be root for all custom knots
 */
public abstract class AbstractKnotProxy implements KnotProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKnotProxy.class);

  protected abstract Observable<KnotContext> processRequest(KnotContext knotContext);

  @Override
  public void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result) {
    if (shouldProcess(knotContext)) {
      processRequest(knotContext)
          .subscribe(
              ctx -> result.handle(Future.succeededFuture(ctx)),
              error -> {
                LOGGER.error("Error happened during Knot Context processing", error);
                result.handle(Future.succeededFuture(processError(knotContext, error)));
              }
          );
    } else {
      result.handle(Future.succeededFuture(knotContext));
    }
  }

  protected abstract boolean shouldProcess(Set<String> knots);

  protected abstract KnotContext processError(KnotContext knotContext, Throwable error);

  protected boolean shouldProcess(KnotContext context) {
    Set<String> knots = Optional.ofNullable(context.getFragments())
        .map(this::getKnotSet)
        .orElse(Collections.emptySet());
    return shouldProcess(knots);
  }

  private Set<String> getKnotSet(List<Fragment> fragments) {
    return
        fragments.stream()
            .map(Fragment::knots)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
  }
}
