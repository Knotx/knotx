/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
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
package com.cognifide.knotx.engine.placeholders;

import com.cognifide.knotx.dataobjects.RenderRequest;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class RequestPlaceholderSubstitutor implements PlaceholderSubstitutor {

  @Override
  public String getValue(final RenderRequest request, final String placeholder) {
    return Arrays.stream(Strategy.values())
        .filter(strategy -> StringUtils.startsWith(placeholder, strategy.prefix))
        .findFirst().map(strategy -> strategy.getValue(request, placeholder)).orElse(null);
  }

  private enum Strategy {

    HEADER("header.") {
      @Override
      String getValue(RenderRequest renderRequest, String placeholder) {
        return renderRequest.request().headers().get(getName(placeholder));
      }
    },
    PARAM("param.") {
      @Override
      String getValue(RenderRequest renderRequest, String placeholder) {
        return renderRequest.request().params().get(getName(placeholder));
      }
    };

    final String prefix;

    Strategy(String prefix) {
      this.prefix = prefix;
    }

    String getName(String placeholder) {
      return StringUtils.substringAfter(placeholder, ".");
    }

    abstract String getValue(RenderRequest request, String placeholder);
  }

}
