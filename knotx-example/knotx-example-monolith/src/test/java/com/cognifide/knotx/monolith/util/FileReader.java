/*
 * Knot.x - Sample App with Mock service
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
package com.cognifide.knotx.monolith.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReader {

    private static final Logger LOG = LoggerFactory.getLogger(FileReader.class);

    public static String readFile(String fileName) {
        String result = StringUtils.EMPTY;

        ClassLoader classLoader = FileReader.class.getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("Cannot red file {}", fileName);
        }

        return result;
    }
}
