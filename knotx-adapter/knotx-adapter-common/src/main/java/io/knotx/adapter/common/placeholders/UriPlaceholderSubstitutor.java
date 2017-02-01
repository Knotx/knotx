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
package io.knotx.adapter.common.placeholders;

import io.knotx.dataobjects.ClientRequest;
import java.net.URI;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class UriPlaceholderSubstitutor implements PlaceholderSubstitutor {

  private static final String URI_PREFIX = "uri.";

  private static final String SLING_URI_PREFIX = "slingUri.";

  @Override
  public String getValue(ClientRequest request, String placeholder) {
    return Arrays.stream(Strategy.values())
        .filter(strategy -> StringUtils.startsWith(placeholder, strategy.prefix))
        .findFirst()
        .map(strategy -> strategy.getValue(request.getPath(), placeholder))
        .orElse(null);
  }

  private enum Strategy {
    PATHPART(URI_PREFIX + "pathpart[") {
      @Override
      String getValue(String uri, String placeholder) {
        final int index =
            NumberUtils.toInt(StringUtils.substringBetween(placeholder, "[", "]"), -1);
        String[] parts = URI.create(uri).getPath().split("/");
        return parts.length > index ? parts[index] : "";
      }
    },
    PATH(URI_PREFIX + "path") {
      @Override
      String getValue(String uri, String placeholder) {
        return URI.create(uri).getPath();
      }
    },
    EXTENSION(URI_PREFIX + "extension") {
      @Override
      String getValue(String uri, String placeholder) {
        return StringUtils.substringAfterLast(URI.create(uri).getPath(), ".");
      }
    },
    SLING_PATHPART(SLING_URI_PREFIX + "pathpart[") {
      @Override
      String getValue(String uri, String placeholder) {
        final int index =
            NumberUtils.toInt(StringUtils.substringBetween(placeholder, "[", "]"), -1);
        return SlingUriInfoHelper.getUriInfo(uri).getPathPart(index);
      }
    },
    SLING_PATH(SLING_URI_PREFIX + "path") {
      @Override
      String getValue(String uri, String placeholder) {
        return SlingUriInfoHelper.getUriInfo(uri).getPath();
      }
    },
    SLING_SELECTORSTRING(SLING_URI_PREFIX + "selectorstring") {
      @Override
      String getValue(String uri, String placeholder) {
        return SlingUriInfoHelper.getUriInfo(uri).getSelectorString();
      }
    },
    SLING_SELECTOR(SLING_URI_PREFIX + "selector[") {
      @Override
      String getValue(String uri, String placeholder) {
        final int index =
            NumberUtils.toInt(StringUtils.substringBetween(placeholder, "[", "]"), -1);
        return SlingUriInfoHelper.getUriInfo(uri).getSelector(index);
      }
    },
    SLING_EXTENSION(SLING_URI_PREFIX + "extension") {
      @Override
      String getValue(String uri, String placeholder) {
        return SlingUriInfoHelper.getUriInfo(uri).getExtension();
      }
    },
    SLING_SUFFIX(SLING_URI_PREFIX + "suffix") {
      @Override
      String getValue(String uri, String placeholder) {
        return SlingUriInfoHelper.getUriInfo(uri).getSuffix();
      }
    };

    final String prefix;

    Strategy(String prefix) {
      this.prefix = prefix;
    }

    abstract String getValue(String uri, String placeholder);

  }

}
