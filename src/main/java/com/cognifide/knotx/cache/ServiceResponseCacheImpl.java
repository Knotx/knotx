/*
 * Knot.x - reactive microservice assembler
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
package com.cognifide.knotx.cache;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ServiceResponseCacheImpl implements ServiceResponseCache {

    private final ConcurrentMap<String, Map<String, Object>> map = new ConcurrentHashMap<>();

    @Override
    public Optional<Map<String, Object>> get(String dataCallUri) {
        return Optional.ofNullable(map.get(dataCallUri));
    }

    @Override
    public void put(String dataCallUri, Map<String, Object> serviceData) {
        map.put(dataCallUri, serviceData);
    }
}
