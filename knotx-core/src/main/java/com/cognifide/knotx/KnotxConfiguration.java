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

import com.cognifide.knotx.template.handlebars.Helpers;
import com.github.jknack.handlebars.Handlebars;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class KnotxConfiguration {

    @Value("#{'${request.preserved.headers}'.split(',')}")
    private List<String> serviceCallHeaders;

    @Value("${requestHandler.port}")
    private Integer requestHandlerPort;

    Integer requestHandlerPort() {
        return requestHandlerPort;
    }

    List<String> serviceCallHeaders() {
        return serviceCallHeaders;
    }

    @Bean
    public Handlebars getHandlebars() {
        Handlebars handlebars = new Handlebars();
        Helpers.register(handlebars);
        return handlebars;
    }
}
