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
package com.cognifide.knotx.adapter.action.http;


import com.cognifide.knotx.adapter.api.AbstractAdapter;
import com.cognifide.knotx.adapter.common.http.HttpAdapterConfiguration;
import com.cognifide.knotx.adapter.common.http.HttpClientFacade;
import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.AdapterResponse;
import com.cognifide.knotx.dataobjects.ClientResponse;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import rx.Observable;

public class HttpActionAdapterVerticle extends AbstractAdapter<HttpAdapterConfiguration> {

  private HttpClientFacade httpClientFacade;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.httpClientFacade = new HttpClientFacade(getHttpClient(configuration), configuration.getServices());
  }

  @Override
  protected HttpAdapterConfiguration initConfiguration(JsonObject config) {
    return new HttpAdapterConfiguration(config());
  }

  @Override
  protected Observable<AdapterResponse> processMessage(AdapterRequest message) {
    return httpClientFacade.process(message, HttpMethod.POST)
        .map(this::prepareResponse);
  }

  private HttpClient getHttpClient(HttpAdapterConfiguration configuration) {
    JsonObject clientOptions = configuration.getClientOptions();
    return clientOptions.isEmpty() ?
        vertx.createHttpClient() : vertx.createHttpClient(new HttpClientOptions(clientOptions));
  }

  private AdapterResponse prepareResponse(ClientResponse response) {
    AdapterResponse result = new AdapterResponse();

    if (response.statusCode() == HttpResponseStatus.OK) {
      if (isJsonBody(response.body()) && response.body().toJsonObject().containsKey("validationErrors")) {
        result.setSignal("error");
      } else {
        result.setSignal("success");
      }
    }
    result.setResponse(response);

    return result;
  }

  private boolean isJsonBody(Buffer bodyBuffer) {
    String body = bodyBuffer.toString().trim();
    if (body.charAt(0) == '{' && body.charAt(body.length() - 1) == '}') {
      return true;
    } else {
      return false;
    }
  }
}
