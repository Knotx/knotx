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
package io.knotx.adapter.common.post;

import io.netty.handler.codec.http.HttpConstants;
import io.vertx.reactivex.core.MultiMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;

public final class UrlEncodedBodyBuilder {

  private UrlEncodedBodyBuilder() {
    //hidden util constructor
  }

  public static String encodeBody(final MultiMap formAttributes) {
    return encodeBody(formAttributes, HttpConstants.DEFAULT_CHARSET);
  }

  public static String encodeBody(final MultiMap formAttributes, Charset charset) {
    if (formAttributes == null || formAttributes.isEmpty()) {
      return StringUtils.EMPTY;
    } else {
      Iterator<Entry<String, String>> entryIt = formAttributes
          .getDelegate()
          .iterator();

      StringBuilder sb = new StringBuilder(128);
      while (entryIt.hasNext()) {
        Entry<String, String> entry = entryIt.next();
        sb.append(encodeComponent(entry.getKey(), charset));
        if (entry.getValue() != null) {
          sb.append('=');
          sb.append(encodeComponent(entry.getValue(), charset));
        }
        if (entryIt.hasNext()) {
          sb.append('&');
        }
      }
      return sb.toString();
    }
  }

  private static String encodeComponent(String s, Charset charset) {
    try {
      return URLEncoder.encode(s, charset.name()).replace("+", "%20");
    } catch (UnsupportedEncodingException ignored) {
      throw new UnsupportedCharsetException(charset.name());
    }
  }
}
