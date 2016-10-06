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

import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.io.InputStreamReader;

import io.vertx.core.json.JsonObject;

public class AbstractKnotxConfigurationTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  protected static JsonObject readConfig(String path) throws Exception {
    return readJson(path);
  }

  public static String readText(String path) throws Exception {
    return CharStreams.toString(new InputStreamReader(Resources.getResource(path).openStream(), "UTF-8"));
  }

  protected static JsonObject readJson(String path) throws Exception {
    return new JsonObject(readText(path));
  }
}
