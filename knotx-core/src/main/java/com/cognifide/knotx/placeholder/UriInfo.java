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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

public class UriInfo {

    private static final Pattern URI_PATTERN = Pattern
            .compile("([^\\.\\?#]+)(\\.([^\\/\\?#]+)\\.)?(\\.?([^\\/\\?#]+))?([^\\?#]+)?((\\?|#).+)?");

    private static final String PATH = "path";

    private static final String PATHPART = "pathpart";

    private static final String SELECTORSTRING = "selectorstring";

    private static final String SELECTOR = "selector";

    private static final String EXTENSION = "extension";

    private static final String SUFFIX = "suffix";

    private Map<String, String> values = Maps.newHashMap();

    public UriInfo(String uri) {
        Matcher matcher = URI_PATTERN.matcher(uri);
        if (matcher.matches()) {
            final String path = matcher.group(1);
            putValue(PATH, path);
            putValues(PATHPART, path, "/", 0);

            final String selectorString = matcher.group(3);
            putValue(SELECTORSTRING, selectorString);
            putValues(SELECTOR, selectorString, ".", 0);

            putValue(EXTENSION, matcher.group(5));
            putValue(SUFFIX, matcher.group(6));
        }
    }

    private void putValues(String key, String valuesString, String separator, int skip) {
        if (valuesString != null) {
            final int index[] = { 0 };
            Arrays.stream(valuesString.split(separator))
                    .forEach(value -> putValue(String.format("%s[%d]", key, index[0]++), value));
        }
    }

    private void putValue(String key, String value) {
        if (value != null) {
            values.put(key, value);
        }
    }

    public String getValue(String placeholder) {
        return values.get(placeholder);
    }

}
