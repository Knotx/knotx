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

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ServiceCorrectConfigurationTest extends AbstractKnotxConfigurationTest {

    private String CORRECT_JSON = "service-correct.json";

    private TemplateEngineConfiguration correctConfig;

    private TemplateEngineConfiguration.ServiceMetadata expectedServiceOne;
    private TemplateEngineConfiguration.ServiceMetadata expectedServiceTwo;

    @Before
    public void setUp() throws Exception {
        correctConfig = new TemplateEngineConfiguration(readConfig(CORRECT_JSON));

        expectedServiceOne = createMockedService("/service/mock/.*", "localhost", 3000);
        expectedServiceTwo = createMockedService("/service/.*", "localhost", 8080);
    }

    @Test
    public void testConfigBeanInitializedProperly() {
        assertThat(correctConfig.getServices(), is(notNullValue()));
        assertThat(correctConfig.getServices().size(), is(2));
        assertThat(correctConfig.getServices(), CoreMatchers.hasItem(expectedServiceOne));
        assertThat(correctConfig.getServices(), CoreMatchers.hasItem(expectedServiceTwo));

    }

    private TemplateEngineConfiguration.ServiceMetadata createMockedService(String path, String domain, Integer port) {
        TemplateEngineConfiguration.ServiceMetadata newService = new TemplateEngineConfiguration.ServiceMetadata();
        newService.setPath(path);
        newService.setDomain(domain);
        newService.setPort(port);
        return newService;
    }


}
