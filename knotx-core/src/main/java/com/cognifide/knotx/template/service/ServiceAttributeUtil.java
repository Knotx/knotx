/*
 * Knot.x - Reactive microservice assembler
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
package com.cognifide.knotx.template.service;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ServiceAttributeUtil {

    private static final int NAMESPACE_GROUP_INDEX = 4;

    private static final String ATTR_PATTERN = "data-call-uri(-(post|get|all))?(-(\\w*))?";

    private ServiceAttributeUtil() {
        //Hidden constructors
    }

    public static String extractNamespace(String attributeName) {
        Objects.requireNonNull(attributeName);

        String namespace = StringUtils.EMPTY;

        Matcher matcher = Pattern.compile(ATTR_PATTERN).matcher(attributeName);
        if (matcher.matches()) {
            namespace = matcher.group(NAMESPACE_GROUP_INDEX);
        }
        return StringUtils.defaultString(namespace);
    }


}
