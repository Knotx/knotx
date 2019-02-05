/*
 * Copyright (C) 2018 Cognifide Limited
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
package io.knotx.server.api.context;

import io.knotx.server.api.context.RequestEventLog.Entry;
import io.vertx.core.json.JsonObject;
import java.util.Optional;

public class RequestContext {

  public static final String KEY = "requestContext";

  private RequestEvent requestEvent;
  private final RequestEventLog log;

  public RequestContext(RequestEvent requestEvent) {
    this.requestEvent = requestEvent;
    log = new RequestEventLog();
  }

  public RequestContext(JsonObject json) {
    this.requestEvent = new RequestEvent(json.getJsonObject("requestEvent"));
    this.log = new RequestEventLog(json.getJsonObject("log"));
  }

  public RequestEvent getRequestEvent() {
    return requestEvent;
  }

  public Status status() {
    final Optional<Entry> failedOperation = log.getOperations().stream()
        .filter(e -> RequestEventLog.Status.SUCCESS != e.getStatus())
        .findAny();
    return failedOperation
        .map(Status::failed)
        .orElse(Status.ok());
  }

  public void success(String handlerId, RequestEvent requestEvent) {
    this.requestEvent = requestEvent;
    log.append(handlerId, RequestEventLog.Status.SUCCESS);
  }

  public void failure(String handlerId, ClientResponse clientResponse) {
    log.append(handlerId, RequestEventLog.Status.FAILURE, clientResponse);
  }

  public void fatal(String handlerId) {
    log.append(handlerId, RequestEventLog.Status.FATAL);
  }

  public JsonObject toJson() {
    return new JsonObject()
        .put("requestEvent", requestEvent.toJson())
        .put("log", log.toJson());
  }

  public static class Status {

    private final boolean failed;
    private Entry cause;

    private Status(boolean failed) {
      this.failed = failed;
    }

    static Status ok() {
      return new Status(false);
    }

    static Status failed(Entry cause) {
      final Status status = new Status(true);
      status.cause = cause;
      return status;
    }

    public boolean isFailed() {
      return failed;
    }

    public Optional<JsonObject> getCause() {
      return Optional.ofNullable(cause).map(Entry::toJson);
    }
  }

}
