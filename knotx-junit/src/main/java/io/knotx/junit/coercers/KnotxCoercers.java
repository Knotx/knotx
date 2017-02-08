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
package io.knotx.junit.coercers;

import static org.mockito.Mockito.when;

import io.knotx.dataobjects.Fragment;
import io.knotx.junit.util.FileReader;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import org.mockito.Mockito;

public class KnotxCoercers {

  public Fragment provideFragment(String fragmentContentFile) throws IOException {
    final String fragmentContent = FileReader.readText(fragmentContentFile);
    Fragment fragmentMock = Mockito.mock(Fragment.class);
    when(fragmentMock.content()).thenReturn(fragmentContent);
    return fragmentMock;
  }

  public JsonObject provideJsonObjectFromString(String input) {
    return new JsonObject(input);
  }

}
