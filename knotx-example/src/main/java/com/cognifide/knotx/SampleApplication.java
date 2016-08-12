/*
 * Knot.x - Sample App with Mock service
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
package com.cognifide.knotx;

import com.cognifide.knotx.repository.RepositoryVerticle;
import com.cognifide.knotx.template.TemplateEngineVerticle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import javax.annotation.PostConstruct;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Application.class))
public class SampleApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleApplication.class);

    @Autowired
    private KnotxVerticle knotxVerticle;

    @Autowired
    private MockServiceConfiguration configuration;

    @Autowired
    private MockServiceVerticle mockServiceVerticle;

    @Autowired
    private RepositoryVerticle repositoryVerticle;

    @Autowired
    private TemplateEngineVerticle engineVerticle;

    @Autowired
    private MockRemoteRepositoryVerticle mockRemoteRepositoryVerticle;

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @PostConstruct
    public void deployVerticle() {
        Vertx vertx = Vertx.vertx();

        // Register codec for RepositoryResponse
        vertx.deployVerticle(repositoryVerticle);
        vertx.deployVerticle(engineVerticle);
        vertx.deployVerticle(mockServiceVerticle);
        vertx.deployVerticle(mockRemoteRepositoryVerticle);
        vertx.deployVerticle(knotxVerticle);

//        VertxOptions options = new VertxOptions();
//        Vertx.clusteredVertx(options,
//                res -> {
//                    if (res.succeeded()) {
//                        Vertx vertx = res.result();
//                        RxHelper.deployVerticle(vertx, repositoryVerticle);
//                        RxHelper.deployVerticle(vertx, mockServiceVerticle);
//                        RxHelper.deployVerticle(vertx, knotxVerticle);
//                        RxHelper.deployVerticle(vertx, mockRemoteRepositoryVerticle);
//                    } else {
//                        LOGGER.fatal("Failed to start vertx server: ", res.cause());
//                    }
//                }
//        );
    }

}
