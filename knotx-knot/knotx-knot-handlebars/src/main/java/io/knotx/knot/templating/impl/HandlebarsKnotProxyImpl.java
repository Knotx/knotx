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
package io.knotx.knot.templating.impl;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.fragments.FragmentContentExtractor;
import io.knotx.knot.AbstractKnotProxy;
import io.knotx.knot.templating.HandlebarsKnotConfiguration;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;
import io.knotx.knot.templating.handlebars.JsonObjectValueResolver;
import io.knotx.knot.templating.helpers.DefaultHandlebarsHelpers;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class HandlebarsKnotProxyImpl extends AbstractKnotProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(HandlebarsKnotProxyImpl.class);

  private static final String SUPPORTED_FRAGMENT_KNOT = "handlebars";

  private static final int DEBUG_MAX_FRAGMENT_CONTENT_LOG_LENGTH = 256;

  private Handlebars handlebars;

  private Cache<String, Template> cache;

  private MessageDigest digest;

  public HandlebarsKnotProxyImpl(HandlebarsKnotConfiguration configuration) {
    this.handlebars = createHandlebars();
    this.cache = CacheBuilder.newBuilder()
        .maximumSize(configuration.getCacheSize())
        .removalListener(listener -> LOGGER.warn(
            "Cache limit exceeded. If this information occurs frequently, check 'cacheSize' configuration option because your cache is too small"))
        .build();
    try {
      this.digest = MessageDigest.getInstance(configuration.getCacheKeyAlgorithm());
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error("Could not initialize fragment hashing algorithm!", e);
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  protected Single<KnotContext> processRequest(KnotContext knotContext) {
    return Single.create(observer -> {
      try {
        knotContext.setTransition(DEFAULT_TRANSITION);
        Optional.ofNullable(knotContext.getFragments()).ifPresent(fragments ->
            fragments.stream()
                .filter(fragment -> shouldProcess(Sets.newHashSet(fragment.knots())))
                .forEach(fragment -> fragment.content(evaluate(template(fragment), fragment.context())))
        );
        observer.onSuccess(knotContext);
      } catch (Exception e) {
        observer.onError(e);
      }
    });
  }

  private Template template(Fragment fragment) {
    try {
      byte[] cacheKeyBytes = digest.digest(fragment.content().getBytes(StandardCharsets.UTF_8));
      String cacheKey = new String(cacheKeyBytes);

      return cache.get(cacheKey, () -> {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Compiles Handlebars fragment [{}]",
              StringUtils
                  .abbreviate(fragment.content().replaceAll("[\n\r\t]", ""),
                      DEBUG_MAX_FRAGMENT_CONTENT_LOG_LENGTH));
        }
        return handlebars.compileInline(FragmentContentExtractor.unwrapContent(fragment));
      });
    } catch (ExecutionException e) {
      LOGGER.error("Could not compile fragment [{}]", fragment.content(), e);
      throw new IllegalStateException(e);
    }
  }

  private String evaluate(Template template, JsonObject context) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Applying context [{}] to template [{}]!", context, template.text());
    }
    try {
      return template.apply(
          Context.newBuilder(context)
              .push(JsonObjectValueResolver.INSTANCE)
              .build());
    } catch (IOException e) {
      LOGGER.error("Could not apply context [{}] to template [{}]", context, template.text());
      throw new IllegalStateException(e);
    }
  }

  @Override
  protected boolean shouldProcess(Set<String> knots) {
    return knots.contains(SUPPORTED_FRAGMENT_KNOT);
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    LOGGER.error("Error happened during Template processing", error);
    ClientResponse errorResponse = new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());

    return new KnotContext()
        .setClientRequest(knotContext.getClientRequest())
        .setClientResponse(errorResponse);
  }

  private Handlebars createHandlebars() {
    Handlebars newHandlebars = new Handlebars();
    DefaultHandlebarsHelpers.registerFor(newHandlebars);

    ServiceLoader.load(CustomHandlebarsHelper.class)
        .iterator().forEachRemaining(helper -> {
      newHandlebars.registerHelper(helper.getName(), helper);
      LOGGER.info("Registered custom Handlebars helper: {}", helper.getName());
    });

    return newHandlebars;
  }
}
