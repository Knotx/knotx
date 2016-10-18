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
package com.cognifide.knotx.engine.service;

import com.google.common.collect.Lists;

import com.cognifide.knotx.dataobjects.HttpRequestWrapper;
import com.cognifide.knotx.dataobjects.RenderRequest;
import com.cognifide.knotx.engine.TemplateEngineConfiguration;
import com.cognifide.knotx.junit.FileReader;
import com.cognifide.knotx.junit.Logback;

import org.jsoup.nodes.Attribute;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(KnotxRxHelper.class)
public class ServiceEngineTest {
  private static final String MOCK_SERVICE_RESPONSE_JSON = "{\"welcomeInCompetition\":\"welcome in competition\",\"thankYouForSubscribingToCompetition\":\"thank you for subscribing to competition\",\"subscribeToNewsletter\":\"subscribe to newsletter\",\"thankYouForSubscribingToNewsletter\":\"thank you for subscribing to newsletter\",\"_response\":{\"statusCode\":200}}";
  private final RunTestOnContext vertxRule = new RunTestOnContext();
  @Rule
  public RuleChain ruleChain = RuleChain.outerRule(new Logback()).around(vertxRule);
  private ServiceEngine serviceEngine;
  private JsonObject config;

  @Before
  public void setUp() throws Exception {
    config = new JsonObject(FileReader.readText("service-correct.json"));
    TemplateEngineConfiguration configuration = new TemplateEngineConfiguration(config);
    serviceEngine = new ServiceEngine(Vertx.newInstance(vertxRule.vertx()).eventBus(), configuration);
  }

//  @Test
//  public void test_ALL_ServiceCallWithFormPostRequest() throws Exception {
//    ServiceEntry serviceEntry = createServiceEntry("labelsrepository", "name",  "{\"path\":\"/service/mock/labelsRepository.json\"}", MOCK_SERVICE_RESPONSE_JSON);
//
//    RenderRequest templateEngineRequest = createFormPostRequest();
//
//    Observable<JsonObject> mapObservable = serviceEngine.doServiceCall(serviceEntry, templateEngineRequest);
//
//    mapObservable.subscribe(obj -> {
//      assertThat(obj.size(), equalTo(2));
//      assertThat(obj.getJsonObject("_result").toString(), equalTo(MOCK_SERVICE_RESPONSE_JSON));
//    });
//  }


  private ServiceEntry createServiceEntry(String namespace, String name, String params, String serviceResponse) throws Exception {
    mockServiceResponse(serviceResponse);

    Attribute mockedServiceAttribute = new Attribute("data-service-" + namespace, name);
    Attribute mockedParamsAttribute = new Attribute("data-params-" + namespace, params);
    ServiceEntry serviceEntry = new ServiceEntry(mockedServiceAttribute, mockedParamsAttribute);
    TemplateEngineConfiguration correctConfig = new TemplateEngineConfiguration(config);

    serviceEntry.mergeParams(correctConfig.getServices().stream().findFirst().get().getParams());
    serviceEntry.setAddress(correctConfig.getServices().stream().findFirst().get().getAddress());
    return serviceEntry;
  }

  private RenderRequest createFormPostRequest() throws Exception {
    MultiMap headers = MultiMap.newInstance(new CaseInsensitiveHeaders());

    MultiMap formsAttributes = MultiMap.newInstance(new CaseInsensitiveHeaders());
    formsAttributes.add("_id", "newsletter");
    formsAttributes.add("email", "email@dom.com");

    HttpRequestWrapper httpRequest = new HttpRequestWrapper().setMethod(HttpMethod.POST).setHeaders(headers).setFormAttributes(formsAttributes);
    return new RenderRequest().setRequest(httpRequest).setTemplate(FileReader.readText("fragment-form1.txt"));
  }

  private void mockServiceResponse(String responseJson) {
    HttpClientResponse httpClientResponse = Mockito.mock(HttpClientResponse.class);
    when(httpClientResponse.toObservable()).thenReturn(Observable.from(Lists.newArrayList(Buffer.buffer(responseJson))));
    when(httpClientResponse.statusCode()).thenReturn(HttpResponseStatus.OK.code());

    PowerMockito.mockStatic(KnotxRxHelper.class);
    PowerMockito.when(KnotxRxHelper.request(anyObject(), anyObject(), anyInt(), anyString(), anyString(), anyObject())).thenReturn(Observable.just(httpClientResponse));
  }


}
