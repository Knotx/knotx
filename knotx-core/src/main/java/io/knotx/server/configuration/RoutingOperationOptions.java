/*
 * Copyright (C) 2018 Cognifide Limited
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
package io.knotx.server.configuration;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

@DataObject(generateConverter = true, publicConverter = false)
public class RoutingOperationOptions {

  private String operationId;
  private List<RoutingHandlerOptions> handlers;

  public RoutingOperationOptions(JsonObject json) {
    RoutingOperationOptionsConverter.fromJson(json, this);
  }

  public RoutingOperationOptions(RoutingOperationOptions other) {
    this.operationId = other.getOperationId();
    this.handlers = new ArrayList<>(other.getHandlers());
  }

  public String getOperationId() {
    return operationId;
  }

  public RoutingOperationOptions setOperationId(String operationId) {
    this.operationId = operationId;
    return this;
  }

  public List<RoutingHandlerOptions> getHandlers() {
    return handlers;
  }

  public void setHandlers(List<RoutingHandlerOptions> handlers) {
    this.handlers = handlers;
  }

  @Override
  public String toString() {
    return "RoutingOperationOptions{" +
        "operationId='" + operationId + '\'' +
        ", handlers=" + handlers +
        '}';
  }
}
