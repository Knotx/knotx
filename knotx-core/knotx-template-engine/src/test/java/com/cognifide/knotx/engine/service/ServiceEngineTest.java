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

import com.cognifide.knotx.api.TemplateEngineRequest;
import com.cognifide.knotx.engine.AbstractKnotxConfigurationTest;
import com.cognifide.knotx.engine.TemplateEngineConfiguration;

import org.jsoup.nodes.Attribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(KnotxRxHelper.class)
public class ServiceEngineTest extends AbstractKnotxConfigurationTest {


    private static final String MOCK_SERVICE_RESPONSE_JSON = "{\"welcomeInCompetition\":\"welcome in competition\",\"thankYouForSubscribingToCompetition\":\"thank you for subscribing to competition\",\"subscribeToNewsletter\":\"subscribe to newsletter\",\"thankYouForSubscribingToNewsletter\":\"thank you for subscribing to newsletter\",\"_response\":{\"statusCode\":200}}";
    private static final String FORM_RESPONSE_JSON = "{\"status\":\"success\",\"_response\":{\"statusCode\":200}}";

    private ServiceEngine serviceEngine;

    private String SERVICE_CONFIGURATION = "service-correct.json";


    @Before
    public void setUp() throws Exception {
        Vertx vertx = Vertx.vertx();
        TemplateEngineConfiguration configuration = new TemplateEngineConfiguration(readConfig(SERVICE_CONFIGURATION));
        serviceEngine = new ServiceEngine(vertx, configuration);
    }


    @Test
    public void findServiceLocation() throws Exception {

    }


    @Test
    public void test_ALL_ServiceCallWithFormPostRequest() throws Exception {

        ServiceEntry serviceEntry = createServiceEntry("data-uri-all-labelsrepository", "/service/mock/labelsRepository.json", MOCK_SERVICE_RESPONSE_JSON);

        TemplateEngineRequest templateEngineRequest = createFormPostRequest();

        Observable<Map<String, Object>> mapObservable = serviceEngine.doServiceCall(serviceEntry, templateEngineRequest);

        mapObservable.subscribe(map -> {
            assertThat(map.size(), equalTo(5));
            assertThat(new JsonObject(map).toString(), equalTo(MOCK_SERVICE_RESPONSE_JSON));
        });
    }


    @Test
    public void test_POST_ServiceCallWithGetRequest() throws Exception {

        ServiceEntry serviceEntry = createServiceEntry("data-uri-post-formresponse", "/service/mock/subscribeToNewsletter.json", FORM_RESPONSE_JSON);

        TemplateEngineRequest templateEngineRequest = createFormPostRequest();

        Observable<Map<String, Object>> mapObservable = serviceEngine.doServiceCall(serviceEntry, templateEngineRequest);

        mapObservable.subscribe(map -> {
            assertThat(map.size(), equalTo(2));
            assertThat(new JsonObject(map).toString(), equalTo(FORM_RESPONSE_JSON));
        });
    }


    private ServiceEntry createServiceEntry(String attrName, String serviceUrl, String serviceResponse) throws Exception {
        mockServiceResponse(serviceResponse);

        Attribute mockedServiceAttribute = new Attribute(attrName, serviceUrl);
        ServiceEntry serviceEntry = ServiceEntry.of(mockedServiceAttribute);
        TemplateEngineConfiguration correctConfig = new TemplateEngineConfiguration(readConfig(SERVICE_CONFIGURATION));

        serviceEntry.setServiceMetadata(correctConfig.getServices().stream().findFirst().get());
        return serviceEntry;
    }

    private TemplateEngineRequest createFormPostRequest() throws Exception {
        MultiMap headers = MultiMap.newInstance(new CaseInsensitiveHeaders());

        MultiMap formsAttributes = MultiMap.newInstance(new CaseInsensitiveHeaders());
        formsAttributes.add("_id", "newsletter");
        formsAttributes.add("email", "email@dom.com");

        return new TemplateEngineRequest(readText("fragment-form1.txt"), HttpMethod.POST, headers, formsAttributes);
    }

    private void mockServiceResponse(String responseJson) {
        HttpClientResponse httpClientResponse = Mockito.mock(HttpClientResponse.class);
        when(httpClientResponse.toObservable()).thenReturn(Observable.from(Lists.newArrayList(Buffer.buffer(responseJson))));
        when(httpClientResponse.statusCode()).thenReturn(200);

        PowerMockito.mockStatic(KnotxRxHelper.class);
        PowerMockito.when(KnotxRxHelper.request(anyObject(), anyObject(), anyInt(), anyString(), anyString(), anyObject())).thenReturn(Observable.just(httpClientResponse));
    }


}