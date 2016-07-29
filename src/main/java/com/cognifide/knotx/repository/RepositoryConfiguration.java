/*
 * Knot.x - reactive microservice assembler
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

import com.cognifide.knotx.Server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(locations = {"${repository.configuration}"})
public class RepositoryConfiguration {

    @Autowired
    private Server server;

    private List<RepositoryMetadata> repositories;

    public Server getServer() {
        return server;
    }

    public List<RepositoryMetadata> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<RepositoryMetadata> repositories) {
        this.repositories = repositories;
    }

    public static class RepositoryMetadata {

        private String path;

        private String serviceUrl;

        private String catalogue;

        private RepositoryType type;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RepositoryMetadata that = (RepositoryMetadata) o;

            if (path != null ? !path.equals(that.path) : that.path != null) return false;
            if (serviceUrl != null ? !serviceUrl.equals(that.serviceUrl) : that.serviceUrl != null)
                return false;
            if (catalogue != null ? !catalogue.equals(that.catalogue) : that.catalogue != null)
                return false;
            return type == that.type;

        }

        @Override
        public int hashCode() {
            int result = path != null ? path.hashCode() : 0;
            result = 31 * result + (serviceUrl != null ? serviceUrl.hashCode() : 0);
            result = 31 * result + (catalogue != null ? catalogue.hashCode() : 0);
            result = 31 * result + (type != null ? type.hashCode() : 0);
            return result;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getServiceUrl() {
            return serviceUrl;
        }

        public void setServiceUrl(String serviceUrl) {
            this.serviceUrl = serviceUrl;
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

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
