/*
 * Knot.x - Reactive microservice assembler
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
package com.cognifide.knotx.template;

import com.cognifide.knotx.api.KnotxConst;
import com.cognifide.knotx.api.TemplateEngineRequest;
import com.cognifide.knotx.template.engine.TemplateEngine;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;

public class TemplateEngineVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngineVerticle.class);

    private TemplateEngine templateEngine;

    private TemplateEngineConfiguration configuration;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        configuration = new TemplateEngineConfiguration(config().getJsonObject("config"));
        templateEngine = new TemplateEngine(this.vertx, configuration);
    }

    @Override
    public void start() throws Exception {
        LOGGER.debug("Registered <{}>", this.getClass().getSimpleName());

        EventBus eventBus = vertx.eventBus();

        Observable<Message<TemplateEngineRequest>> messageObservable = eventBus.<TemplateEngineRequest>consumer(KnotxConst.TEMPLATE_ENGINE_ADDRESS).toObservable();

        messageObservable
            .doOnNext(this::traceMessage)
            .subscribe(
                msg -> {
                    templateEngine.process(msg.body())
                        .subscribe(
                            data -> msg.reply(data),
                            error -> {
                                LOGGER.error("Error happened", error);
                                msg.reply(Buffer.buffer("ERROR").getDelegate());
                            }
                        );
                    LOGGER.trace("Got message: {}", msg.body());
                }
            );
    }

    private void traceMessage(Message<?> message) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body());
        }
    }
}
