/*
 * Knot.x - Reactive microservice assembler - Http Service Adapter
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
package com.cognifide.knotx.adapter.common.http;

import com.cognifide.knotx.adapter.common.exception.AdapterServiceContractException;
import com.cognifide.knotx.adapter.common.exception.UnsupportedServiceException;
import com.cognifide.knotx.adapter.common.placeholders.UriTransformer;
import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;

public class HttpClientFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientFacade.class);
  private static final String REQUEST_KEY = "clientRequest";
  private static final String PARAMS_KEY = "params";
  private static final String PATH_PROPERTY_KEY = "path";
  private static final ClientResponse INTERNAL_SERVER_ERROR_RESPONSE = new ClientResponse().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);

  private final List<ServiceMetadata> services;

  private final HttpClient httpClient;

  public HttpClientFacade(HttpClient httpClient, List<ServiceMetadata> services) {
    this.httpClient = httpClient;
    this.services = services;
  }

  public Observable<ClientResponse> process(JsonObject message, HttpMethod method) {
    return Observable.just(message)
        .doOnNext(this::validateContract)
        .map(this::prepareRequestData)
        .flatMap(serviceRequest -> callService(serviceRequest, method))
        .flatMap(this::wrapResponse)
        .defaultIfEmpty(INTERNAL_SERVER_ERROR_RESPONSE);
  }

  /**
   * Method to validate contract or params JsonObject for the Adapter Service<br/>
   * The contract checks if all required fields exists in the object. throwing AdapterServiceContractException
   * in case of contract violation.<br/>
   * @param message - Event Bus Json Object message that contains 'clientRequest' and 'params' objects.
   */
  protected void validateContract(JsonObject message) {
    final boolean pathPresent = message.getJsonObject(PARAMS_KEY).containsKey(PATH_PROPERTY_KEY);
    if (!pathPresent) {
      throw new AdapterServiceContractException("Parameter `path` was not defined in `params`!");
    }
  }

  /**
   * Method responsible for building request to the service.</br>
   * <br/>
   * The responsibility of the method is to build ClientRequest based on the original Http Request<br/>
   *   - It must set path property of the request based on the params<br/>
   *   - It might set headers of the request if needed.</br>
   * <br/>
   * In case of headers created modified in this method, ensure that your service configuration
   * allows passing those headers to the target service. See 'allowed.request.headers' section of the configuration
   * </br>
   * @param originalRequest - ClientRequest representing original request comming to the Knot.x
   * @param params - JsonObject of the params to be used to build request.
   * @return ClientRequest representing Http request to the target service
   */
  protected ClientRequest buildServiceRequest(ClientRequest originalRequest, JsonObject params) {
    return new ClientRequest(originalRequest.toJson())
        .setPath(UriTransformer.resolveServicePath(params.getString(PATH_PROPERTY_KEY), originalRequest));
  }

  private Pair<ClientRequest, ServiceMetadata> prepareRequestData(JsonObject message) {
    final Pair<ClientRequest, ServiceMetadata> serviceData;

    final ClientRequest originalRequest = new ClientRequest(message.getJsonObject(REQUEST_KEY));
    final ClientRequest serviceRequest = buildServiceRequest(originalRequest, message.getJsonObject(PARAMS_KEY));

    final Optional<ServiceMetadata> serviceMetadata = findServiceMetadata(serviceRequest.path());
    if (serviceMetadata.isPresent()) {
      serviceData = Pair.of(serviceRequest, serviceMetadata.get());
    } else {
      final String error = String.format("Parameter `params.path`: `%s` not supported!", serviceRequest.path());
      throw new UnsupportedServiceException(error);
    }
    return serviceData;
  }

  private Optional<ServiceMetadata> findServiceMetadata(String servicePath) {
    return services.stream().filter(metadata -> servicePath.matches(metadata.getPath())).findAny();
  }

  private Observable<HttpClientResponse> callService(Pair<ClientRequest, ServiceMetadata> serviceData, HttpMethod method) {
    final ClientRequest serviceRequest = serviceData.getLeft();
    final ServiceMetadata serviceMetadata = serviceData.getRight();

    return Observable.create(subscriber -> {
      HttpClientRequest httpRequest = httpClient.request(method, serviceMetadata.getPort(), serviceMetadata.getDomain(), serviceRequest.path());
      Observable<HttpClientResponse> resp = httpRequest.toObservable();
      resp.subscribe(subscriber);
      httpRequest.headers().addAll(getFilteredHeaders(serviceRequest.headers(), serviceMetadata.getAllowedRequestHeaderPatterns()));
      httpRequest.headers().remove(HttpHeaders.CONTENT_LENGTH.toString());

      httpRequest.end();
    });
  }

  private MultiMap getFilteredHeaders(MultiMap headers, List<Pattern> allowedHeaders) {
    return headers.names().stream()
        .filter(AllowedHeadersFilter.create(allowedHeaders))
        .collect(MultiMapCollector.toMultimap(o -> o, headers::get));
  }

  private Observable<ClientResponse> wrapResponse(HttpClientResponse response) {
    return Observable.just(Buffer.buffer())
        .mergeWith(response.toObservable())
        .reduce(Buffer::appendBuffer)
        .doOnNext(this::traceServiceCall)
        .map(buffer -> new ClientResponse()
            .setBody(buffer)
            .setHeaders(response.headers())
            .setStatusCode(HttpResponseStatus.valueOf(response.statusCode()))
        );
  }

  private void traceServiceCall(Buffer results) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Service call returned <{}>", results.toJsonObject().encodePrettily());
    }
  }
}
