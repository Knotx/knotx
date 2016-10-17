/*
 * Knot.x - Reactive microservice assembler - http service adapter
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
package com.cognifide.knotx.core.serviceadapter.http;

import com.cognifide.knotx.dataobjects.HttpResponseWrapper;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpClient;
import rx.Observable;

public class HttpClientFacadeTest {

  // Configuration

  private static final Integer PORT = 6001;

  private static final String DOMAIN = "knotxdomain.com";

  private static final String PATH = "/services/test.*";

  // Request payload

  private static final String REQUEST_PATH = "/services/test/service1";

  private static final HttpMethod REQUEST_METHOD = HttpMethod.GET;

  private static final List<Pattern> patterns = Collections.singletonList(Pattern.compile("X-test*"));

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private HttpClient httpClientMock;


  @Ignore
  @Test
  // FIXME: 17.10.2016
  public void whenNoAllowedHeadersAvailable_expectNoHeadersPassed() {
    // before
    List<HttpServiceAdapterConfiguration.ServiceMetadata> services = new ArrayList<>();
    services.add(new HttpServiceAdapterConfiguration.ServiceMetadata().setPort(PORT).setDomain(DOMAIN).setPath(PATH).setAllowedRequestHeaderPatterns(patterns));
    HttpClientFacade clientFacade = new HttpClientFacade(httpClientMock, services);

    // when
    Observable<HttpResponseWrapper> result = clientFacade.process(getPayloadMessage(REQUEST_PATH, REQUEST_METHOD));

    // then
    result.subscribe(response -> {
          Mockito.verify(httpClientMock).request(HttpMethod.GET, PORT, DOMAIN, REQUEST_PATH);
        },
        error -> Assert.fail(error.getMessage()));
  }

  private JsonObject getPayloadMessage(String servicePath, HttpMethod method) {
    return new JsonObject()
        .put("path", servicePath)
        .put("method", method);
  }


}
