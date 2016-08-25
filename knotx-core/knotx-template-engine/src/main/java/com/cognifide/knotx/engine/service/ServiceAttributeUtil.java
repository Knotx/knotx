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
package com.cognifide.knotx.engine.service;


import com.cognifide.knotx.api.ServiceCallMethod;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ServiceAttributeUtil {

    private static final int NAMESPACE_GROUP_INDEX = 4;

    private static final int METHOD_TYPE_GROUP_INDEX = 2;

    private static final String ATTR_REGEX = "data-call-uri(-(post|get|all))?(-(\\w*))?";

    private static final Pattern ATTR_PATTERN = Pattern.compile(ATTR_REGEX);

    private ServiceAttributeUtil() {
        //Hidden constructors
    }

    public static String extractNamespace(String attributeName) {
        return extract(attributeName, NAMESPACE_GROUP_INDEX);
    }

    public static ServiceCallMethod extractMethodType(String attributeName) {
        String methodTypeName = extract(attributeName, METHOD_TYPE_GROUP_INDEX);
        if (StringUtils.isEmpty(methodTypeName)) {
            return ServiceCallMethod.ALL;
        }

        return ServiceCallMethod.valueOf(methodTypeName.toUpperCase());
    }

    private static String extract(String attributeName, int groupIndex) {
        Objects.requireNonNull(attributeName);

        Matcher matcher = ATTR_PATTERN.matcher(attributeName);
        if (matcher.matches()) {
            String namespace = matcher.group(groupIndex);
            return StringUtils.defaultString(namespace);
        } else {
            throw new RuntimeException(String.format("Attribute %s is invalid", attributeName));
        }

    }
}
