package com.cognifide.knotx.handler;


import com.cognifide.knotx.event.ObservableRequest;
import com.cognifide.knotx.template.TemplateHandler;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerRequest;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class RestServiceResponseHandler implements Handler<HttpClientResponse> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceResponseHandler.class);

	private final HttpServerRequest request;

	private final Template template;

	private final TemplateHandler templateHandler;

	private final String dataCallUri;

	private final ObservableRequest observableRequest;

	private final Element snippet;

	private final boolean templateDebug;

	public RestServiceResponseHandler(HttpServerRequest request, Template template, TemplateHandler templateHandler, String dataCallUri,
	                                  ObservableRequest observableRequest, Element snippet, boolean templateDebug) {
		this.request = request;
		this.template = template;
		this.templateHandler = templateHandler;
		this.dataCallUri = dataCallUri;
		this.observableRequest = observableRequest;
		this.snippet = snippet;
		this.templateDebug = templateDebug;
	}

	@Override
	public void handle(HttpClientResponse response) {
		response.bodyHandler(buffer -> {
			String responseContent = buffer.getString(0, buffer.length());
			LOGGER.debug("Request in: " + request.absoluteURI() + " for " + dataCallUri);
			try {
				String compiledContent = template.apply(new Gson().fromJson(responseContent, Map.class));
				Element snippetParent = new Element(Tag.valueOf("div"), "");
				if (templateDebug) {
					String debugComment = "<!-- webservice `" + dataCallUri + "` call -->";
					snippetParent.prepend(debugComment);
				}
				snippet.replaceWith(snippetParent.append(compiledContent));
			} catch (IOException e) {
				LOGGER.error("Can't apply response to template!", e);
			} finally {
				observableRequest.onFinish();
				templateHandler.finishIfLast(request);
			}
		});
	}
}
