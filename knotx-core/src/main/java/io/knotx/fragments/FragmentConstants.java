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
package io.knotx.fragments;

import java.util.regex.Pattern;

public final class FragmentConstants {

  public static final String SNIPPET_IDENTIFIER_NAME = "data-knotx-knots";

  public static final Pattern ANY_SNIPPET_PATTERN =
      Pattern.compile("(?is).*<script\\s+" + SNIPPET_IDENTIFIER_NAME + ".*", Pattern.DOTALL);

  public static final Pattern SNIPPET_PATTERN =
      Pattern.compile("<script\\s+" + SNIPPET_IDENTIFIER_NAME
          + "\\s*=\\s*\"([A-Za-z0-9-,]+)\"[^>]*>.+?</script>", Pattern.DOTALL);

  public static final String FRAGMENT_IDENTIFIERS_SEPARATOR = ",";

  static final int DEBUG_MAX_FRAGMENT_CONTENT_LOG_LENGTH = 256;

  private FragmentConstants() {
    //constants util class constructor
  }

}
