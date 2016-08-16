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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class UriInfo {

    /**
     * This regular expression is used for decomposite uri into following parts:
     * <p>
     * <ul>
     * <li>path</li>
     * <li>selector string</li>
     * <li>extension</li>
     * <li>suffix</li>
     * </ul>
     * <p>
     */
    private static final Pattern URI_PATTERN = Pattern
            .compile("([^\\.\\?#]+)(\\.([^\\/\\?#]+)\\.)?(\\.?([^\\/\\?#]+))?([^\\?#]+)?((\\?|#).+)?");

    private String path;

    private String[] pathparts;

    private String selectorString;

    private String[] selectors;

    private String extension;

    private String suffix;

    public UriInfo(String uri) {
        final Matcher matcher = URI_PATTERN.matcher(uri);
        if (matcher.matches()) {
            path = matcher.group(1);
            if (StringUtils.length(path) > 1) {
                pathparts = path.substring(1).split("/");
            }

            selectorString = matcher.group(3);
            if (selectorString != null) {
                selectors = selectorString.split("\\.");
            }

            extension = matcher.group(5);
            suffix = matcher.group(6);
        }
    }

    public String getPath() {
        return path;
    }

    public String getPathPart(int index) {
        return pathparts != null && 0 <= index && index < pathparts.length ? pathparts[index] : null;
    }

    public String getSelectorString() {
        return selectorString;
    }

    public String getSelector(int index) {
        return selectors != null && 0 <= index && index < selectors.length ? selectors[index] : null;
    }

    public String getExtension() {
        return extension;
    }

    public String getSuffix() {
        return suffix;
    }

}
