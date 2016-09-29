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
package com.cognifide.knotx.engine;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;

public final class HeadersHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadersHelper.class);

    private static final String WILDCARD = "*";

    private HeadersHelper() {
        //to prevent initialization
    }

    public static List<Pattern> getPatternsFromHeadersConfig(JsonArray headersConfiguration) {
        List<String> allowedHeaders = headersConfiguration.getList();
        return allowedHeaders.stream().map(allowedHeader -> {
            String pattern = "^" + allowedHeader.replace(WILDCARD, "(.+)") + "$";
            Pattern compiledPattern;
            try {
                compiledPattern = Pattern.compile(pattern);
            } catch (PatternSyntaxException e) {
                LOGGER.error("Invalid allowed headers configuration syntax: " + allowedHeader, e);
                throw new RuntimeException("Application error");
            }
            return compiledPattern;
        }).collect(Collectors.toList());
    }


    public static MultiMap getFilteredHeaders(MultiMap headers, List<Pattern> allowedHeaders) {
        final MultiMap filteredHeaders = MultiMap.caseInsensitiveMultiMap();
        headers.names().stream()
            .filter(header -> allowedHeaders.stream().anyMatch(allowedHeader -> allowedHeader.matcher(header).matches()))
            .forEach(header -> filteredHeaders.add(header, headers.get(header)));
        return filteredHeaders;
    }

}
