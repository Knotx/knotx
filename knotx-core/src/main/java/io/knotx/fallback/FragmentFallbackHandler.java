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

import io.knotx.fragment.ClientResponse;
import io.knotx.fragment.Fragment;
import io.knotx.options.FallbackMetadata;
import io.knotx.server.api.FragmentsContext;
import io.knotx.snippet.SnippetFragmentsContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class FragmentFallbackHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(FragmentFallbackHandler.class);

  private final FragmentFallbackHandlerOptions options;

  FragmentFallbackHandler(FragmentFallbackHandlerOptions options) {
    this.options = options;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    FragmentsContext fragmentsContext = routingContext.get(FragmentsContext.KEY);
    try {
      List<Fragment> inputFragments = fragmentsContext.getFragments();
      inputFragments.stream()
          .filter(Fragment::failed)
          .forEach(f -> f.setBody(applyFallback(f, fragmentsContext)));

      fragmentsContext.setFragments(clearFallbackFragments(inputFragments));
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

  private List<Fragment> clearFallbackFragments(List<Fragment> fragments) {
    List<Fragment> result = new ArrayList<>(fragments.size());
    fragments.forEach(fragment -> {
      if (!FallbackConstants.FALLBACK_TYPE.equals(fragment.getType())) {
        result.add(fragment);
      }
    });
    return result;
  }

  private String applyFallback(Fragment failed, FragmentsContext knotContext) {
    String fallbackIdentifier = getFallbackId(failed);

    List<Fragment> fallbackFragments = knotContext.getFragments()
        .stream().filter(fragment -> FallbackConstants.FALLBACK_TYPE.equals(fragment.getType()))
        .collect(Collectors.toList());

    return getFallbackBody(fallbackIdentifier, fallbackFragments);
  }

  private String getFallbackId(Fragment failed) {
    String fallbackIdentifier = failed.getConfiguration()
        .getString(FallbackConstants.FALLBACK_IDENTIFIER);
    if (StringUtils.isBlank(fallbackIdentifier)) {
      fallbackIdentifier = options.getDefaultFallback();
    }
    return fallbackIdentifier;
  }

  private String getFallbackBody(String fallbackId, List<Fragment> fallbackFragments) {
    Optional<String> fallbackBody = fallbackFragments.stream()
        .filter(fragment -> fragment.getConfiguration()
            .getString(FallbackConstants.FALLBACK_IDENTIFIER)
            .equals(fallbackId))
        .findFirst().map(Fragment::getBody);
    if (!fallbackBody.isPresent()) {
      fallbackBody = options.getFallbacks().stream()
          .filter(metadata -> metadata.getId().equals(fallbackId)).findFirst()
          .map(FallbackMetadata::getMarkup);
    }
    return fallbackBody.orElse(FallbackConstants.EMPTY_FALLBACK_VALUE);
  }
}
