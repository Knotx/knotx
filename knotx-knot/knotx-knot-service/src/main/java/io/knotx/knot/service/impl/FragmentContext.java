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
package io.knotx.knot.service.impl;

import com.google.common.base.MoreObjects;
import io.knotx.dataobjects.Fragment;
import io.knotx.knot.service.service.ServiceAttributeUtil;
import io.knotx.knot.service.service.ServiceEntry;
import io.reactivex.Observable;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class FragmentContext {

  private static final String DATA_SERVICE = "data-knotx-service.*";
  private static final String DATA_PARAMS = "data-knotx-params.*";

  private Fragment fragment;
  List<ServiceEntry> services;

  private FragmentContext() {
    //hidden constructor
  }

  /**
   * Factory method that creates context from the {@link Fragment}. All services and params are
   * extracted to separate entries.
   *
   * @param fragment - fragment from which the context will be created.
   * @return a FragmentContext that wraps given fragment.
   */
  public static FragmentContext from(Fragment fragment) {
    Document document = Jsoup.parseBodyFragment(fragment.content());
    Element scriptTag = document.body().child(0);

    List<Attribute> attributes = scriptTag.attributes().asList();

    Map<String, Attribute> serviceAttributes = attributes.stream()
        .filter(attribute -> attribute.getKey().matches(DATA_SERVICE))
        .collect(Collectors
            .toMap(attribute -> ServiceAttributeUtil.extractNamespace(attribute.getKey()),
                Function.identity()));

    Map<String, Attribute> paramsAttributes = attributes.stream()
        .filter(attribute -> attribute.getKey().matches(DATA_PARAMS))
        .collect(Collectors
            .toMap(attribute -> ServiceAttributeUtil.extractNamespace(attribute.getKey()),
                Function.identity()));

    return new FragmentContext()
        .fragment(fragment)
        .services(
            serviceAttributes.entrySet().stream()
                .map(entry -> new ServiceEntry(entry.getValue(),
                    paramsAttributes.get(entry.getKey())))
                .collect(Collectors.toList())
        );
  }

  /**
   * @return an {@link Observable} that emits a list of {@link ServiceEntry} that were registered
   * with current {@link Fragment}.
   */
  public Observable<ServiceEntry> services() {
    return Observable.fromIterable(services);
  }

  /**
   * @return a fragment wrapped in this context.
   */
  public Fragment fragment() {
    return fragment;
  }

  private FragmentContext fragment(Fragment fragment) {
    this.fragment = fragment;
    return this;
  }

  private FragmentContext services(List<ServiceEntry> services) {
    this.services = services;
    return this;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("fragment", fragment)
        .add("services", services)
        .toString();
  }
}
