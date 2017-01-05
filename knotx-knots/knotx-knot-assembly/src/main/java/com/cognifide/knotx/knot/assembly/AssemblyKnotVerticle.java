/*
 * Knot.x - Reactive microservice assembler - Assembly Knot
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
package com.cognifide.knotx.knot.assembly;

import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import com.cognifide.knotx.knot.api.AbstractKnot;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rx.Observable;

public class AssemblyKnotVerticle extends AbstractKnot<AssemblyKnotConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssemblyKnotVerticle.class);

  private static final String SNIPPET_IDENTIFIER_NAME = "data-knot-types";

  private static final String ANY_SNIPPET_PATTERN = "(?is).*<script\\s+" + SNIPPET_IDENTIFIER_NAME + ".*";

  @Override
  protected Observable<KnotContext> process(KnotContext message) {
    return Observable.create(observer -> {
      try {
        String templateContent = message.fragments().map(fragments -> fragments.stream()
            .map(Fragment::getContent)
            .filter(StringUtils::isNotBlank)
            .map(value -> {
              if (value.matches(ANY_SNIPPET_PATTERN)) {
                Document document = Jsoup.parseBodyFragment(value);
                Element scriptTag = document.body().child(0);
                return scriptTag.unwrap().toString();
              } else {
                return value;
              }
            })
            .collect(Collectors.joining())).orElse(StringUtils.EMPTY);
        observer.onNext(createSuccessResponse(message, templateContent));
        observer.onCompleted();
      } catch (Exception e) {
        observer.onError(e);
      }
    });
  }

  @Override
  protected boolean shouldProcess(Set<String> fragmentsIdentifiers) {
    // this is default Knot which assembles all fragment to final body markup
    return true;
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    LOGGER.error("Error happened during Template processing", error);
    ClientResponse errorResponse = new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);

    return new KnotContext()
        .setClientRequest(knotContext.clientRequest())
        .setClientResponse(errorResponse);
  }

  @Override
  protected AssemblyKnotConfiguration initConfiguration(JsonObject config) {
    return new AssemblyKnotConfiguration(config);
  }

  private KnotContext createSuccessResponse(KnotContext inputContext, String renderedContent) {
    ClientResponse clientResponse = inputContext.clientResponse();
    MultiMap headers = clientResponse.headers();
    headers.set(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(renderedContent.length()));
    clientResponse.setBody(Buffer.buffer(renderedContent)).setHeaders(headers);

    return new KnotContext()
        .setClientRequest(inputContext.clientRequest())
        .setClientResponse(clientResponse);
  }

}
