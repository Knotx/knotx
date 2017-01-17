/*
 * Knot.x - Reactive microservice assembler - Launcher
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
package com.cognifide.knotx.launcher;

import com.google.common.collect.Maps;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class SystemPropsConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(SystemPropsConfiguration.class);

  private Map<String, Value> envConfig = Maps.newHashMap();

  public SystemPropsConfiguration(String identifier) {
    envConfig = System.getProperties()
        .entrySet()
        .stream()
        .filter(entry -> onlyPropertyForIdentifier(entry, identifier))
        .collect(Collectors.toMap(
            entry -> StringUtils.substringAfter(((String) entry.getKey()), identifier + "."),
            entry -> new Value(((String) entry.getValue()))
        ));
  }

  private boolean onlyPropertyForIdentifier(Entry<Object, Object> entry, String identifier) {
    boolean result = false;

    String key = ((String) entry.getKey());
    if (key.startsWith(identifier)) {
      if (StringUtils.isBlank(StringUtils.substringAfter(key, identifier + "."))) {
        LOGGER
            .warn("-D{}={} does not have specified key after Service name", key, entry.getValue());
      } else {
        result = true;
      }
    }

    return result;
  }

  /**
   * Update given JsonObject with the data provided in system property during Knotx start.<br/>
   * In order to provide such overrides you can use two approches:
   * <ul>
   * <li>-Dio.knotx.KnotxServer.httpPort=9999,
   * -Dio.knotx.KnotxServer.splitter.address=other-address - this will override one property
   * with the value given after '=' </li>
   * <li>-Dio.knotx.KnotxServer.splitter=file:/aaa/bb/cc.json - this will merge the given cc.json
   * file from the field specified</li>
   * </ul>
   */
  public JsonObject updateJsonObject(JsonObject descriptor) {
    final JsonObject object = descriptor.copy();
    envConfig.entrySet().forEach(
        entry -> {
          String[] path = StringUtils.split(entry.getKey(), ".");
          JsonObject element = object;
          for (int idx = 0; idx < path.length; idx++) {
            if (idx < path.length - 1) {
              element = element.getJsonObject(path[idx]);
            } else { //last
              if (entry.getValue().getObject() instanceof JsonObject) {
                element.getJsonObject(path[idx]).mergeIn((JsonObject) entry.getValue().getObject());
              } else {
                element.put(path[idx], entry.getValue().getObject());
              }
            }
          }
        }
    );
    return object;
  }


  public Map<String, Value> envConfig() {
    return envConfig;
  }

  protected class Value {

    private static final String FILE_PREFIX = "file:";
    private Object var;

    public Value(String value) {
      if ("true".equals(value) || "false".equals(value)) {
        var = Boolean.valueOf(value);
      } else if (StringUtils.isNumeric(value)) {
        var = Integer.parseInt(value);
      } else if (value.startsWith(FILE_PREFIX)) {
        String objectPath = StringUtils.substringAfter(value, FILE_PREFIX);
        try {
          var = new JsonObject(FileUtils.readFileToString(new File(objectPath), "UTF-8"));
        } catch (Exception e) {
          LOGGER.error("Unable to read/parse Json Object from the given path: {}", objectPath);
          throw new IllegalArgumentException(e);
        }
      } else {
        var = value;
      }
    }

    public Object getObject() {
      return var;
    }
  }
}
