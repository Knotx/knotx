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
package com.cognifide.knotx.dataobjects;

import com.google.common.base.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.buffer.Buffer;

public class AdapterResponse extends Codec {
  private ClientResponse response;

  private String signal;

  public AdapterResponse() {
    //Empty Reponse object
  }

  public ClientResponse response() {
    return response;
  }

  public AdapterResponse setResponse(ClientResponse response) {
    this.response = response;
    return this;
  }

  public String signal() {
    return signal;
  }

  public AdapterResponse setSignal(String signal) {
    this.signal = signal;

    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AdapterResponse)) return false;
    AdapterResponse that = (AdapterResponse) o;
    return Objects.equal(signal, that.signal()) &&
        response.equals(that.response());
  }

  @Override
  public int hashCode() {
    return 31 * response.hashCode() + signal.hashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("response", response)
        .append("signal", signal)
        .toString();
  }

  @Override
  public void encodeToWire(Buffer buffer) {
    encodeString(buffer, signal);
    if (response != null) {
      encodeInt(buffer, 1);
      response.encodeToWire(buffer);
    } else {
      encodeInt(buffer, 0);
    }
  }

  @Override
  public void decodeFromWire(AtomicInteger pos, Buffer buffer) {
    signal = decodeString(pos, buffer);
    int exists = decodeInt(pos, buffer);
    if (exists == 1) {
      response = new ClientResponse();
      response.decodeFromWire(pos, buffer);
    }
  }
}
