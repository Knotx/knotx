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
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.vertx.core.http.HttpServerRequest;

@Component
@Scope("prototype")
public class UriPlaceholder implements Placeholder {

    private UriInfo uriInfo;

    @Override
    public String getValue(HttpServerRequest request, String placeholder) {
        if (uriInfo == null) {
            uriInfo = new UriInfo(request.uri());
        }
        return Arrays.stream(Strategy.values())
                .filter(strategy -> StringUtils.startsWith(placeholder, strategy.prefix)).findFirst()
                .map(strategy -> strategy.getValue(uriInfo, placeholder)).orElse(null);
    }

    private enum Strategy {

        PATHPART("pathpart[") {
            @Override
            String getValue(UriInfo uriInfo, String placeholder) {
                final int index = NumberUtils.toInt(StringUtils.substringBetween(placeholder, "[", "]"), -1);
                return uriInfo.getPathPart(index);
            }
        },
        PATH("path") {
            @Override
            String getValue(UriInfo uriInfo, String placeholder) {
                return uriInfo.getPath();
            }
        },
        SELECTORSTRING("selectorstring") {
            @Override
            String getValue(UriInfo uriInfo, String placeholder) {
                return uriInfo.getSelectorString();
            }
        },
        SELECTOR("selector[") {
            @Override
            String getValue(UriInfo uriInfo, String placeholder) {
                final int index = NumberUtils.toInt(StringUtils.substringBetween(placeholder, "[", "]"), -1);
                return uriInfo.getSelector(index);
            }
        },
        EXTENSION("extension") {
            @Override
            String getValue(UriInfo uriInfo, String placeholder) {
                return uriInfo.getExtension();
            }
        },
        SUFFIX("suffix") {
            @Override
            String getValue(UriInfo uriInfo, String placeholder) {
                return uriInfo.getSuffix();
            }
        };

        final String prefix;

        Strategy(String prefix) {
            this.prefix = prefix;
        }

        abstract String getValue(UriInfo uriInfo, String placeholder);

    }

}
