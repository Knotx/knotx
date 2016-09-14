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
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import io.vertx.core.json.JsonObject;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RepositoryCorrectConfigurationTest extends AbstractKnotxConfigurationTest {

  private String CORRECT_JSON = "repository-correct.json";

  private RepositoryConfiguration correctConfig;

  private RepositoryConfiguration.RepositoryMetadata expectedLocalRepositoryMetadata;

  private RepositoryConfiguration.RepositoryMetadata expectedRemoteRepositoryMetadata;


  @Before
  public void setUp() throws Exception {
    correctConfig = new RepositoryConfiguration(readConfig(CORRECT_JSON));

    expectedLocalRepositoryMetadata = setupLocalRepositoryMetadata();
    expectedRemoteRepositoryMetadata = setupRemoteRepositoryMetadata();
  }

  @Test
  public void testConfigBeanInitializedProperly() {
    assertThat(correctConfig.getRepositories(), is(notNullValue()));
    assertThat(correctConfig.getRepositories().size(), is(2));
    assertThat(correctConfig.getRepositories(), CoreMatchers.hasItem(expectedLocalRepositoryMetadata));
    assertThat(correctConfig.getRepositories(), CoreMatchers.hasItem(expectedRemoteRepositoryMetadata));

  }

  @Test
  public void testValidation() {
    assertThat(correctConfig.getRepositories(), is(notNullValue()));
    assertThat(correctConfig.getRepositories().size(), is(2));
    assertThat(RepositoryType.LOCAL.validate(correctConfig.getRepositories().get(0)), is(true));
    assertThat(RepositoryType.LOCAL.validate(correctConfig.getRepositories().get(1)), is(true));
  }

  private RepositoryConfiguration.RepositoryMetadata setupLocalRepositoryMetadata() {
    RepositoryConfiguration.RepositoryMetadata expectedLocalRepositoryMetadata = new RepositoryConfiguration.RepositoryMetadata();
    expectedLocalRepositoryMetadata.setPath("/content/local/.*");
    expectedLocalRepositoryMetadata.setType(RepositoryType.LOCAL);
    expectedLocalRepositoryMetadata.setCatalogue(StringUtils.EMPTY);
    expectedLocalRepositoryMetadata.setClientOptions(new JsonObject());
    return expectedLocalRepositoryMetadata;
  }

  private RepositoryConfiguration.RepositoryMetadata setupRemoteRepositoryMetadata() {
    RepositoryConfiguration.RepositoryMetadata expectedLocalRepositoryMetadata = new RepositoryConfiguration.RepositoryMetadata();
    expectedLocalRepositoryMetadata.setPath("/content/.*");
    expectedLocalRepositoryMetadata.setType(RepositoryType.REMOTE);
    expectedLocalRepositoryMetadata.setDomain("localhost");
    expectedLocalRepositoryMetadata.setPort(8080);
    expectedLocalRepositoryMetadata.setClientOptions(new JsonObject("{\"tryUseCompression\" : true, \"keepAlive\": false}"));
    return expectedLocalRepositoryMetadata;
  }
}