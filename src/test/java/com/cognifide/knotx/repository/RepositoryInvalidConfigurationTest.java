/*
 * Knot.x - reactive microservice assembler
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.cognifide.knotx.repository;

import com.cognifide.knotx.TestApplication;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestApplication.class)
@TestPropertySource(properties = {"repository.configuration=classpath:repository-invalid.yml"})
public class RepositoryInvalidConfigurationTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Autowired
    private RepositoryConfiguration configuration;

    private RepositoryConfiguration.RepositoryMetadata expectedLocalRepositoryMetadata;

    @Test
    public void testConfiguration() {
        assertThat(configuration.getRepositories(), is(notNullValue()));
        assertThat(configuration.getRepositories().size(), is(1));
        assertThat(RepositoryType.LOCAL.validate(configuration.getRepositories().get(0)), is(false));
    }


}