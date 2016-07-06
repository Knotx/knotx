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
package com.cognifide.knotx.service;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "serviceConfiguration")
class ServiceConfigurationMetadata {

    @XmlElement(name = "services")
    private Services services;

    Services getServices() {
        return services;
    }

    @XmlRootElement(name = "services")
    static class Services {

        @XmlElements({@XmlElement(name = "service")})
        private List<ServiceEndpointProvider.ServiceEndpointProviderMetadata> services;

        List<ServiceEndpointProvider.ServiceEndpointProviderMetadata> getServices() {
            return services;
        }
    }

}
