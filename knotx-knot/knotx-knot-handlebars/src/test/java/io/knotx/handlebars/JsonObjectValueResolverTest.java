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
package io.knotx.handlebars;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.knotx.junit5.util.FileReader;
import io.knotx.knot.templating.handlebars.JsonObjectValueResolver;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonObjectValueResolverTest {

  private String expected;
  private Template template;

  @BeforeEach
  public void before() throws Exception {
    template = new Handlebars().compileInline(FileReader.readText("sample.hbs"));
    expected = FileReader.readText("expected").trim();
  }

  @Test
  public void JsonObjectResolver_whenApplyingProgrammaticallyCreatedObject_expectVariablesResolved()
      throws Exception {
    Context context = Context.newBuilder(programmaticModel()).push(JsonObjectValueResolver.INSTANCE)
        .build();
    String compiled = template.apply(context).trim();

    assertThat(compiled, equalTo(expected));
  }

  @Test
  public void JsonObjectResolver_whenApplyingFileBasedObject_expectVariablesResolved()
      throws Exception {
    Context context = Context.newBuilder(filebasedModel()).push(JsonObjectValueResolver.INSTANCE)
        .build();
    String compiled = template.apply(context).trim();

    assertThat(compiled, equalTo(expected));
  }

  private JsonObject programmaticModel() {
    return new JsonObject().put("sample",
        new JsonObject().put("result",
            new JsonObject().put("first", "First Message")
                .put("second",
                    new JsonObject().put("foo", "Very Long Second Foo Message !! 2 &&%^$")))
            .put("arr", new JsonArray().add(1).add(2).add(3).add("foo").add("bar").add(true)));

  }

  private JsonObject filebasedModel() throws Exception {
    return new JsonObject(FileReader.readText("testObject.json"));
  }
}
