/*
 * Knot.x - Reactive microservice assembler - Launcher
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
package com.cognifide.knotx.launcher;

import com.cognifide.knotx.codec.AdapterRequestCodec;
import com.cognifide.knotx.codec.AdapterResponseCodec;
import com.cognifide.knotx.codec.ClientRequestCodec;
import com.cognifide.knotx.codec.ClientResponseCodec;
import com.cognifide.knotx.codec.KnotContextCodec;
import com.cognifide.knotx.dataobjects.AdapterRequest;
import com.cognifide.knotx.dataobjects.AdapterResponse;
import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import org.apache.commons.lang3.tuple.Pair;
import rx.Observable;

public class KnotxStarterVerticle extends AbstractVerticle {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxStarterVerticle.class);
  public static final String CONFIG_OVERRIDE = "config";
  public static final String SERVICE_OPTIONS = "options";
  
  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    
    EventBus eventBus = (EventBus) this.vertx.eventBus().getDelegate();
    eventBus.registerDefaultCodec(AdapterRequest.class, new AdapterRequestCodec());
    eventBus.registerDefaultCodec(AdapterResponse.class, new AdapterResponseCodec());
    eventBus.registerDefaultCodec(ClientRequest.class, new ClientRequestCodec());
    eventBus.registerDefaultCodec(ClientResponse.class, new ClientResponseCodec());
    eventBus.registerDefaultCodec(KnotContext.class, new KnotContextCodec());
    
    vertx.registerVerticleFactory(new KnotxServiceVerticleFactory());
  }
  
  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Observable.from(config().getJsonArray("services"))
        .flatMap(this::deployVerticle)
        .compose(joinDeployments())
        .subscribe(
            message -> {
              LOGGER.info("Knot.x STARTED {}", message);
              startFuture.complete();
            },
            error -> {
              LOGGER.error("Verticle could not be deployed {}", error);
              startFuture.fail(error);
            }
        );
  }
  
  private Observable<Pair<String, String>> deployVerticle(final Object service) {
    return vertx.deployVerticleObservable((String) service, getServiceOptions((String) service))
        .map(deploymentID -> Pair.of((String) service, deploymentID));
  }
  
  private DeploymentOptions getServiceOptions(final String service) {
    DeploymentOptions deploymentOptions = new DeploymentOptions();
    if (config().containsKey(CONFIG_OVERRIDE) && config().getJsonObject(CONFIG_OVERRIDE).containsKey(service)) {
      JsonObject serviceConfig = config().getJsonObject(CONFIG_OVERRIDE).getJsonObject(service);
      if (serviceConfig.containsKey(SERVICE_OPTIONS)) {
        deploymentOptions.fromJson(serviceConfig.getJsonObject(SERVICE_OPTIONS));
      }
    }
    return deploymentOptions;
  }
  
  private Observable.Transformer<Pair<String, String>, String> joinDeployments() {
    return observable ->
        observable.reduce(new StringBuilder(System.lineSeparator()).append(System.lineSeparator()), this::collectDeployment)
            .map(StringBuilder::toString);
  }
  
  private StringBuilder collectDeployment(StringBuilder accumulator, Pair<String, String> deploymentId) {
    return accumulator
        .append(String.format("\t\tDeployed %s [%s]", deploymentId.getRight(), deploymentId.getLeft()))
        .append(System.lineSeparator());
  }
}
