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
package com.cognifide.knotx.util;

import com.google.common.base.Charsets;
import com.google.gson.Gson;

import com.cognifide.knotx.api.RepositoryRequest;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class RepositoryRequestCodec implements MessageCodec<RepositoryRequest, RepositoryRequest> {

    @Override
    public void encodeToWire(Buffer buffer, RepositoryRequest object) {
        String requestAsJson = new Gson().toJson(object);
        buffer.appendString(requestAsJson, Charsets.UTF_8.name());
    }

    @Override
    public RepositoryRequest decodeFromWire(int pos, Buffer buffer) {
        JsonObject jsonObject = buffer.toJsonObject();
        String path = jsonObject.getString("path");

        // TODO extract headers
        return new RepositoryRequest(path, null);
    }

    @Override
    public RepositoryRequest transform(RepositoryRequest message) {
        return message;
    }

    @Override
    public String name() {
        return RepositoryRequest.class.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
