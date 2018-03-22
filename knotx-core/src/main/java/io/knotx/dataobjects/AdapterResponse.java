/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.dataobjects;

import com.google.common.base.Objects;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

@DataObject(generateConverter = true)
public class AdapterResponse {

  private ClientResponse response;

  private String signal;

  public AdapterResponse() {
    //Empty Reponse object
  }


  public AdapterResponse(JsonObject json) {
    AdapterResponseConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    AdapterResponseConverter.toJson(this, json);
    return json;
  }

  public ClientResponse getResponse() {
    return response;
  }

  public AdapterResponse setResponse(ClientResponse response) {
    this.response = response;
    return this;
  }

  public String getSignal() {
    return signal;
  }

  public AdapterResponse setSignal(String signal) {
    this.signal = signal;

    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AdapterResponse)) {
      return false;
    }
    AdapterResponse that = (AdapterResponse) o;
    return Objects.equal(signal, that.signal) &&
        response.equals(that.response);
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
}
