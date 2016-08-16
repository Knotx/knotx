package com.cognifide.knotx.repository;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpClientResponse;

import java.net.URI;

/**
 * Created by wojtek on 12.08.16.
 */
public class HttpTemplate implements Template<String, URI>  {


	private int httpStatus;

	private MultiMap headers;

	private URI uri;

	public HttpTemplate(HttpClientResponse response) {
		this.uri = uri;
		this.httpStatus = httpStatus;
		this.headers = headers;

	}

	@Override
	public URI getId() {
		return uri;
	}

	@Override
	public String get() {
		return content;
	}

	@Override
	public void handle(HttpClientResponse  httpClientResponse, Action onSuccess, Action onFailure) {
		if (httpStatus != 200) {
			onSuccess.handle(this);
		} else {

		}

	}
}
