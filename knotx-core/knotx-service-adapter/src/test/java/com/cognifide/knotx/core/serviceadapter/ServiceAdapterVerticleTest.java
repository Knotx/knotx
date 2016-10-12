/*
 * Knot.x - Sample App with Mock service
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
package com.cognifide.knotx.core.serviceadapter;


import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

import com.cognifide.knotx.dataobjects.HttpRequestWrapper;
import com.cognifide.knotx.dataobjects.HttpResponseWrapper;
import com.cognifide.knotx.launcher.KnotxStarterVerticle;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStreamReader;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;

@RunWith(VertxUnitRunner.class)
public class ServiceAdapterVerticleTest {

  private Vertx vertx;

  @Before
  public void setUp(TestContext context) throws Exception {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    this.vertx = Vertx.vertx();

    DeploymentOptions deploymentOptions = new DeploymentOptions();
    deploymentOptions.fromJson(new JsonObject().put("config", readJson("knotx-service-adapter-test.json")));

    vertx.deployVerticle(KnotxStarterVerticle.class.getName(), deploymentOptions, context.asyncAssertSuccess());
  }

  @After
  public void tearDown() {
    vertx.close();
  }

  @Test
  public void whenRequestingMockFirstWithServiceAdapter_expectMockFirstResultInBody(TestContext context) {
    HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper().setMethod(HttpMethod.GET).setPath("/service/mock/first.json");
    this.vertx.eventBus().sendObservable("knotx.core.service-adapter", httpRequestWrapper.toJson()).map(result -> new HttpResponseWrapper((JsonObject) result.body())).subscribe(result -> {
      Assert.assertEquals(HttpResponseStatus.OK, result.statusCode());
      JsonObject jsonResponse = new JsonObject(result.body().toString());
      context.assertEquals("this is webservice no. 1", jsonResponse.getString("message"));
    }, error -> context.fail("Error from event bus."));
  }


  private String readText(String path) throws Exception {
    return CharStreams.toString(new InputStreamReader(Resources.getResource(path).openStream(), "UTF-8"));
  }

  private JsonObject readJson(String path) throws Exception {
    return new JsonObject(readText(path));
  }

}
