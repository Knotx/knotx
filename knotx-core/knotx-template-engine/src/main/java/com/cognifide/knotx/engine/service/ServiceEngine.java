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

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import com.cognifide.knotx.dataobjects.RenderRequest;
import com.cognifide.knotx.dataobjects.ServiceCallMethod;
import com.cognifide.knotx.engine.AllowedHeadersFilter;
import com.cognifide.knotx.engine.MultiMapCollector;
import com.cognifide.knotx.engine.TemplateEngineConfiguration;
import com.cognifide.knotx.engine.placeholders.UriTransformer;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

public class ServiceEngine {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEngine.class);
  private static final String RESULT_NAMESPACE_KEY = "_result";
  private static final String RESPONSE_NAMESPACE_KEY = "_response";

  private final TemplateEngineConfiguration configuration;

  private final HttpClient httpClient;

  public ServiceEngine(HttpClient httpClient, TemplateEngineConfiguration serviceConfiguration) {
    this.httpClient = httpClient;
    this.configuration = serviceConfiguration;
  }

  public Observable<JsonObject> doServiceCall(ServiceEntry serviceEntry,
                                              RenderRequest renderRequest) {
    HttpMethod httpMethod = computeServiceMethodType(renderRequest, serviceEntry.getMethodType());
    Observable<HttpClientResponse> serviceResponse =
        KnotxRxHelper.request(
            httpClient,
            httpMethod,
            serviceEntry.getPort(),
            serviceEntry.getDomain(),
            UriTransformer.getServiceUri(renderRequest, serviceEntry),
            req -> buildRequestBody(req,
                getFilteredHeaders(renderRequest.request().headers(), serviceEntry.getHeadersPatterns()),
                renderRequest.request().formAttributes(),
                httpMethod
            )
        );
    return serviceResponse.flatMap(item -> transformResponse(item, serviceEntry));
  }

  private Observable<JsonObject> transformResponse(HttpClientResponse response, ServiceEntry serviceEntry) {
    return Observable.just(Buffer.buffer()).mergeWith(response.toObservable())
        .reduce(Buffer::appendBuffer)
        .doOnNext(buffer -> traceServiceCall(buffer, serviceEntry))
        .map(this::buildResultObject)
        .map(results ->
            results.put(RESPONSE_NAMESPACE_KEY, new JsonObject().put("statusCode", response.statusCode()))
        );
  }

  private void buildRequestBody(HttpClientRequest request, MultiMap headers,
                                MultiMap formAttributes, HttpMethod httpMethod) {
    request.headers().addAll(headers);

    if (!formAttributes.isEmpty() && HttpMethod.POST.equals(httpMethod)) {
      Buffer buffer = createFormPostBody(formAttributes);
      request.headers().set("content-length", String.valueOf(buffer.length()));
      request.headers().set("content-type", "application/x-www-form-urlencoded");
      request.write(buffer);
    }
  }

  private MultiMap getFilteredHeaders(MultiMap headers, List<Pattern> allowedHeaders) {
    return headers.names().stream()
        .filter(AllowedHeadersFilter.create(allowedHeaders))
        .collect(MultiMapCollector.toMultimap(o -> o, headers::get));
  }


  private Buffer createFormPostBody(MultiMap formAttributes) {
    Buffer buffer = Buffer.buffer();

    String formPostContent = Joiner.on("&").withKeyValueSeparator("=")
        .join((Iterable<Map.Entry<String, String>>) formAttributes.getDelegate());
    buffer.appendString(formPostContent, Charsets.UTF_8.toString());
    return buffer;
  }

  public ServiceEntry findServiceLocation(final ServiceEntry serviceEntry) {
    return configuration.getServices().stream()
        .filter(service -> serviceEntry.getServiceUri().matches(service.getPath()))
        .findFirst().map(metadata -> serviceEntry.setServiceMetadata(metadata))
        .get();
  }

  private HttpMethod computeServiceMethodType(RenderRequest renderRequest,
                                              ServiceCallMethod serviceCallMethod) {
    if (HttpMethod.POST.equals(renderRequest.request().method())
        && ServiceCallMethod.POST.equals(serviceCallMethod)) {
      return HttpMethod.POST;
    } else {
      return HttpMethod.GET;
    }
  }

  private JsonObject buildResultObject(Buffer buffer) {
    JsonObject object = new JsonObject();

    String rawData = buffer.toString().trim();

    if (rawData.charAt(0) == '[') {
      object.put(RESULT_NAMESPACE_KEY, new JsonArray(rawData));
    } else if (rawData.charAt(0) == '{') {
      object.put(RESULT_NAMESPACE_KEY, new JsonObject(rawData));
    } else {
      throw new DecodeException("Result is neither Json Array nor Json Object");
    }

    return object;
  }

  private void traceServiceCall(Buffer results, ServiceEntry entry) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Service call returned <{}> <{}>", results.toJsonObject().encodePrettily(), entry.getServiceUri());
    }
  }
}
