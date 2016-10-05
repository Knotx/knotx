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


import com.cognifide.knotx.dataobjects.TemplateEngineRequest;
import com.cognifide.knotx.engine.AbstractKnotxConfigurationTest;
import com.cognifide.knotx.engine.TemplateEngineConfiguration;
import com.cognifide.knotx.engine.parser.HtmlFragment;
import com.cognifide.knotx.engine.parser.HtmlParser;

import org.jsoup.nodes.Attribute;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.MultiMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(KnotxRxHelper.class)
public class ServiceEntryTest extends AbstractKnotxConfigurationTest {

  private static HtmlFragment htmlFragment;

  private static ServiceEntry serviceEntryAll;

  private static ServiceEntry serviceEntryGet;

  private static ServiceEntry serviceEntryPost;

  @BeforeClass
  public static void setUp() throws Exception {
    htmlFragment = new HtmlParser(readText("test.html")).getFragments().get(1);
    serviceEntryAll = createServiceEntry("data-uri-all-labelsrepository", "/service/mock/labelsRepository.json");
    serviceEntryGet = createServiceEntry("data-uri-get-labelsrepository", "/service/mock/labelsRepository.json");
    serviceEntryPost = createServiceEntry("data-uri-post-labelsrepository", "/service/mock/labelsRepository.json");
  }

  @Test
  public void canServeRequest_whenPostWithFormIdAndAllAttribute_expectRequestServed() throws Exception {
    boolean result = serviceEntryAll.canServeRequest(htmlFragment, createRequest(HttpMethod.POST, true));
    assertThat(result, equalTo(true));
  }

  @Test
  public void canServeRequest_whenPostWithoutFormIdAndAllAttribute_expectRequestServed() throws Exception {
    boolean result = serviceEntryAll.canServeRequest(htmlFragment, createRequest(HttpMethod.POST, false));
    assertThat(result, equalTo(true));
  }

  @Test
  public void canServeRequest_whenGetWithFormIdAndAllAttribute_expectRequestServed() throws Exception {
    boolean result = serviceEntryAll.canServeRequest(htmlFragment, createRequest(HttpMethod.GET, true));
    assertThat(result, equalTo(true));
  }

  @Test
  public void canServeRequest_whenGetWithoutFormIdAndAllAttribute_expectRequestServed() throws Exception {
    boolean result = serviceEntryAll.canServeRequest(htmlFragment, createRequest(HttpMethod.GET, false));
    assertThat(result, equalTo(true));
  }

  @Test
  public void canServeRequest_whenPostWithFormIdAndGetAttribute_expectRequestNotServed() throws Exception {
    boolean result = serviceEntryGet.canServeRequest(htmlFragment, createRequest(HttpMethod.POST, true));
    assertThat(result, equalTo(false));
  }

  @Test
  public void canServeRequest_whenPostWithoutFormIdAndGetAttribute_expectRequestNotServed() throws Exception {
    boolean result = serviceEntryGet.canServeRequest(htmlFragment, createRequest(HttpMethod.POST, false));
    assertThat(result, equalTo(false));
  }

  @Test
  public void canServeRequest_whenGetWithFormIdAndGetAttribute_expectRequestServed() throws Exception {
    boolean result = serviceEntryGet.canServeRequest(htmlFragment, createRequest(HttpMethod.GET, true));
    assertThat(result, equalTo(true));
  }

  @Test
  public void canServeRequest_whenGetWithoutFormIdAndGetAttribute_expectRequestServed() throws Exception {
    boolean result = serviceEntryGet.canServeRequest(htmlFragment, createRequest(HttpMethod.GET, false));
    assertThat(result, equalTo(true));
  }


  @Test
  public void canServeRequest_whenPostWithFormIdAndPostAttribute_expectRequestNotServed() throws Exception {
    boolean result = serviceEntryPost.canServeRequest(htmlFragment, createRequest(HttpMethod.POST, true));
    assertThat(result, equalTo(false));
  }

  @Test
  public void canServeRequest_whenPostWithoutFormIdAndPostAttribute_expectRequestServed() throws Exception {
    boolean result = serviceEntryPost.canServeRequest(htmlFragment, createRequest(HttpMethod.POST, false));
    assertThat(result, equalTo(true));
  }

  @Test
  public void canServeRequest_whenGetWithFormIdAndPostAttribute_expectRequestNotServed() throws Exception {
    boolean result = serviceEntryPost.canServeRequest(htmlFragment, createRequest(HttpMethod.GET, true));
    assertThat(result, equalTo(false));
  }

  @Test
  public void canServeRequest_whenWithoutFormIdAndPostAttribute_expectRequestNotServed() throws Exception {
    boolean result = serviceEntryPost.canServeRequest(htmlFragment, createRequest(HttpMethod.GET, false));
    assertThat(result, equalTo(false));
  }

  private static ServiceEntry createServiceEntry(String attrName, String serviceUrl) throws Exception {
    Attribute mockedServiceAttribute = new Attribute(attrName, serviceUrl);
    ServiceEntry serviceEntry = ServiceEntry.of(mockedServiceAttribute);
    TemplateEngineConfiguration correctConfig = new TemplateEngineConfiguration(readConfig("service-correct.json"));

    serviceEntry.setServiceMetadata(correctConfig.getServices().stream().findFirst().get());
    return serviceEntry;
  }

  private TemplateEngineRequest createRequest(HttpMethod method, boolean withAttributes) throws Exception {
    MultiMap headers = MultiMap.newInstance(new CaseInsensitiveHeaders());

    MultiMap formsAttributes = MultiMap.newInstance(new CaseInsensitiveHeaders());
    if (withAttributes) {
      formsAttributes.add("_id", "newsletter");
      formsAttributes.add("email", "email@dom.com");
    }

    return new TemplateEngineRequest(readText("fragment-form1.txt"), method, headers, new MultiMap(null), formsAttributes, "");
  }

}
