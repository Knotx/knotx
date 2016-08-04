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
package com.cognifide.knotx;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.google.common.collect.Lists;

@Configuration
public class KnotxConfiguration {
	@Autowired
	private Environment environment;

	Integer requestHandlerPort() {
		return environment.getProperty("requestHandler.port", Integer.class);
	}

	List<String> serviceCallHeaders() {
		String headers = environment.getProperty("request.preserved.headers", String.class);
		return Lists.newArrayList(headers.split(","));
	}

}
