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
package io.knotx.fallback;

import com.google.common.collect.Maps;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.SnippetFragmentsContext;
import io.knotx.fragment.Fragment;
import io.knotx.server.api.FragmentsContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import org.apache.commons.lang3.StringUtils;

public class FragmentFallbackHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(FragmentFallbackHandler.class);

  private final Map<String, FallbackStrategy> fallbackStrategies = Maps.newHashMap();

  private final FragmentFallbackHandlerOptions options;

  FragmentFallbackHandler(FragmentFallbackHandlerOptions options) {
    this.options = options;
    ServiceLoader<FallbackStrategy> fallbackStrategyServiceLoader = ServiceLoader
        .load(FallbackStrategy.class);
    for (FallbackStrategy strategy : fallbackStrategyServiceLoader) {
      fallbackStrategies.put(strategy.getId(), strategy);
    }
  }

  @Override
  public void handle(RoutingContext routingContext) {
    FragmentsContext fragmentsContext = routingContext.get(FragmentsContext.KEY);
    try {
      Map<String, Fragment> fallbackFragmentCache = Maps.newHashMap();
      fragmentsContext.getFragments().stream()
          .filter(Fragment::failed)
          .forEach(f -> f.setBody(applyFallback(f, fragmentsContext, fallbackFragmentCache)));
      routingContext.put(SnippetFragmentsContext.KEY, fragmentsContext);
    } catch (Exception ex) {
      LOGGER.error("Exception happened during SnippetFragment assembly.", ex);

      ClientResponse errorResponse = new ClientResponse()
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      routingContext.put(FragmentsContext.KEY, new FragmentsContext()
          .setClientRequest(fragmentsContext.getClientRequest())
          .setClientResponse(errorResponse));
    } finally {
      routingContext.next();
    }
  }

  private String applyFallback(Fragment failed, FragmentsContext knotContext,
      Map<String, Fragment> fallbackFragmentCache) {
    Fragment fallback = getFallback(failed.getConfiguration().getString("fallback-id"),
        knotContext, fallbackFragmentCache);
    return fallback.getBody();
  }

  private Fragment getFallback(String fallbackId, FragmentsContext fragmentsContext,
      Map<String, Fragment> fallbackFragmentCache) {
    Fragment result = fallbackFragmentCache.get(fallbackId);

    if (result == null) {
      result = fragmentsContext.getFragments().stream()
          .filter(fragment -> "fallback".equals(fragment.getType()))
          .filter(
              f -> StringUtils
                  .equals(fallbackId,
                      f.getConfiguration().getString(FragmentFallbackConstants.FALLBACK_ID)))
          .findFirst()
          .orElse(null);

      if (result == null) {
        result = getGlobalFallback(fallbackId)
            .orElseThrow(() -> {
//              LOGGER.error(
//                  "SnippetFragment {} specifies fallback but no fallback snippet with id '{}' was found",
//                  failed, fallbackId);
              return new IllegalArgumentException(
                  String.format("No fallback snippet with id '%s' was found", fallbackId));
            });
      }
      fallbackFragmentCache.put(fallbackId, result);
    }
    return result;
  }

  private Optional<Fragment> getGlobalFallback(String fallbackId) {
    return this.options.getFallbacks().stream()
        .filter(f -> StringUtils.equals(fallbackId, f.getId()))
        .findFirst()
        .map(metadata -> new Fragment("fallback",
            new JsonObject().put("fallback-id", metadata.getId()), metadata.getMarkup()));
  }
}
