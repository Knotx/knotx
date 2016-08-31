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
package com.cognifide.knotx.engine.impl;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public enum HandlebarsHelpers implements Helper<Object> {

    /**
     * Checks if current value and given parameter are equal as Strings.<br/>
     * Usage:
     * <p>
     * <pre>
     *     {{#string_equals value "parameter"}}
     *         equal
     *     {{else}}
     *         not equal
     *     {{/string_equals}}
     * </pre>
     * <p>
     * If value is "parameter", the output will be "equal".
     */
    STRING_EQUALS("string_equals") {
        @Override
        public CharSequence apply(Object value, Options options) throws IOException {
            Options.Buffer buffer = options.buffer();
            if (StringUtils.equals(String.valueOf(value), options.param(0, StringUtils.EMPTY))) {
                buffer.append(options.fn());
            } else {
                buffer.append(options.inverse());
            }
            return buffer;
        }
    },

    /**
     * Encodes given value.<br/>
     * Usage:
     * <p>
     * <pre>
     *     {{encode_uri value}}
     * </pre>
     * <p>
     * If value is "ex@mple string", the output will be "ex%40mple+string"
     */
    ENCODE_URI("encode_uri") {
        @Override
        public CharSequence apply(Object value, Options options) throws IOException {
            Options.Buffer buffer = options.buffer();
            buffer.append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8.name()));
            return buffer;
        }
    };

    private final String name;

    HandlebarsHelpers(String name) {
        this.name = name;
    }

    public static void register(Handlebars handlebars) {
        Stream.of(values()).forEach(helper -> handlebars.registerHelper(helper.name, helper));
    }

    @Override
    public abstract CharSequence apply(Object context, Options options) throws IOException;
}
