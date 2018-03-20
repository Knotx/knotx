/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.adapter.common.http;

import io.knotx.adapter.common.configuration.ServiceAdapterOptions;
import io.knotx.adapter.common.configuration.ServiceSettings;
import io.knotx.adapter.common.exception.AdapterServiceContractException;
import io.knotx.adapter.common.exception.UnsupportedServiceException;
import io.knotx.adapter.common.placeholders.UriTransformer;
import io.knotx.configuration.CustomHttpHeader;
import io.knotx.dataobjects.AdapterRequest;
import io.knotx.dataobjects.ClientRequest;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.http.AllowedHeadersFilter;
import io.knotx.http.MultiMapCollector;
import io.knotx.util.DataObjectsUtil;
import io.reactivex.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;

public class HttpClientFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientFacade.class);
  private static final String PATH_PROPERTY_KEY = "path";
  private static final String QUERY_PARAMS_PROPERTY_KEY = "queryParams";
  private static final String HEADERS_PROPERTY_KEY = "headers";

  private final List<ServiceSettings> services;

  private final WebClient webClient;

  private final CustomHttpHeader customHttpHeader;

  public HttpClientFacade(WebClient webClient, ServiceAdapterOptions configuration) {
    this.webClient = webClient;
    this.services = configuration.getServices();
    this.customHttpHeader = configuration.getCustomHttpHeader();
  }

  public Single<ClientResponse> process(AdapterRequest message, HttpMethod method) {
    return Single.just(message)
        .doOnSuccess(this::validateContract)
        .map(this::prepareRequestData)
        .flatMap(
            serviceRequest -> callService(serviceRequest, method)
                .doOnSuccess(resp -> logResponse(serviceRequest, resp)))
        .flatMap(this::wrapResponse);
  }

  private void logResponse(Pair<ClientRequest, ServiceSettings> request,
      HttpResponse<Buffer> resp) {
    if (resp.statusCode() >= 400 && resp.statusCode() < 600) {
      LOGGER.error("{} {} -> Got response {}, headers[{}]",
          logResponseData(request, resp));
    } else if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("{} {} -> Got response {}, headers[{}]",
          logResponseData(request, resp));
    }
  }

  private Object[] logResponseData(Pair<ClientRequest, ServiceSettings> request,
      HttpResponse<Buffer> resp) {
    Object[] data = {
        request.getLeft().getMethod(),
        toUrl(request),
        resp.statusCode(),
        DataObjectsUtil.toString(resp.headers())};

    return data;
  }

  private String toUrl(Pair<ClientRequest, ServiceSettings> request) {
    return new StringBuilder(request.getRight().getDomain()).append(request.getRight().getPort())
        .append(request.getLeft().getPath()).toString();
  }

  /**
   * Method to validate contract or params JsonObject for the AdapterProxy Service<br>
   * The contract checks if all required fields exists in the object.
   * throwing AdapterServiceContractException in case of contract violation.<br>
   *
   * @param message - Event Bus Json Object message that contains 'clientRequest' and 'params'
   * objects.
   */
  protected void validateContract(AdapterRequest message) {
    if (message.getParams() == null || !message.getParams().containsKey(PATH_PROPERTY_KEY)) {
      throw new AdapterServiceContractException("Parameter `path` was not defined in `params`!");
    }
  }

  /**
   * Method responsible for building request to the service.
   * <br>
   * <br>
   * The responsibility of the method is to build ClientRequest based on the original Http
   * Request<br>
   * - It must set path property of the request based on the params<br>
   * - It might set headers of the request if needed.<br>
   * <br>
   * In case of headers created modified in this method, ensure that your service configuration
   * allows passing those headers to the target service. See 'allowedRequestHeaders' section
   * of the configuration <br>
   *
   * @param originalRequest - ClientRequest representing original request comming to the Knot.x
   * @param params - JsonObject of the params to be used to build request.
   * @return ClientRequest representing Http request to the target service
   */
  protected ClientRequest buildServiceRequest(ClientRequest originalRequest, JsonObject params) {
    return new ClientRequest(originalRequest)
        .setPath(UriTransformer
            .resolveServicePath(params.getString(PATH_PROPERTY_KEY), originalRequest));
  }

  private Pair<ClientRequest, ServiceSettings> prepareRequestData(AdapterRequest adapterRequest) {
    final Pair<ClientRequest, ServiceSettings> serviceData;

    final JsonObject params = adapterRequest.getParams();
    final ClientRequest serviceRequest = buildServiceRequest(adapterRequest.getRequest(), params);
    final Optional<ServiceSettings> serviceMetadata = findServiceMetadata(serviceRequest.getPath());

    if (serviceMetadata.isPresent()) {
      final ServiceSettings metadata = serviceMetadata.get();
      if (params.containsKey(HEADERS_PROPERTY_KEY)) {
        metadata.setAdditionalHeaders(params.getJsonObject(HEADERS_PROPERTY_KEY));
      }
      if (params.containsKey(QUERY_PARAMS_PROPERTY_KEY)) {
        metadata.setQueryParams(params.getJsonObject(QUERY_PARAMS_PROPERTY_KEY));
      }
      serviceData = Pair.of(serviceRequest, metadata);
    } else {
      final String error = String
          .format("No matching service definition for the requested path '%s'",
              serviceRequest.getPath());
      throw new UnsupportedServiceException(error);
    }
    return serviceData;
  }

  private Optional<ServiceSettings> findServiceMetadata(String servicePath) {
    return services.stream().filter(metadata -> servicePath.matches(metadata.getPath())).findAny();
  }

  private Single<HttpResponse<Buffer>> callService(
      Pair<ClientRequest, ServiceSettings> serviceData, HttpMethod method) {
    final Single<HttpResponse<Buffer>> httpResponse;

    final ClientRequest serviceRequest = serviceData.getLeft();
    final ServiceSettings serviceMetadata = serviceData.getRight();

    final HttpRequest<Buffer> request = webClient
        .request(method, serviceMetadata.getPort(), serviceMetadata.getDomain(),
            serviceRequest.getPath());

    updateRequestQueryParams(request, serviceMetadata);
    updateRequestHeaders(request, serviceRequest, serviceMetadata);
    overrideRequestHeaders(request, serviceMetadata);

    if (!serviceRequest.getFormAttributes().isEmpty()) {
      httpResponse = request.rxSendForm(serviceRequest.getFormAttributes());
    } else {
      httpResponse = request.rxSend();
    }

    return httpResponse;
  }

  private void overrideRequestHeaders(HttpRequest<Buffer> request, ServiceSettings metadata) {
    if (metadata.getAdditionalHeaders() != null) {
      metadata.getAdditionalHeaders().forEach(entry -> {
        request.putHeader(entry.getKey(), entry.getValue().toString());
      });
    }
  }

  private void updateRequestQueryParams(HttpRequest<Buffer> request, ServiceSettings metadata) {
    if (metadata.getQueryParams() != null) {
      metadata.getQueryParams().forEach(entry ->
          request.addQueryParam(entry.getKey(), entry.getValue().toString())
      );
    }
  }

  private void updateRequestHeaders(HttpRequest<Buffer> request, ClientRequest serviceRequest,
      ServiceSettings serviceMetadata) {

    MultiMap filteredHeaders = getFilteredHeaders(serviceRequest.getHeaders(),
        serviceMetadata.getAllowedRequestHeadersPatterns());
    filteredHeaders.names().forEach(
        headerName -> filteredHeaders.getAll(headerName)
            .forEach(value -> request.headers().add(headerName, value)));

    if (customHttpHeader != null) {
      request.headers().set(
          customHttpHeader.getName(),
          customHttpHeader.getValue()
      );
    }
  }

  private MultiMap getFilteredHeaders(MultiMap headers, List<Pattern> allowedHeaders) {
    return headers.names().stream()
        .filter(AllowedHeadersFilter.create(allowedHeaders))
        .collect(MultiMapCollector.toMultiMap(o -> o, headers::getAll));
  }

  private Single<ClientResponse> wrapResponse(HttpResponse<Buffer> response) {
    return toBody(response)
        .doOnSuccess(this::traceServiceCall)
        .map(buffer -> new ClientResponse()
            .setBody(buffer.getDelegate())
            .setHeaders(response.headers())
            .setStatusCode(response.statusCode())
        );
  }

  private Single<Buffer> toBody(HttpResponse<Buffer> response) {
    if (response.body() != null) {
      return Single.just(response.body());
    } else {
      LOGGER.warn("Service returned empty body");
      return Single.just(Buffer.buffer());
    }
  }

  private void traceServiceCall(Buffer results) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Service call returned <{}>", results.toString());
    }
  }
}
