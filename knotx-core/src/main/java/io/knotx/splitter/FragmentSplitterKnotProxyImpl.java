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
package io.knotx.splitter;

import io.knotx.dataobjects.KnotContext;
import io.knotx.knot.AbstractKnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.NoSuchElementException;
import java.util.Set;

public class FragmentSplitterKnotProxyImpl extends AbstractKnotProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(FragmentSplitterKnotProxyImpl.class);

  private final FragmentSplitter splitter;

  public FragmentSplitterKnotProxyImpl(FragmentSplitterOptions options) {
    splitter = new HtmlFragmentSplitter(options.getSnippetTagName());
  }

  @Override
  protected Single<KnotContext> processRequest(KnotContext knotContext) {
    try {
      knotContext
          .setFragments(splitter.split(knotContext.getClientResponse().getBody().toString()));
      knotContext.getClientResponse().setStatusCode(HttpResponseStatus.OK.code()).clearBody();

      return Single.just(knotContext);
    } catch (Exception ex) {
      LOGGER.error("Exception happened during HTML splitting.", ex);
      return Single.just(processError(knotContext, ex));
    }
  }

  @Override
  protected boolean shouldProcess(Set<String> knots) {
    return true;
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    HttpResponseStatus statusCode;
    if (error instanceof NoSuchElementException) {
      statusCode = HttpResponseStatus.NOT_FOUND;
    } else {
      statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    knotContext.getClientResponse().setStatusCode(statusCode.code());
    return knotContext;
  }

}
