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
package com.cognifide.knotx.knot.service.impl;

import com.cognifide.knotx.codec.AdapterRequestCodec;
import com.cognifide.knotx.codec.AdapterResponseCodec;
import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.AdapterResponse;
import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.junit.FileReader;
import com.cognifide.knotx.junit.Logback;
import com.cognifide.knotx.knot.service.ServiceKnotConfiguration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Action2;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(VertxUnitRunner.class)
public class TemplateEngineTest {

  private static final String TEST_RESOURCES_ROOT = "template-engine";
  private static final String TEMPLATE_ENGINE_CONFIG_JSON = TEST_RESOURCES_ROOT + "/test-config.json";
  private static final String EXPECTED_RESULT_FILE_NAME = "expected.html";
  private static final String FRAGMENT_FILE_NAME_PATTERN = "fragment%d.txt";
  private static final Document.OutputSettings OUTPUT_SETTINGS = new Document.OutputSettings()
      .escapeMode(Entities.EscapeMode.xhtml)
      .indentAmount(0)
      .prettyPrint(false);

  private RunTestOnContext runTestOnContext = new RunTestOnContext();
  @Rule
  public RuleChain chain = RuleChain.outerRule(new Logback()).around(runTestOnContext);
  private Vertx vertx;

  @Before
  public void setUp() throws Exception {
    vertx = Vertx.newInstance(runTestOnContext.vertx());
    ((io.vertx.core.eventbus.EventBus)vertx.eventBus().getDelegate()).registerDefaultCodec(AdapterRequest.class, new AdapterRequestCodec());
    ((io.vertx.core.eventbus.EventBus)vertx.eventBus().getDelegate()).registerDefaultCodec(AdapterResponse.class, new AdapterResponseCodec());
    vertx.eventBus().<AdapterRequest>consumer("knotx.mock.service-adapter").handler(this::mockServiceAdapter);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void whenRequestedWithOneRawFragment_expectNotChangedHtmlAndNoServiceCalls(TestContext context) throws Exception {
    final String resourcesFolderName = "one-raw-fragment";
    final String expectedResponse = getExpectedResponse(resourcesFolderName);

    testViewEngineProcessing(context, resourcesFolderName, 1, (result, eventBus) -> {
          context.assertEquals(unifyHtml(expectedResponse), unifyHtml(result));
          verify(eventBus, times(0)).send(anyString(), anyObject(), (Handler<AsyncResult<Message<Object>>>) anyObject());
        },
        error -> context.fail(error.getMessage())
    );
  }

  @SuppressWarnings("unchecked")
  @Test
  public void whenRequestedWithOneSnippetFragment_expectHtmlAndOneServiceCall(TestContext context) throws Exception {
    final String resourcesFolderName = "one-snippet-fragment";
    final String expectedResponse = getExpectedResponse(resourcesFolderName);

    testViewEngineProcessing(context, resourcesFolderName, 1, (result, eventBus) -> {
          context.assertEquals(unifyHtml(expectedResponse), unifyHtml(result));
          verify(eventBus, times(1)).send(anyString(), anyObject(), (Handler<AsyncResult<Message<Object>>>) anyObject());
        },
        error -> context.fail(error.getMessage())
    );
  }

  @SuppressWarnings("unchecked")
  @Test
  public void whenRequestedWithSevenMixedFragments_expectHtmlAndThreeServiceCall(TestContext context) throws Exception {
    final String resourcesFolderName = "seven-mixed-fragments";
    final String expectedResponse = getExpectedResponse(resourcesFolderName);

    testViewEngineProcessing(context, resourcesFolderName, 7, (result, eventBus) -> {
          context.assertEquals(unifyHtml(expectedResponse), unifyHtml(result));
          verify(eventBus, times(3)).send(anyString(), anyObject(), (Handler<AsyncResult<Message<Object>>>) anyObject());
        },
        error -> context.fail(error.getMessage())
    );
  }

  @SuppressWarnings("unchecked")
  @Test
  public void whenRequestedWithSevenMixedFragmentsAllCallsToSingleService_expectHtmlAndOneServiceCall(TestContext context) throws Exception {
    final String resourcesFolderName = "multiple-calls-to-single-service";
    final String expectedResponse = getExpectedResponse(resourcesFolderName);

    testViewEngineProcessing(context, resourcesFolderName, 7, (result, eventBus) -> {
          context.assertEquals(unifyHtml(expectedResponse), unifyHtml(result));
          verify(eventBus, times(1)).send(anyString(), anyObject(), (Handler<AsyncResult<Message<Object>>>) anyObject());
        },
        error -> context.fail(error.getMessage())
    );
  }

  private void testViewEngineProcessing(TestContext context, String resourcesFolderName, int fragmentsNo, Action2<String, EventBus> onSuccess, Action1<Throwable> onError) throws Exception {
    Async async = context.async();

    // given
    final EventBus eventBus = spy(vertx.eventBus());
    final TemplateEngine templateEngine = new TemplateEngine(eventBus, getConfig(TEMPLATE_ENGINE_CONFIG_JSON));

    // when
    Observable<String> result = templateEngine.process(getContext(resourcesFolderName, fragmentsNo));

    // then
    result.subscribe(
        testResult -> onSuccess.call(testResult, eventBus),
        onError,
        async::complete);
  }

  private ServiceKnotConfiguration getConfig(String configFile) throws Exception {
    return new ServiceKnotConfiguration(new JsonObject(FileReader.readText(configFile)));
  }

  private KnotContext getContext(String testResourcesPath, int fragmentsCount) throws Exception {
    ArrayList<Fragment> fragments = new ArrayList<>(fragmentsCount);
    for (int i = 1; i <= fragmentsCount; ++i) {
      final String fragmentContent = FileReader.readText(getFragmentResourcePath(testResourcesPath, i));
      String id = fragmentContent.startsWith("<script") ? "_snippet" : "_raw";
      fragments.add(new Fragment(new JsonObject().put("_ID", id).put("_CONTENT", fragmentContent)));
    }
    return new KnotContext().setFragments(fragments).setClientRequest(new ClientRequest());
  }

  private String getFragmentResourcePath(String testResourcesPath, int i) {
    return TEST_RESOURCES_ROOT + File.separator + testResourcesPath + File.separator + String.format(FRAGMENT_FILE_NAME_PATTERN, i);
  }

  private String getExpectedResponse(String testName) throws Exception {
    return FileReader.readText(TEST_RESOURCES_ROOT + File.separator + testName + File.separator + EXPECTED_RESULT_FILE_NAME);
  }

  private String unifyHtml(String html) {
    return Jsoup.parse(html.replace("\n", ""), "UTF-8", Parser.xmlParser())
        .outputSettings(OUTPUT_SETTINGS)
        .html()
        .trim();
  }

  private void mockServiceAdapter(Message<AdapterRequest> message) {
    Observable.just(message.body())
        .subscribe(
            req -> {
              final String resourcePath = req.params().getString("path");
              try {
                final String responseBody = FileReader.readText(resourcePath);
                message.reply(new AdapterResponse().setResponse(new ClientResponse().setStatusCode(HttpResponseStatus.OK).setBody(Buffer.buffer(responseBody))));
              } catch (Exception e) {
                message.reply(new AdapterResponse().setResponse(new ClientResponse().setStatusCode(HttpResponseStatus.NOT_FOUND)));
              }
            },
            error -> message.reply(new AdapterResponse().setResponse(new ClientResponse().setStatusCode(HttpResponseStatus.NOT_FOUND)))
        );
  }
}
