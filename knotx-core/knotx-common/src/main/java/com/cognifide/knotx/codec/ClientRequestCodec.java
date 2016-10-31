/*
 * Knot.x - Reactive microservice assembler - API
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
package com.cognifide.knotx.codec;

import com.cognifide.knotx.dataobjects.ClientRequest;

import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.eventbus.MessageCodec;

public class ClientRequestCodec implements MessageCodec<ClientRequest, ClientRequest> {

  @Override
  public void encodeToWire(io.vertx.core.buffer.Buffer buffer, ClientRequest request) {
    request.encodeToWire(buffer);
  }

  @Override
  public ClientRequest decodeFromWire(int pos, io.vertx.core.buffer.Buffer buffer) {
    ClientRequest request = new ClientRequest();
    request.decodeFromWire(new AtomicInteger(pos), buffer);

    return request;
  }


  @Override
  public ClientRequest transform(ClientRequest request) {
    return request;
  }

  @Override
  public String name() {
    return this.getClass().getSimpleName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }


}
