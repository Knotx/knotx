/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import io.knotx.junit.util.FileReader;
import io.knotx.server.configuration.KnotxFlowSettings;
import io.knotx.server.configuration.KnotxServerOptions;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;

public class KnotxServerOptionsRepositoriesTest {

  private KnotxFlowSettings flowConfiguration;

  @Before
  public void before() throws Exception {
    JsonObject config = new JsonObject(FileReader.readText(
        "io/knotx/server/test-config-server.json"));
    flowConfiguration = new KnotxServerOptions(config).getDefaultFlow();
  }

  @Test
  public void whenConfigWithRepositoryMappings_expectRepositoryAddressOnMatchingPaths()
      throws Exception {
    assertThat(flowConfiguration.repositoryForPath("/content/local/simple.html").get().getAddress(),
        equalTo("knotx.repository.filesystem"));
    assertThat(flowConfiguration.repositoryForPath("/content/simple.html").get().getAddress(),
        equalTo("knotx.repository.http"));
  }

  @Test
  public void whenConfigWithRepositoryMappings_expectNoRepositoryAddressOnNotMatchingPaths()
      throws Exception {
    assertThat(flowConfiguration.repositoryForPath("/content2/local/simple.html").isPresent(),
        equalTo(false));
    assertThat(flowConfiguration.repositoryForPath("/service/simple.html").isPresent(), equalTo(false));
  }


}
