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
package io.knotx.proxy.reactive;

import io.knotx.reactivex.proxy.KnotProxy;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.reactivex.core.Vertx;
import java.util.Optional;

public final class KnotProxyFactory {

  private KnotProxyFactory() {
    // hidden
  }

  public static KnotProxy createProxy(Vertx vertx, Optional<DeliveryOptions> deliveryOptions,
      String address) {
    return deliveryOptions
        .map(options -> io.knotx.reactivex.proxy.KnotProxy.createProxyWithOptions(vertx, address, options))
        .orElse(KnotProxy.createProxy(vertx, address));
  }

}
