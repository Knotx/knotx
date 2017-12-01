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
package io.knotx.http;

import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.reactivex.core.MultiMap;
import java.util.List;
import java.util.Map;

public final class UriHelper {

  private UriHelper() {
    // util constructor
  }

  public static MultiMap getParams(String uri) {
    QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
    Map<String, List<String>> queryParams = queryStringDecoder.parameters();

    io.vertx.core.MultiMap params = io.vertx.core.MultiMap.caseInsensitiveMultiMap();
    if (!queryParams.isEmpty()) {
      for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
        params.add(entry.getKey(), entry.getValue());
      }

    }
    return MultiMap.newInstance(params);
  }
}
