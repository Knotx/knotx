/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
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
package com.cognifide.knotx.engine.util;

import com.google.gson.Gson;

import com.cognifide.knotx.engine.api.TemplateEngineRequest;

import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.rxjava.core.MultiMap;

public class TemplateEngineRequestCodec implements MessageCodec<TemplateEngineRequest, TemplateEngineRequest> {

    private static final int CONTENT_START_IDX = 4;
    private static final byte SUCCESS_FLAG = 1;
    private static final byte ERROR_FLAG = 0;

    @Override
    public void encodeToWire(Buffer buffer, TemplateEngineRequest object) {
        buffer.appendInt(object.getTemplate().length());
        buffer.appendBuffer((Buffer) object.getTemplate().getDelegate());

        byte[] encoded = new Gson().toJson(object.getHeaders()).getBytes(CharsetUtil.UTF_8);
        buffer.appendInt(encoded.length);
        Buffer buff = Buffer.buffer(encoded);
        buffer.appendBuffer(buff);
    }

    @Override
    public TemplateEngineRequest decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        pos += 4;
        TemplateEngineRequest request = new TemplateEngineRequest(new io.vertx.rxjava.core.buffer.Buffer(buffer.getBuffer(pos, pos + length)));

        pos += length;
        length = buffer.getInt(pos);
        pos += 4;
        byte[] encoded = buffer.getBytes(pos, pos + length);
        String str = new String(encoded, CharsetUtil.UTF_8);
        request.setHeaders(new Gson().fromJson(str, MultiMap.class));

        return request;
    }

    @Override
    public TemplateEngineRequest transform(TemplateEngineRequest message) {
        return message;
    }

    @Override
    public String name() {
        return TemplateEngineRequest.class.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
