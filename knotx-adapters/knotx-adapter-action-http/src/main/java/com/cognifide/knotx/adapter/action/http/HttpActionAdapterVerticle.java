/*
 * Knot.x - Reactive microservice assembler - HTTP Action Adapter
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

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import rx.Observable;

public class HttpActionAdapterVerticle extends AbstractAdapter<HttpAdapterConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpActionAdapterVerticle.class);

  @Override
  protected HttpAdapterConfiguration initConfiguration(JsonObject config) {
    return new HttpAdapterConfiguration(config());
  }

  @Override
  protected Observable<JsonObject> processMessage(JsonObject message) {
    return null;
  }

}
