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
package io.knotx.example.blockingadapter;

import io.vertx.core.json.JsonObject;

class ExampleBlockingAdapterConfiguration {

  private String address;

  private String protocol;

  private String domain;

  private Integer port;

  ExampleBlockingAdapterConfiguration(JsonObject config) {
    address = config.getString("address");
    protocol = config.getString("protocol");
    domain = config.getString("domain");
    port = config.getInteger("port");
  }

  String getAddress() {
    return address;
  }

  String getProtocol() {
    return protocol;
  }

  String getDomain() {
    return domain;
  }

  Integer getPort() {
   return port;
  }
}
