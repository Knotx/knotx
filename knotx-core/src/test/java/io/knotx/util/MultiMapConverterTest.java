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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import com.googlecode.zohhak.api.Coercion;
import com.googlecode.zohhak.api.Configure;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ZohhakRunner.class)
@Configure(separator = ";")
public class MultiMapConverterTest {

  @Test
  public void toJsonObject_whenMultiMapEmpty_expectEmptyJsonObject() throws Exception {
    JsonObject jsonObject = MultiMapConverter.toJsonObject(MultiMap.caseInsensitiveMultiMap());
    assertThat(
        jsonObject.size(),
        equalTo(0));
  }

  @TestWith({
      "mapKey1:A,B,C;{\"mapKey1\":[\"A\",\"B\",\"C\"]}",
      "mapKey1:A,B,C,D;{\"mapKey1\":[\"A\",\"B\",\"C\",\"D\"]}",
      "mapKey1:A,B,C,D,E;{\"mapKey1\":[\"A\",\"B\",\"C\",\"D\",\"E\"]}",
      "mapKey1:A|mapKey2:B;{\"mapKey1\":[\"A\"],\"mapKey2\":[\"B\"]}",
      "mapKey1:A|mapKey2:B|mapKey3:C;{\"mapKey1\":[\"A\"],\"mapKey2\":[\"B\"],\"mapKey3\":[\"C\"]}",
      "mapKey1:A,B|mapKey2:C,D|mapKey3:E,F;{\"mapKey1\":[\"A\",\"B\"],\"mapKey2\":[\"C\",\"D\"],\"mapKey3\":[\"E\",\"F\"]}",
      "mapKey1:A,B,C|mapKey2:A,B,C|mapKey3:A,B,C;{\"mapKey1\":[\"A\",\"B\",\"C\"],\"mapKey2\":[\"A\",\"B\",\"C\"],\"mapKey3\":[\"A\",\"B\",\"C\"]}"
  })
  public void toJsonObject_whenMultiMapWithMultipleEntries_expectJsonObjectWithJsonArrays(
      MultiMap multiMap, String expectedJson) throws Exception {
    JsonObject jsonObject = MultiMapConverter.toJsonObject(multiMap);
    assertThat(
        jsonObject.toString(),
        sameJSONAs(expectedJson).allowingAnyArrayOrdering());
  }

  @TestWith({
      "{\"mapKey1\":[\"A\",\"B\",\"C\"]};1",
      "{\"mapKey1\":[\"A\"],\"mapKey2\":[\"B\"]};2",
      "{\"mapKey1\":[\"A\"],\"mapKey2\":[\"B\"],\"mapKey3\":[\"C\"]};3"
  })
  public void fromJsonObject_whenNotEmptyJson_expectSizeOfMultiMap(
      JsonObject jsonObject, int multimapSize) throws Exception {
    final MultiMap multiMap = MultiMapConverter.fromJsonObject(jsonObject);
    assertThat(
        multiMap.size(),
        equalTo(multimapSize));
  }

  @Coercion
  public JsonObject provideJsonObject(String input) {
    return new JsonObject(input);
  }

  @Coercion
  public MultiMap provideMultiMap(String input) {
    final MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();

    final String[] entries = input.split("\\|");
    for (String entry : entries) {
      final String[] keyWithValues = entry.split(":");
      final String[] elements = keyWithValues[1].split(",");
      for (String e : elements) {
        multiMap.add(keyWithValues[0], e);
      }
    }

    return multiMap;
  }
}
