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
import io.vertx.core.json.JsonObject;

public class AdapterRequest extends Codec {
  private ClientRequest request;

  private JsonObject params;

  public AdapterRequest() {
    //Empty object
  }

  public ClientRequest request() {
    return request;
  }

  public AdapterRequest setRequest(ClientRequest request) {
    this.request = request;
    return this;
  }

  public JsonObject params() {
    return params;
  }

  public AdapterRequest setParams(JsonObject params) {
    this.params = params;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AdapterRequest)) return false;
    AdapterRequest that = (AdapterRequest) o;
    return Objects.equal(params, that.params()) &&
        request.equals(that.request());
  }

  @Override
  public int hashCode() {
    return 31 * request.hashCode() + params.hashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("request", request)
        .append("params", params)
        .toString();
  }

  @Override
  public void encodeToWire(Buffer buffer) {
    if (request != null) {
      encodeInt(buffer, 1);
      request.encodeToWire(buffer);
    } else {
      encodeInt(buffer, 0);
    }
    if (params != null) {
      encodeInt(buffer, 1);
      encodeString(buffer, params.encode());
    } else {
      encodeInt(buffer, 0);
    }
  }

  @Override
  public void decodeFromWire(AtomicInteger pos, Buffer buffer) {
    int exists = decodeInt(pos, buffer);
    if (exists == 1) {
      request = new ClientRequest();
      request.decodeFromWire(pos, buffer);
    }
    exists = decodeInt(pos, buffer);
    if (exists == 1) {
      params = new JsonObject(decodeString(pos, buffer));
    }
  }
}
