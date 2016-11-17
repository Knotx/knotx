/*
 * Knot.x - Reactive microservice assembler - View Knot
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
package com.cognifide.knotx.knot.service.service;


import com.cognifide.knotx.knot.service.ServiceKnotConfiguration;
import com.cognifide.knotx.junit.FileReader;

import org.jsoup.nodes.Attribute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vertx.core.json.JsonObject;

public class ServiceEntryTest {

  private ServiceKnotConfiguration config;

  @Before
  public void setUp() throws Exception {
    JsonObject configFile = new JsonObject(FileReader.readText("service-correct.json"));
    this.config = new ServiceKnotConfiguration(configFile);
  }


  @Test
  public void mergePayload_pathFromParamsAttribute() {
    ServiceEntry serviceEntry = new ServiceEntry(new Attribute("data-service-first", "first-service"),
        new Attribute("data-params-first", "{\"path\":\"first-service\"}"));
    serviceEntry.mergeParams(config.getServices().stream().findFirst().get().getParams());
    Assert.assertEquals("first-service", serviceEntry.getParams().getString("path"));
  }

  @Test
  public void mergePayload_pathFromConfigAttribute() {
    ServiceEntry serviceEntry = new ServiceEntry(new Attribute("data-service-first", "first-service"),
        new Attribute("data-params-first", "{}"));
    serviceEntry.mergeParams(config.getServices().stream().findFirst().get().getParams());
    Assert.assertEquals("/service/mock/first.json", serviceEntry.getParams().getString("path"));
  }

  @Test
  public void mergePayload_nameFromParamsAttribute() {
    ServiceEntry serviceEntry = new ServiceEntry(new Attribute("data-service-first", "first-service"),
        new Attribute("data-params-first", "{\"name\":\"first-service-name\"}"));
    serviceEntry.mergeParams(config.getServices().stream().findFirst().get().getParams());
    Assert.assertEquals("/service/mock/first.json", serviceEntry.getParams().getString("path"));
    Assert.assertEquals("first-service-name", serviceEntry.getParams().getString("name"));
  }

}
