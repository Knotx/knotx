/*
 * Knot.x - Reactive microservice assembler - Repository Verticle
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
package com.cognifide.knotx.repository.util;

import com.cognifide.knotx.repository.api.RepositoryResponse;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class RepositoryResponseCodec implements MessageCodec<RepositoryResponse, RepositoryResponse> {

    private static final int CONTENT_START_IDX = 4;
    private static final byte SUCCESS_FLAG = 1;
    private static final byte ERROR_FLAG = 0;

    @Override
    public void encodeToWire(Buffer buffer, RepositoryResponse object) {
        Buffer data = Buffer.buffer();
        data.appendByte((object.isSuccess() ? SUCCESS_FLAG : ERROR_FLAG)); //Success or error
        if (object.isSuccess()) {
            data.appendBuffer((Buffer) object.getData().getDelegate());
        } else {
            data.appendString(object.getReason());
        }
        buffer.appendInt(data.length());
        buffer.appendBuffer(data);
    }

    @Override
    public RepositoryResponse decodeFromWire(int pos, Buffer buffer) {
        RepositoryResponse object;

        int length = buffer.getInt(pos);
        pos += CONTENT_START_IDX;
        if (buffer.getByte(pos++) == SUCCESS_FLAG) {
            object = RepositoryResponse.success(io.vertx.rxjava.core.buffer.Buffer.newInstance(buffer.getBuffer(pos, pos + length)));
        } else {
            object = RepositoryResponse.error(buffer.getString(pos, pos + length));
        }
        return object;
    }

    @Override
    public RepositoryResponse transform(RepositoryResponse message) {
        return message;
    }

    @Override
    public String name() {
        return RepositoryResponse.class.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
