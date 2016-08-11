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

import java.util.Map;

import org.springframework.util.PropertyPlaceholderHelper;

import com.google.common.collect.Maps;

import io.vertx.core.http.HttpServerRequest;

public class UriPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

    private final HttpServerRequest request;

    private final UriInfo uriInfo;

    private Map<String, String> placeholders = Maps.newHashMap();

    public UriPlaceholderResolver(HttpServerRequest request) {
        this.request = request;
        this.uriInfo = new UriInfo(request.uri());
    }

    @Override
    public String resolvePlaceholder(String placeholder) {
        if (!placeholders.containsKey(placeholder)) {
            String value = RequestPlaceholderHelper.getValue(request, placeholder);
            if (value == null) {
                value = uriInfo.getValue(placeholder);
            }
            placeholders.put(placeholder, value);
        }
        return placeholders.get(placeholder);
    }

}
