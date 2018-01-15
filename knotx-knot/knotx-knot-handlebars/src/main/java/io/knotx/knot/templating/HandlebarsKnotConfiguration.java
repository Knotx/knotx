/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.knot.templating;

import io.vertx.core.json.JsonObject;

public class HandlebarsKnotConfiguration {

  private final String address;
  private final String cacheKeyAlgorithm;
  private final Long cacheSize;

  HandlebarsKnotConfiguration(JsonObject config) {
    this.address = config.getString("address");
    this.cacheKeyAlgorithm = config.getString("cacheKeyAlgorithm", "MD5");
    this.cacheSize = config.getLong("cacheSize", 1000L);
  }

  public String address() {
    return address;
  }

  public String getCacheKeyAlgorithm() {
    return cacheKeyAlgorithm;
  }

  public Long getCacheSize() {
    return cacheSize;
  }
}
