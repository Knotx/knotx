/*
 * Knot.x - Reactive microservice assembler - Action Knot Verticle
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
package com.cognifide.knotx.knot.action;

import com.google.common.base.MoreObjects;

import com.cognifide.knotx.http.StringToPatternFunction;
import com.cognifide.knotx.knot.api.KnotConfiguration;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

class ActionKnotConfiguration  extends KnotConfiguration {

  private final List<AdapterMetadata> adapterMetadataList;

  private final String formIdentifierName;

  public ActionKnotConfiguration(JsonObject config) {
    super(config);
    this.formIdentifierName = config.getString("formIdentifierName");
    this.adapterMetadataList = config.getJsonArray("adapters").stream()
        .map(item -> (JsonObject) item)
        .map(item -> {
          AdapterMetadata metadata = new AdapterMetadata();
          metadata.name = item.getString("name");
          metadata.address = item.getString("address");
          metadata.params = item.getJsonObject("params", new JsonObject()).getMap();
          metadata.allowedRequestHeaders = item.getJsonArray("allowed.request.headers", new JsonArray()).stream()
              .map(object -> (String) object)
              .map(new StringToPatternFunction())
              .collect(Collectors.toList());
          metadata.allowedResponseHeaders = item.getJsonArray("allowed.response.headers", new JsonArray()).stream()
              .map(object -> (String) object)
              .map(new StringToPatternFunction())
              .collect(Collectors.toList());
          return metadata;
        }).collect(Collectors.toList());
  }

  public List<AdapterMetadata> adapterMetadatas() {
    return adapterMetadataList;
  }

  public String formIdentifierName() {
    return formIdentifierName;
  }

  static class AdapterMetadata {

    private String name;

    private String address;

    private Map<String, Object> params;

    private List<Pattern> allowedRequestHeaders;

    private List<Pattern> allowedResponseHeaders;

    public String getName() {
      return name;
    }

    public String getAddress() {
      return address;
    }


    public Map<String, Object> getParams() {
      return params;
    }

    public List<Pattern> getAllowedRequestHeaders() {
      return allowedRequestHeaders;
    }


    public List<Pattern> getAllowedResponseHeaders() {
      return allowedResponseHeaders;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj != null && obj instanceof AdapterMetadata) {
        final AdapterMetadata other = (AdapterMetadata) obj;
        return new EqualsBuilder()
            .append(name, other.getName())
            .append(address, other.getAddress())
            .append(params, other.getParams())
            .append(allowedRequestHeaders, other.getAllowedRequestHeaders())
            .append(allowedResponseHeaders, other.getAllowedResponseHeaders())
            .isEquals();
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
          .append(allowedRequestHeaders)
          .append(allowedResponseHeaders)
          .toHashCode();
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("name", name)
          .add("address", address)
          .add("params", params)
          .add("allowedRequestHeaders", allowedRequestHeaders)
          .add("allowedResponseHeaders", allowedResponseHeaders)
          .toString();
    }
  }
}
