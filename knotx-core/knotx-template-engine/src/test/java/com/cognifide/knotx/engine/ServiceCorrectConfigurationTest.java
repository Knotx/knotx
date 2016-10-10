/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
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
package com.cognifide.knotx.engine;

import com.cognifide.knotx.engine.TemplateEngineConfiguration.ServiceMetadata;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Collectors;

import io.vertx.core.json.JsonArray;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

public class ServiceCorrectConfigurationTest extends AbstractKnotxConfigurationTest {

  private static final String CORRECT_JSON = "service-correct.json";

  private TemplateEngineConfiguration correctConfig;

  private ServiceMetadata expectedServiceOne;
  private ServiceMetadata expectedServiceTwo;

  @Before
  public void setUp() throws Exception {
    correctConfig = new TemplateEngineConfiguration(readJson(CORRECT_JSON));
    expectedServiceOne = createMockedService("/service/mock/.*", "localhost", 3000, new JsonArray().add("Accept-*").add("Location"));
    expectedServiceTwo = createMockedService("/service/.*", "localhost", 8080, new JsonArray());
  }

  @Test
  public void whenCorrectConfigIsProvided_expectConfigIsProperlyParsed() {
    assertThat(correctConfig.getServices(), is(notNullValue()));
    assertThat(correctConfig.getServices().size(), is(2));
    assertThat(correctConfig.getServices(), CoreMatchers.hasItem(expectedServiceOne));
    assertThat(correctConfig.getServices(), CoreMatchers.hasItem(expectedServiceTwo));
    assertEquals(correctConfig.getServices().get(0).getAllowedRequestHeaderPatterns().toString(), expectedServiceOne.getAllowedRequestHeaderPatterns().toString());
    assertEquals(correctConfig.getServices().get(1).getAllowedRequestHeaderPatterns().toString(), expectedServiceTwo.getAllowedRequestHeaderPatterns().toString());
  }

  @Test
  public void whenAllowedHeaderSyntaxIsIncorrect_expectException() {
    exception.expect(RuntimeException.class);
    createMockedService("/service/.*", "localhost", 8080, new JsonArray().add("Accept("));
  }

  private ServiceMetadata createMockedService(String path, String domain, Integer port, JsonArray allowedHeaders) {
    ServiceMetadata newService = new ServiceMetadata();
    newService.setPath(path);
    newService.setDomain(domain);
    newService.setPort(port);
    newService.setAllowedRequestHeaderPatterns(allowedHeaders.stream()
      .map(o -> (String) o)
      .map(new StringToPatternMap())
      .collect(Collectors.toList()));
    return newService;
  }

}
