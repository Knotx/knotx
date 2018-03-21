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
package io.knotx.assembler;

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.knotx.fragments.SnippetPatterns;
import io.knotx.knot.AbstractKnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.MultiMap;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class FragmentAssemblerKnotProxyImpl extends AbstractKnotProxy {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(FragmentAssemblerKnotProxyImpl.class);
  private final SnippetPatterns patterns;

  private FragmentAssemblerOptions options;

  public FragmentAssemblerKnotProxyImpl(FragmentAssemblerOptions options) {
    this.options = options;
    this.patterns = new SnippetPatterns(options.getSnippetTagName());
  }

  @Override
  protected Single<KnotContext> processRequest(KnotContext knotContext) {
    if (hasFragments(knotContext)) {
      try {
        String joinedFragments = knotContext.getFragments().stream()
            .map(fragment -> options.getUnprocessedStrategy().get(fragment, patterns))
            .collect(Collectors.joining());

        return Single.just(createSuccessResponse(knotContext, joinedFragments));
      } catch (Exception ex) {
        LOGGER.error("Exception happened during Fragment assembly.", ex);
        return Single.just(processError(knotContext, ex));
      }
    } else {
      LOGGER.error("Fragments are empty or not exists in KnotContext.");
      return Single.just(processError(knotContext, null));
    }
  }

  private boolean hasFragments(KnotContext knotContext) {
    return knotContext.getFragments() != null && !knotContext.getFragments().isEmpty();
  }

  @Override
  protected boolean shouldProcess(Set<String> knots) {
    return true;
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    ClientResponse errorResponse = new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());

    return new KnotContext()
        .setClientRequest(knotContext.getClientRequest())
        .setClientResponse(errorResponse);
  }

  private KnotContext createSuccessResponse(KnotContext inputContext, String renderedContent) {
    ClientResponse clientResponse = inputContext.getClientResponse();
    if (StringUtils.isBlank(renderedContent)) {
      clientResponse.setStatusCode(HttpResponseStatus.NO_CONTENT.code());
    } else {
      MultiMap headers = clientResponse.getHeaders();
      headers.add(HttpHeaders.CONTENT_LENGTH.toString().toLowerCase(),
          Integer.toString(renderedContent.length()));

      clientResponse.setBody(Buffer.buffer(renderedContent)).setHeaders(headers);
      clientResponse.setStatusCode(HttpResponseStatus.OK.code());
    }

    return new KnotContext()
        .setClientRequest(inputContext.getClientRequest())
        .setClientResponse(clientResponse);
  }
}
