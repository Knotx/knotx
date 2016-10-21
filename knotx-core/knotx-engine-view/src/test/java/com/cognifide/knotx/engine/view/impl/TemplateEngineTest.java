/*
 * Knot.x - Reactive microservice assembler - View Engine Verticle
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
package com.cognifide.knotx.engine.view.impl;

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.engine.view.ViewEngineConfiguration;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.junit.FileReader;
import com.cognifide.knotx.junit.KnotxConfiguration;
import com.cognifide.knotx.junit.Logback;
import com.cognifide.knotx.junit.TestVertxDeployer;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import rx.Observable;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(VertxUnitRunner.class)
public class TemplateEngineTest {

  private RunTestOnContext runTestOnContext = new RunTestOnContext();
  private TestVertxDeployer knotx = new TestVertxDeployer(runTestOnContext);

  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(runTestOnContext).around(knotx);

  @Test
  @KnotxConfiguration("knotx-template-engine-test.json")
  public void whenRequestedWithOneRawFragment_expectNotChangedHtml(TestContext context) throws Exception {
    Async async = context.async();

    // given
    final Vertx vertx = new Vertx(runTestOnContext.vertx());
    final EventBus eventBus = spy(vertx.eventBus());
    final TemplateEngine templateEngine = new TemplateEngine(eventBus, getConfig("template-engine-config.json"));

    final String expectedResponse = FileReader.readText("template-engine/one-raw-fragment/expected.html");

    // when
    Observable<String> result = templateEngine.process(getContext("one-raw-fragment", 1));

    // then
    result.subscribe(
        response -> {
          final String expectedClean = Jsoup.clean(expectedResponse, Whitelist.relaxed());
          final String responseClean = Jsoup.clean(expectedResponse, Whitelist.relaxed());
          context.assertEquals(expectedClean, responseClean);
          verify(eventBus, times(0)).sendObservable(anyString(), anyObject());
        },
        error -> context.fail(error.getMessage()),
        async::complete);
  }

  private ViewEngineConfiguration getConfig(String configFile) throws Exception {
    return new ViewEngineConfiguration(new JsonObject(FileReader.readText(configFile)));
  }

  private KnotContext getContext(String testResourcesPath, int fragmentsCount) throws Exception {
    ArrayList<Fragment> fragments = new ArrayList<>(fragmentsCount);
    for (int i = 1; i <= fragmentsCount; ++i) {
      final String fragmentContent = FileReader.readText(getFragmentResourcePath(testResourcesPath, i));
      String id = fragmentContent.startsWith("<script") ? "_snippet" : "_raw";
      fragments.add(new Fragment(new JsonObject().put("_ID", id).put("_CONTENT", fragmentContent)));
    }
    return new KnotContext().setFragments(fragments);
  }

  private String getFragmentResourcePath(String testResourcesPath, int i) {
    return "template-engine" + File.separator + testResourcesPath + File.separator + "fragment" + i + ".txt";
  }
}
