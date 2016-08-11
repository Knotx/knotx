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

import org.apache.commons.lang3.StringUtils;

import io.vertx.core.http.HttpServerRequest;

public final class RequestPlaceholderHelper {

    private final static String HEADER_PREFIX = "header.";

    private final static String PARAM_PREFIX = "param.";

    private final static String ATTRIBUTE_PREFIX = "attribute.";

    private RequestPlaceholderHelper() {
    }

    public static String getValue(HttpServerRequest request, String placeholder) {
        String value = null;
        final String name = StringUtils.substringAfter(placeholder, ".");
        if (StringUtils.startsWith(placeholder, HEADER_PREFIX)) {
            value = request.getHeader(name);
        } else if (StringUtils.startsWith(placeholder, PARAM_PREFIX)) {
            value = request.getParam(name);
        } else if (StringUtils.startsWith(placeholder, ATTRIBUTE_PREFIX)) {
            value = request.getFormAttribute(name);
        }
        return value;
    }

}
