/*
 * Knot.x - Reactive microservice assembler - Repository Verticle
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
package com.cognifide.knotx.repository;

import com.cognifide.knotx.repository.impl.RepositoryType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonObject;

public class RepositoryConfiguration {

  private List<RepositoryMetadata> repositories;

  public RepositoryConfiguration(JsonObject config) {
    repositories = config.getJsonArray("repositories").stream()
        .map(item -> (JsonObject) item)
        .map(item -> {
          RepositoryMetadata repositoryMetadata = new RepositoryMetadata();
          repositoryMetadata.type = RepositoryType.valueOf(item.getString("type").toUpperCase());
          repositoryMetadata.path = item.getString("path");
          repositoryMetadata.clientOptions = item.getJsonObject("client.options", new JsonObject());

          if (repositoryMetadata.type == RepositoryType.LOCAL) {
            repositoryMetadata.catalogue = item.getString("catalogue");
          } else {
            repositoryMetadata.domain = item.getString("domain");
            repositoryMetadata.port = item.getInteger("port");
          }

          return repositoryMetadata;
        }).collect(Collectors.toList());

    validate();
  }


  public List<RepositoryMetadata> getRepositories() {
    return repositories;
  }

  private void validate() {
    String invalidMetadata = repositories.stream()
        .filter(metadata -> !metadata.getType().validate(metadata))
        .map(RepositoryMetadata::toString)
        .collect(Collectors.joining(", "));
    if (StringUtils.isNotEmpty(invalidMetadata)) {
      throw new RuntimeException("Invalid repositories configuration " + invalidMetadata);
    }
  }

  public static class RepositoryMetadata {

    private String path;

    private String domain;

    private Integer port;

    private String catalogue;

    private RepositoryType type;

    private JsonObject clientOptions;

    @Override
    public boolean equals(Object obj) {
      if (obj != null && obj instanceof RepositoryMetadata) {
        final RepositoryMetadata other = (RepositoryMetadata) obj;
        return new EqualsBuilder()
            .append(path, other.getPath())
            .append(domain, other.getDomain())
            .append(port, other.getPort())
            .append(catalogue, other.getCatalogue())
            .append(type, other.getType())
            .append(clientOptions, other.getClientOptions()).isEquals();
      } else {
        return false;
      }

    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder()
          .append(path)
          .append(domain)
          .append(port)
          .append(catalogue)
          .append(type)
          .append(clientOptions)
          .toHashCode();
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public String getDomain() {
      return domain;
    }

    public void setDomain(String domain) {
      this.domain = domain;
    }

    public Integer getPort() {
      return port;
    }

    public void setPort(Integer port) {
      this.port = port;
    }

    public String getCatalogue() {
      return catalogue;
    }

    public void setCatalogue(String catalogue) {
      this.catalogue = catalogue;
    }

    public RepositoryType getType() {
      return type;
    }

    public void setType(RepositoryType type) {
      this.type = type;
    }

    public JsonObject getClientOptions() {
      return clientOptions;
    }

    public void setClientOptions(JsonObject clientOptions) {
      this.clientOptions = clientOptions;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
  }
}
