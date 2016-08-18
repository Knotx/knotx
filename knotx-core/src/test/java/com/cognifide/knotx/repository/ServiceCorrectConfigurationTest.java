/*
 * Knot.x - Reactive microservice assembler
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

import com.cognifide.knotx.TestApplication;
import com.cognifide.knotx.template.ServiceConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author pawel.koper on 27.07.16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestApplication.class)
@TestPropertySource(properties = {"repository.configuration=classpath:repository-correct.yml", "service.configuration=classpath:service-correct.yml"})
public class ServiceCorrectConfigurationTest {

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    private ServiceConfiguration.ServiceMetadata expectedServiceOne;
    private ServiceConfiguration.ServiceMetadata expectedServiceTwo;


    @Before
    public void setUp() throws Exception {
        expectedServiceOne = createMockedService("/service/mock/.*", "localhost", 3000);
        expectedServiceTwo = createMockedService("/service/.*", "localhost", 8080);
    }

    @Test
    public void testConfigBeanInitializedProperly() {
        assertThat(serviceConfiguration.getServices(), is(notNullValue()));
        assertThat(serviceConfiguration.getServices().size(), is(2));
        assertThat(serviceConfiguration.getServices(), hasItem(expectedServiceOne));
        assertThat(serviceConfiguration.getServices(), hasItem(expectedServiceTwo));

    }

    private ServiceConfiguration.ServiceMetadata createMockedService(String path, String domain, Integer port) {
        ServiceConfiguration.ServiceMetadata newService = new ServiceConfiguration.ServiceMetadata();
        newService.setPath(path);
        newService.setDomain(domain);
        newService.setPort(port);
        return newService;
    }


}
