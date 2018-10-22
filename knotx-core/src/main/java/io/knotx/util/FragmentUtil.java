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
package io.knotx.util;

import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotStatus;

public final class FragmentUtil {
  public static Fragment failure(Fragment fragment, String knot, Throwable t) {
    fragment.failed(true)
        .knotRouting().stream()
        .filter(k -> knot.equals(k.getName()))
        .findFirst()
        .get()
        .setStatus(KnotStatus.FAILURE)
        .addError(t.getClass().getSimpleName(), t.getMessage());
    return fragment;
  }

  public static Fragment success(Fragment fragment, String knot) {
    fragment.failed(false)
        .knotRouting().stream()
        .filter(k -> knot.equals(k.getName()))
        .findFirst()
        .get()
        .setStatus(KnotStatus.SUCCESS);
    return fragment;
  }

}
