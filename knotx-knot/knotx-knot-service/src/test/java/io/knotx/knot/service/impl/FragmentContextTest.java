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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import io.knotx.dataobjects.Fragment;
import io.knotx.junit5.KnotxArgumentConverter;
import io.knotx.knot.service.service.ServiceEntry;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

public class FragmentContextTest {

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "snippet_one_service_no_params.txt;{}",
      "snippet_one_service_invalid_params_bound.txt;{}",
      "snippet_one_service_one_param.txt;{\"path\":\"/overridden/path\"}",
      "snippet_one_service_many_params.txt;{\"path\":\"/overridden/path\",\"anotherParam\":\"someValue\"}",
      "snippet_one_service_many_params-no-prefix.txt;{\"path\":\"/overridden/path\",\"anotherParam\":\"someValue\"}",
  })
  public void from_whenFragmentContainsOneService_expectFragmentContextWithExtractedParamsParams(
      @ConvertWith(KnotxArgumentConverter.class) Fragment fragment, String expectedParameters) {

    final FragmentContext fragmentContext = FragmentContext.from(fragment);
    final ServiceEntry serviceEntry = fragmentContext.services.get(0);
    assertThat(serviceEntry.getParams().toString(), sameJSONAs(expectedParameters));
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "snippet_one_service_no_params.txt;1",
      "snippet_one_service_one_param.txt;1",
      "snippet_one_service_many_params.txt;1",
      "snippet_two_services.txt;2",
      "snippet_five_services.txt;5",
      "snippet_two_services-no-prefix.txt;2",
      "snippet_one_service_many_params-no-prefix.txt;1"
  })
  public void from_whenFragmentContainsServices_expectFragmentContextWithProperNumberOfServicesExtracted(
      @ConvertWith(KnotxArgumentConverter.class) Fragment fragment, int numberOfExpectedServices) {

    final FragmentContext fragmentContext = FragmentContext.from(fragment);
    assertThat(fragmentContext.services.size(), is(numberOfExpectedServices));
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "snippet_two_services_with_params.txt;{\"first\":{\"first-service-key\":\"first-service-value\"},\"second\":{\"second-service-key\":\"second-service-value\"}}",
      "snippet_four_services_with_params_and_extra_param.txt;{\"a\":{\"a\":\"a\"},\"b\":{\"b\":\"b\"},\"c\":{\"c\":\"c\"},\"d\":{\"d\":\"d\"}}",
      "snippet_four_services_with_params_and_extra_param-no-prefix.txt;{\"a\":{\"a\":\"a\"},\"b\":{\"b\":\"b\"},\"c\":{\"c\":\"c\"},\"d\":{\"d\":\"d\"}}"
  })
  public void from_whenFragmentContainsServices_expectProperlyAssignedParams(
      @ConvertWith(KnotxArgumentConverter.class) Fragment fragment, JsonObject parameters) {

    final FragmentContext fragmentContext = FragmentContext.from(fragment);
    fragmentContext.services.forEach(serviceEntry ->
        assertThat(serviceEntry.getParams().toString(),
            sameJSONAs(parameters.getJsonObject(serviceEntry.getNamespace()).toString())
        )
    );
  }

}
