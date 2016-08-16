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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;

import com.google.common.collect.Maps;

import io.vertx.core.http.HttpServerRequest;

@Component
@Scope("prototype")
public class UriPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

    private final HttpServerRequest request;

    private final Map<String, String> cache = Maps.newHashMap();

    @Autowired
    private List<Placeholder> placeholders;

    public UriPlaceholderResolver(HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public String resolvePlaceholder(String placeholder) {
        if (!cache.containsKey(placeholder)) {
            final String value = placeholders.stream().map(bean -> bean.getValue(request, placeholder))
                    .filter(Objects::nonNull).findFirst().orElse(null);
            cache.put(placeholder, value);
        }
        return cache.get(placeholder);
    }

}
