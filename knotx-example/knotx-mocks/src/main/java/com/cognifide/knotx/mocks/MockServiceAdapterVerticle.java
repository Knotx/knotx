/*
 * Knot.x - Mocked services for sample app
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
package com.cognifide.knotx.mocks;


import com.cognifide.knotx.mocks.adapter.MockAdapterHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Mock Service Adapter for testing purposes. It simulates real service adapters on event bus.
 * Data exchange contract:<br/>
 * <ul>
 *   <li>Input data Json Format
 *      <pre>
 *        {
 *          "clientRequest" : {..},
 *          "params" : {..}
 *        }
 *      </pre>
 *      When <strong>clientRequest</strong> is the JSON representation of ClientRequest<br/>
 *      And <strong>params</strong> is any JSON Object - currently not interpretted by mock<br/>
 *   </li>
 *   <li>Output data Json Formt - is JSON representation of ClientResponse.<br/>
 *   <strong>body</strong> field of the wrapper is suppose to carry on the actual response from the mocked service (content of the mock file)<br/>
 *   In order to interpret response in the verticle talking with mock, you can use following approach:<br/>
 *   <pre>
 *    new JsobObject(responseWrapper.getBody());
 *   </pre>
 *   </li>
 * </ul>
 */
public class MockServiceAdapterVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockServiceAdapterVerticle.class);

  @Override
  public void start() {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    vertx.eventBus().consumer(config().getString("address"), createHandler());
  }

  private MockAdapterHandler createHandler() {
    return new MockAdapterHandler(config().getString("mockDataRoot"), vertx.fileSystem());
  }
}
