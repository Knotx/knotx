/*
 * Knot.x - reactive microservice assembler
 *
 * Copyright (C) 2013 Cognifide Limited
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

class ServiceEndpointProvider implements ServiceEndpoint {

	private String path;

	private String domain;

	private Integer port;

	private ServiceEndpointProvider() {
		// hidden constructor
	}

	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public Integer getPort() {
		return port;
	}

	boolean support(String path) {
		return path.matches(this.path);
	}

	static ServiceEndpointProvider from(ServiceEndpointProviderMetadata metadata) {
		ServiceEndpointProvider provider = new ServiceEndpointProvider();
		provider.path = metadata.getPath();
		provider.domain = metadata.getDomain();
		provider.port = Integer.valueOf(metadata.getPort());
		return provider;
	}

	@SuppressWarnings("unused")
	@XmlRootElement(name = "service")
	static class ServiceEndpointProviderMetadata {

		@XmlElement(name = "path")
		private String path;

		@XmlElement(name = "domain")
		private String domain;

		@XmlElement(name = "port")
		private String port;

		String getPath() {
			return path;
		}

		String getDomain() {
			return domain;
		}

		String getPort() {
			return port;
		}
	}

}
