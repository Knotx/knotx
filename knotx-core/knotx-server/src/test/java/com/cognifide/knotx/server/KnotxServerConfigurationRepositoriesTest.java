/*
 * Knot.x - Reactive microservice assembler - HTTP Server
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
package com.cognifide.knotx.server;

import com.cognifide.knotx.junit.FileReader;

import org.junit.Before;
import org.junit.Test;

import java.util.EnumMap;
import java.util.List;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class KnotxServerConfigurationRepositoriesTest {

  private JsonObject config;

  @Before
  public void before() throws Exception {
    config = new JsonObject(FileReader.readText("test-config-server.json"));
  }

  @Test
  public void whenConfigWithRepositoryMappings_expectRepositoryAddressOnMatchingPaths() throws Exception {
    KnotxServerConfiguration serverConfig = new KnotxServerConfiguration(config);

    assertThat(serverConfig.repositoryForPath("/content/local/simple.html").get(), equalTo("knotx.repository.filesystem"));
    assertThat(serverConfig.repositoryForPath("/content/simple.html").get(), equalTo("knotx.repository.http"));
  }

  @Test
  public void whenConfigWithRepositoryMappings_expectNoRepositoryAddressOnNotMatchingPaths() throws Exception {
    KnotxServerConfiguration serverConfig = new KnotxServerConfiguration(config);

    assertThat(serverConfig.repositoryForPath("/content2/local/simple.html").isPresent(), equalTo(false));
    assertThat(serverConfig.repositoryForPath("/service/simple.html").isPresent(), equalTo(false));
  }


}
