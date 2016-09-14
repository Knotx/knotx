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
package com.cognifide.knotx.api;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import rx.Observable;

public abstract class RepositoryResponse extends JsonObjectRequest {

  protected String data;

  protected int statusCode;

  protected MultiMap headers;

  public static RepositoryResponse fromJson(JsonObject object) {
    RepositoryResponse repositoryResponse;

    MultiMap headers = fromJsonArray(object.getJsonArray("headers"));
    Boolean shouldProcess = object.getBoolean("shouldProcess");
    String data = object.getString("data");

    if (shouldProcess) {
      repositoryResponse = success(data, headers);
    } else {
      int statusCode = object.getInteger("statusCode");
      repositoryResponse = error(statusCode, data, headers);
    }
    return repositoryResponse;
  }

  public static RepositoryResponse success(String data, MultiMap headers) {
    return new SuccessRepositoryResponse(data, headers);
  }

  public static RepositoryResponse error(int statusCode, String data, MultiMap headers) {
    return new ErrorRepositoryResponse(statusCode, data, headers);
  }

  public abstract boolean shouldProcess();

  public Observable<RepositoryResponse> toObservable() {
    return Observable.just(this);
  }

  public String getData() {
    return data;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public MultiMap getHeaders() {
    return headers;
  }

  @Override
  public JsonObject toJsonObject() {
    JsonObject object = new JsonObject();
    object.put("shouldProcess", shouldProcess());
    object.put("headers", toJsonArray(headers));
    object.put("statusCode", statusCode);
    object.put("data", data);
    return object;
  }

  @Override
  public String toString() {
    return "RepositoryResponse{" + "shouldProcess=" + shouldProcess() + ", data='" + data + '\''
        + ", statusCode=" + statusCode + ", headers=" + headers + '}';
  }

}
