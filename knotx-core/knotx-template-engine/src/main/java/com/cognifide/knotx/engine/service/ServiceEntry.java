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


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jsoup.nodes.Attribute;

import java.util.Objects;

import io.vertx.core.json.JsonObject;

public class ServiceEntry {

  private String namespace;
  private String name;
  private String address;
  private String cacheKey;
  private JsonObject payload;

  private ServiceEntry() {
    //Hidden constructors
  }

  public static ServiceEntry of(Attribute serviceAttribute, Attribute paramsAttribute) {
    ServiceEntry entry = new ServiceEntry();

    entry.namespace = ServiceAttributeUtil.extractNamespace(serviceAttribute.getKey());
    entry.name = serviceAttribute.getValue();
    entry.payload = new JsonObject(paramsAttribute.getValue());
    entry.cacheKey = serviceAttribute.getValue() + paramsAttribute.getValue();

    return entry;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public JsonObject getPayload() {
    return payload;
  }

  public String getCacheKey() {
    return cacheKey;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setCacheKey(String cacheKey) {
    this.cacheKey = cacheKey;
  }

  public ServiceEntry mergePayload(JsonObject config) {
    JsonObject merged = config.mergeIn(this.payload);
    this.payload = merged;
    return this;
  }

  public JsonObject getResultWithNamespaceAsKey(JsonObject result) {
    if (StringUtils.isNotEmpty(namespace)) {
      return new JsonObject().put(namespace, result);
    } else {
      return result;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ServiceEntry) {
      final ServiceEntry other = (ServiceEntry) o;
      return new EqualsBuilder()
          .append(namespace, other.getNamespace())
          .append(name, other.getName())
          .append(cacheKey, other.getCacheKey())
          .append(payload, other.getPayload())
          .isEquals();
    } else {
      return false;
    }

  }

  @Override
  public int hashCode() {
    return Objects.hash(namespace, name, cacheKey, payload);
  }

}
