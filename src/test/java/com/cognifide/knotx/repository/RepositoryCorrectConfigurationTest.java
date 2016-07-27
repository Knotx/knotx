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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cognifide.knotx.TestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestApplication.class)
@TestPropertySource(properties = { "repository.configuration=classpath:repository-correct.yml" })
public class RepositoryCorrectConfigurationTest {

	@Autowired
	private RepositoryConfiguration configuration;

	private RepositoryConfiguration.RepositoryMetadata expectedLocalRepositoryMetadata;

	private RepositoryConfiguration.RepositoryMetadata expectedRemoteRepositoryMetadata;


	@Before
	public void setUp() throws Exception {
		expectedLocalRepositoryMetadata = setupLocalRepositoryMetadata();
		expectedRemoteRepositoryMetadata = setupRemoteRepositoryMetadata();
	}

	@Test
	public void testConfigBeanInitializedProperly() {
		assertThat(configuration.getRepositories(), is(notNullValue()));
		assertThat(configuration.getRepositories().size(), is(2));
		assertThat(configuration.getRepositories(), hasItem(expectedLocalRepositoryMetadata));
		assertThat(configuration.getRepositories(), hasItem(expectedRemoteRepositoryMetadata));

	}

	@Test
	public void testValidation() {
		assertThat(configuration.getRepositories(), is(notNullValue()));
		assertThat(configuration.getRepositories().size(), is(2));
		assertThat(RepositoryType.LOCAL.validate(configuration.getRepositories().get(0)), is(true));
		assertThat(RepositoryType.LOCAL.validate(configuration.getRepositories().get(1)), is(true));

	}


	private RepositoryConfiguration.RepositoryMetadata setupLocalRepositoryMetadata() {
		RepositoryConfiguration.RepositoryMetadata expectedLocalRepositoryMetadata = new RepositoryConfiguration.RepositoryMetadata();
		expectedLocalRepositoryMetadata.setPath("/content/local/.*");
		expectedLocalRepositoryMetadata.setType(RepositoryType.LOCAL);
		expectedLocalRepositoryMetadata.setCatalogue(StringUtils.EMPTY);
		return expectedLocalRepositoryMetadata;
	}

	private RepositoryConfiguration.RepositoryMetadata setupRemoteRepositoryMetadata() {
		RepositoryConfiguration.RepositoryMetadata expectedLocalRepositoryMetadata = new RepositoryConfiguration.RepositoryMetadata();
		expectedLocalRepositoryMetadata.setPath("/content/.*");
		expectedLocalRepositoryMetadata.setType(RepositoryType.REMOTE);
		expectedLocalRepositoryMetadata.setServiceUrl("localhost:8080");
		return expectedLocalRepositoryMetadata;
	}


}