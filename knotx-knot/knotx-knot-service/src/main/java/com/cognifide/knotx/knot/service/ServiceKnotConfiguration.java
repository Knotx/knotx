/*
 * Knot.x - Reactive microservice assembler - Service Knot
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
package com.cognifide.knotx.knot.service;

import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ServiceKnotConfiguration {

  private final String address;
  private List<ServiceMetadata> services;

  public ServiceKnotConfiguration(JsonObject config) {
    address = config.getString("address");
    services = config.getJsonArray("services").stream()
        .map(item -> (JsonObject) item)
        .map(item -> {
          ServiceMetadata metadata = new ServiceMetadata();
          metadata.name = item.getString("name");
          metadata.address = item.getString("address");
          metadata.params = item.getJsonObject("params");
          metadata.cacheKey = item.getString("cacheKey");
          return metadata;
        }).collect(Collectors.toList());
  }

  public List<ServiceMetadata> getServices() {
    return services;
  }

  public String getAddress() {
    return address;
  }

  public static class ServiceMetadata {

    private String name;
    private String address;
    private JsonObject params;
    private String cacheKey;

    @Override
    public boolean equals(Object obj) {
      if (obj != null && obj instanceof ServiceMetadata) {
        final ServiceMetadata other = (ServiceMetadata) obj;
        return new EqualsBuilder()
            .append(name, other.getName())
            .append(address, other.getAddress())
            .append(params, other.getParams())
            .append(cacheKey, other.getCacheKey()).isEquals();
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder()
          .append(name)
          .append(address)
          .append(params)
          .append(cacheKey)
          .toHashCode();
    }

    public String getName() {
      return name;
    }

    void setName(String name) {
      this.name = name;
    }

    public String getAddress() {
      return address;
    }

    void setAddress(String address) {
      this.address = address;
    }

    public JsonObject getParams() {
      return params;
    }

    void setParams(JsonObject params) {
      this.params = params;
    }

    public String getCacheKey() {
      return cacheKey;
    }

    void setCacheKey(String cacheKey) {
      this.cacheKey = cacheKey;
    }
  }
}
