/*
 * Knot.x - Reactive microservice assembler - Common
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
package com.cognifide.knotx.common;

import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.circuitbreaker.CircuitBreaker;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.servicediscovery.ServiceDiscovery;
import io.vertx.rxjava.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This verticle provides support for various microservice functionality
 * like service discovery, circuit breaker and simple log publisher
 */
public class BaseMicroserviceVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseMicroserviceVerticle.class);

  private static final String LOG_EVENT_ADDRESS = "events.log";

  protected ServiceDiscovery discovery;
  protected CircuitBreaker circuitBreaker;
  protected Set<Record> registeredRecords = new ConcurrentHashSet<>();

  @Override
  public void start() throws Exception {
    LOGGER.debug("Starting <{}>", this.getClass().getName());
    // init service discovery instance
    discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));

    // init circuit breaker instance
    JsonObject circuitBreakerOptions = config().getJsonObject("circuit-breaker") != null ?
        config().getJsonObject("circuit-breaker") : new JsonObject();

    circuitBreaker = CircuitBreaker.create(circuitBreakerOptions.getString("name", "circuit-breaker"), vertx,
        new CircuitBreakerOptions()
            .setMaxFailures(circuitBreakerOptions.getInteger("max-failures", 5))
            .setTimeout(circuitBreakerOptions.getLong("timeout", 10000L))
            .setFallbackOnFailure(true)
            .setResetTimeout(circuitBreakerOptions.getLong("reset-timeout", 30000L))
    );
  }

  @Override
  public void stop(Future<Void> future) throws Exception {
    // In current design, the publisher is responsible for removing the service
    List<Future> futures = new ArrayList<>();

    registeredRecords.stream()
        .forEach(
            record -> {
              Future<Void> unregistrationFuture = Future.future();
              futures.add(unregistrationFuture);
              discovery.unpublish(record.getRegistration(), unregistrationFuture.completer());
            });

    if (futures.isEmpty()) {
      discovery.close();
      future.complete();
    } else {
      CompositeFuture.all(futures)
          .setHandler(ar -> {
            discovery.close();
            if (ar.failed()) {
              future.fail(ar.cause());
            } else {
              future.complete();
            }
          });
    }
  }

  protected Future<Void> publishEventBusService(String name, String address, String serviceInterface, JsonObject serviceMetadata) {
    Record record = EventBusService.createRecord(name, address, serviceInterface, serviceMetadata);
    return publish(record);
  }

  /**
   * Publish a service with record.
   *
   * @param record service record
   * @return async result
   */
  private Future<Void> publish(Record record) {
    if (discovery == null) {
      try {
        start();
      } catch (Exception e) {
        throw new IllegalStateException("Cannot create discovery service");
      }
    }

    Future<Void> future = Future.future();
    // publish the service
    discovery.publish(record, ar -> {
      if (ar.succeeded()) {
        registeredRecords.add(record);
        LOGGER.info("Service <" + ar.result().getName() + "> published");
        future.complete();
      } else {
        future.fail(ar.cause());
      }
    });

    return future;
  }

  /**
   * A helper method that simply publish logs on the event bus.
   *
   * @param type log type
   * @param data log message data
   */
  protected void publishLogEvent(String type, JsonObject data) {
    JsonObject msg = new JsonObject().put("type", type)
        .put("message", data);
    vertx.eventBus().publish(LOG_EVENT_ADDRESS, msg);
  }

  protected void publishLogEvent(String type, JsonObject data, boolean succeeded) {
    JsonObject msg = new JsonObject().put("type", type)
        .put("status", succeeded)
        .put("message", data);
    vertx.eventBus().publish(LOG_EVENT_ADDRESS, msg);
  }
}
