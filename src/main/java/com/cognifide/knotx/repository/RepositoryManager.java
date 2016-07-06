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

import com.cognifide.knotx.manager.AbstractResourceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
class RepositoryManager extends AbstractResourceManager<Repository<String, URI>, ConfigurationMetadata> {

	@Value("${repository.configuration}")
	private String configurationPath;

	@Override
	protected List<Repository<String, URI>> extract(ConfigurationMetadata configurationMetadata) {
		return configurationMetadata.getRepositories().getRepositories().stream()
				.map(this::getRepositoryByMetadata)
				.collect(Collectors.toList());
	}

	@Override
	protected String getConfigurationPath() {
		return configurationPath;
	}

	@SuppressWarnings("unchecked")
	private Repository<String, URI> getRepositoryByMetadata(RepositoryMetadata metadata) {
		return metadata.getRepositoryType().getRepositoryBuilder().create(metadata, resourceLoader, applicationContext);
	}

}
