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
package com.cognifide.knotx.junit;

import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.buffer.Buffer;

public class KnotContextFactory {

  public static KnotContext empty(List<Fragment> fragments) {
    return empty("").setFragments(fragments);
  }

  public static KnotContext empty() {
    return empty("");
  }

  public static KnotContext empty(String template) {
    return new KnotContext()
        .setClientResponse(
            new ClientResponse()
                .setBody(StringUtils.isEmpty(template) ? Buffer.buffer() : Buffer.buffer(template))
                .setStatusCode(HttpResponseStatus.OK)
                .setHeaders(MultiMap.caseInsensitiveMultiMap()))
        .setClientRequest(new ClientRequest());
  }

}
