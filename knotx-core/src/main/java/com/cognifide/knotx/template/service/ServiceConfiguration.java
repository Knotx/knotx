/*
 * Knot.x - Reactive microservice assembler
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
package com.cognifide.knotx.template.service;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(locations = {"${service.configuration}"})
public class ServiceConfiguration {

    private List<ServiceMetadata> services;

    public List<ServiceMetadata> getServices() {
        return services;
    }

    public void setServices(List<ServiceMetadata> services) {
        this.services = services;
    }

    public static class ServiceMetadata {

        private String path;

        private String domain;

        private Integer port;

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof ServiceMetadata) {
                final ServiceMetadata other = (ServiceMetadata) obj;
                return new EqualsBuilder()
                        .append(path, other.getPath())
                        .append(domain, other.getDomain())
                        .append(port, other.getPort()).isEquals();
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
    }
}
