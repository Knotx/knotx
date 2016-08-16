/*
 * Knot.x - Reactive microservice assembler
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.cognifide.knotx.placeholder;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import io.vertx.core.http.HttpServerRequest;

@Component
public class RequestPlaceholder implements Placeholder {

    @Override
    public String getValue(final HttpServerRequest request, final String placeholder) {
        return Arrays.stream(Strategy.values())
                .filter(strategy -> StringUtils.startsWith(placeholder, strategy.prefix)).findFirst()
                .map(strategy -> strategy.getValue(request, placeholder)).orElse(null);
    }

    private enum Strategy {

        HEADER("header.") {
            @Override
            String getValue(HttpServerRequest request, String placeholder) {
                return request.getHeader(getName(placeholder));
            }
        },
        PARAM("param.") {
            @Override
            String getValue(HttpServerRequest request, String placeholder) {
                return request.getParam(getName(placeholder));
            }
        },
        ATTRIBUTE("attribute.") {
            @Override
            String getValue(HttpServerRequest request, String placeholder) {
                return request.getFormAttribute(getName(placeholder));
            }
        };

        final String prefix;

        Strategy(String prefix) {
            this.prefix = prefix;
        }

        String getName(String placeholder) {
            return StringUtils.substringAfter(placeholder, ".");
        }

        abstract String getValue(HttpServerRequest request, String placeholder);

    }

}
