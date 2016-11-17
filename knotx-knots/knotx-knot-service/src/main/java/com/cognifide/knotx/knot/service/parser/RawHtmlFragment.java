/*
 * Knot.x - Reactive microservice assembler - View Knot
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
package com.cognifide.knotx.knot.service.parser;

import com.cognifide.knotx.knot.service.service.ServiceEntry;
import com.cognifide.knotx.fragments.Fragment;

import io.vertx.core.json.JsonObject;
import rx.Observable;

public class RawHtmlFragment implements HtmlFragment {

  private final Fragment fragment;

  public RawHtmlFragment(Fragment fragment) {
    this.fragment = fragment;
  }

  @Override
  public String getContentWithContext(JsonObject model) {
    return fragment.getContent();
  }

  @Override
  public Fragment getFragment() {
    return fragment;
  }

  @Override
  public boolean hasHandlebarsTemplate() {
    return false;
  }

  @Override
  public Observable<ServiceEntry> getServices() {
    return Observable.empty();
  }

}
