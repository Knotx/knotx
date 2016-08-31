/*
 * Knot.x - Reactive microservice assembler - API
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.cognifide.knotx.api;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import rx.Observable;

public abstract class RepositoryResponse extends JsonObjectRequest {

	private static final long serialVersionUID = -5892594714118271978L;

	protected String reason;

	protected String data;

	protected int statusCode;

	protected MultiMap headers;

	protected boolean success;

	public static RepositoryResponse fromJson(JsonObject object) {
		RepositoryResponse repositoryResponse;
		Boolean success = object.getBoolean("success");
		Boolean shouldProcess = object.getBoolean("shouldProcess");
		MultiMap  headers = fromJsonArray(object.getJsonArray("headers"));
		String  data = object.getString("data");

		if (success && shouldProcess) {
			repositoryResponse = success(data, headers);
		} else if (success) {
			repositoryResponse = empty(headers);
		} else {
			int statusCode = object.getInteger("statusCode");
			String reason = object.getString("reason");
			repositoryResponse = error(statusCode, reason, headers);
		}
		return repositoryResponse;
	}

	public static RepositoryResponse success(String data, MultiMap headers) {
		return new SuccessRepositoryResponse(data, headers);
	}

	public static RepositoryResponse empty(MultiMap headers) {
		return new EmptyRepositoryResponse(headers);
	}

	public static RepositoryResponse error(int statusCode, String reason, MultiMap headers) {
		return new ErrorRepositoryResponse(statusCode, reason, headers);
	}

	public abstract boolean shouldProcess();

	public Observable<RepositoryResponse> toObservable() {
		return Observable.just(this);
	}

	public boolean isSuccess() {
		return success;
	}

	public String getData() {
		return data;
	}

	public String getReason() {
		return reason;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public MultiMap getHeaders() {
		return headers;
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject object = new JsonObject().put("success", success);
		object.put("shouldProcess", shouldProcess());
		object.put("headers", toJsonArray(headers));
		if (success && shouldProcess()) {
			object.put("data", data);
		} else {
			object.put("reason", reason);
			object.put("statusCode", statusCode);
		}
		return object;
	}

	@Override
	public String toString() {
		return "RepositoryResponse{" +"shouldProcess="+shouldProcess()+ ", success=" + success + ", reason='" + reason + '\'' + ", data='" + data
				+ '\'' + ", statusCode=" + statusCode + ", headers=" + headers + '}';
	}

}
