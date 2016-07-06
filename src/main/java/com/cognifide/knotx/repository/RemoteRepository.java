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
package com.cognifide.knotx.repository;

import io.vertx.core.AsyncResultHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.net.URI;

class RemoteRepository implements Repository<String, URI> {

	private String path;

	private String serviceUrl;

	private RemoteRepository() {
		// hidden constructor
	}

	@Override
	public void get(URI uri, AsyncResultHandler<Template<String, URI>> handler) throws IOException {
		throw new UnsupportedOperationException("Not supported yet!");
	}

	@Override
	public boolean support(URI uri) {
		return Boolean.FALSE;
	}

	private static RemoteRepository of(String path, String serviceUrl) {
		RemoteRepository remoteRepository = new RemoteRepository();
		remoteRepository.path = path;
		remoteRepository.serviceUrl = serviceUrl;
		return remoteRepository;
	}

	@SuppressWarnings("unused")
	@XmlRootElement(name = "remote")
	static class RemoteRepositoryMetadata implements RepositoryMetadata {

		@XmlElement(name = "path")
		private String path;

		@XmlElement(name = "url")
		private String serviceUrl;

		String getPath() {
			return path;
		}

		String getServiceUrl() {
			return serviceUrl;
		}

		@Override
		public RepositoryType getRepositoryType() {
			return RepositoryType.REMOTE;
		}
	}

	static class RemoteRepositoryBuilder implements
			RepositoryBuilder<RemoteRepository.RemoteRepositoryMetadata> {

		private static final long serialVersionUID = 3007511016213749712L;

		@Override
		public Repository<String, URI> create(RemoteRepository.RemoteRepositoryMetadata metadata,
		                                      ResourceLoader resourceLoader, ApplicationContext applicationContext) {
			return RemoteRepository.of(metadata.getPath(), metadata.getServiceUrl());
		}
	}

}
