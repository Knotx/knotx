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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import io.vertx.core.Vertx;

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Application.class))
public class SampleApplication {

	@Autowired
	private KnotxVerticle knotxVerticle;

	@Autowired
	private MockServiceConfiguration configuration;

	@Autowired
	private MockServiceVerticle mockServiceVerticle;

	@Autowired
	private MockRemoteRepositoryVerticle mockRemoteRepositoryVerticle;

	public static void main(String[] args) {
		SpringApplication.run(SampleApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle() {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(mockServiceVerticle);
		vertx.deployVerticle(knotxVerticle);
		vertx.deployVerticle(mockRemoteRepositoryVerticle);
	}

}
