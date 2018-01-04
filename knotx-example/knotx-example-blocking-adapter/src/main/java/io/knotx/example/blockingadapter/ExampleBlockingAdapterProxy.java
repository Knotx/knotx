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
package io.knotx.example.blockingadapter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

import io.knotx.adapter.BlockingAdapterProxy;
import io.knotx.adapter.common.placeholders.UriTransformer;
import io.knotx.dataobjects.AdapterRequest;
import io.knotx.dataobjects.ClientResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.IOException;

public class ExampleBlockingAdapterProxy extends BlockingAdapterProxy<HttpResponse> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleBlockingAdapterProxy.class);

  private static final String PATH_PROPERTY_KEY = "path";

  private String endpoint;

  ExampleBlockingAdapterProxy(Vertx vertx, String protocol, String domain, Integer port) {
    super(vertx);
    this.endpoint = protocol + "://" + domain + ":" + port;
  }

  @Override
  protected HttpResponse processBlocking(AdapterRequest request) {
    String uri = endpoint + UriTransformer
        .resolveServicePath(request.getParams().getString(PATH_PROPERTY_KEY), request.getRequest());
    try {
      return Request.Get(uri).execute().returnResponse();
    } catch (IOException e) {
      LOGGER.error("Could not connect to endpoint [{}]", uri, e);
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  protected ClientResponse createClientResponse(AsyncResult<HttpResponse> res) {
    if (res.succeeded()) {
      try {
        return new ClientResponse().setStatusCode(res.result().getStatusLine().getStatusCode())
            .setBody(Buffer.buffer(getResponseBody(res)));
      } catch (IOException e) {
        LOGGER.error("Could not parse response", e);
        return new ClientResponse().setStatusCode(500).setBody(Buffer.buffer("{}"));
      }
    } else {
      LOGGER.warn("Http invocation failed!");
      return new ClientResponse().setStatusCode(500).setBody(Buffer.buffer("{}"));
    }
  }

  private String getResponseBody(AsyncResult<HttpResponse> res) throws IOException {
    return IOUtils.toString(res.result().getEntity().getContent(), "UTF-8");
  }
}
