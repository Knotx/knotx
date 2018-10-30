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
package io.knotx.assembler;

import com.google.common.collect.Maps;
import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.fallback.DefaultFallbackStrategy;
import io.knotx.fallback.FallbackStrategy;
import io.knotx.fragments.FragmentConstants;
import io.knotx.options.FallbackMetadata;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FragmentAssemblerFallbackHandler {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(FragmentAssemblerFallbackHandler.class);

  private final Map<String, FallbackStrategy> fallbackStrategies = Maps.newHashMap();

  private final FragmentAssemblerOptions options;

  public FragmentAssemblerFallbackHandler(FragmentAssemblerOptions options) {
    this.options = options;
    ServiceLoader<FallbackStrategy> fallbackStrategyServiceLoader = ServiceLoader.load(FallbackStrategy.class);
    for (FallbackStrategy strategy : fallbackStrategyServiceLoader) {
      fallbackStrategies.put(strategy.getId(), strategy);
    }
  }

  public String applyFallback(Fragment failed, KnotContext knotContext) {
    Fragment fallback = getFallback(failed, knotContext);
    FallbackStrategy strategy = getFallbackStrategy(fallback);
    return strategy.applyFallback(failed, fallback, knotContext);
  }

  private FallbackStrategy getFallbackStrategy(Fragment fallbackFragment) {
    String strategyId = Optional.ofNullable(getFallbackStrategyId(fallbackFragment))
        .orElse(DefaultFallbackStrategy.ID);
    return Optional.ofNullable(fallbackStrategies.get(strategyId)).orElseThrow(() -> {
      LOGGER.error("Fragment {} specifies fallback strategy but no fallback strategy with given id was found", fallbackFragment);
      return new IllegalStateException(String.format("no strategy with id %s found", strategyId));
    });
  }

  private String getFallbackStrategyId(Fragment fallbackFragment) {
    return getAttribute(fallbackFragment, this.options.getSnippetOptions().getParamsPrefix() + FragmentConstants.FALLBACK_STRATEGY);
  }

  private Fragment getFallback(Fragment failed, KnotContext knotContext) {
    Fragment result = knotContext.getFragments().stream()
        .filter(f -> f.isFallback())
        .filter(f -> StringUtils.equals(failed.fallback().get(), getFallbackId(f)))
        .findFirst()
        .orElse(null);

    if (result == null) {
      result = getGlobalFallback(failed)
          .orElseThrow(() -> {
            LOGGER.error("Fragment {} specifies fallback but no fallback snippet with id '{}' was found", failed, failed.fallback().orElse(null));
            return new IllegalStateException(String.format("No fallback snippet with id '%s' was found", failed.fallback().orElse(null)));
          });
    }
    return result;
  }

  private Optional<Fragment> getGlobalFallback(Fragment failed) {
    return this.options.getSnippetOptions().getFallbacks().stream()
        .filter(f -> StringUtils.equals(failed.fallback().get(), f.getId()))
        .findFirst()
        .map(FallbackMetadata::getMarkup)
        .map(Fragment::fallback);
  }

  private String getFallbackId(Fragment fragment) {
    return getAttribute(fragment, this.options.getSnippetOptions().getParamsPrefix() + FragmentConstants.FALLBACK_ID);
  }

  private String getAttribute(Fragment fragment, String attributeId) {
    Document document = Jsoup.parseBodyFragment(fragment.content());
    Element scriptTag = document.body().child(0);
    List<Attribute> attributes = scriptTag.attributes().asList();
    return attributes.stream()
        .filter(a -> StringUtils.equals(attributeId, a.getKey()))
        .findFirst()
        .map(Attribute::getValue)
        .orElse(null);
  }
}
