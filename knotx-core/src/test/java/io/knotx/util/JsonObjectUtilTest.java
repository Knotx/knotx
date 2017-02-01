/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

import io.vertx.core.json.JsonObject;
import org.junit.Test;

public class JsonObjectUtilTest {

  private final static String OLD_DOMAIN = "old.domain";
  private final static int OLD_PORT = 8000;

  private final static String NEW_DOMAIN = "new.domain";
  private final static int NEW_PORT = 8000;

  private JsonObject source = new JsonObject()
          .put("clientDestinations", new JsonObject()
              .put("domain", OLD_DOMAIN)
              .put("port", OLD_PORT));

  @Test
  public void whenSourceObjectMergedWithChangedOneDeepField_expectOnlyThatFieldIsOverriden() {
    JsonObject newDomain = new JsonObject()
            .put("clientDestinations", new JsonObject()
                .put("domain", NEW_DOMAIN));

    JsonObject result = JsonObjectUtil.deepMerge(source, newDomain);

    assertThat(
        result.getJsonObject("clientDestinations").getString("domain"),
        equalTo(NEW_DOMAIN));
    assertThat(
        result.getJsonObject("clientDestinations").getInteger("port"),
        equalTo(OLD_PORT));
  }

  @Test
  public void whenSourceObjectMergedWithChangedAllDeepFields_expectAllDeepFieldsAreOverriden() {
    JsonObject allFields = new JsonObject()
            .put("clientDestinations", new JsonObject()
                .put("domain", NEW_DOMAIN)
                .put("port", NEW_PORT));

    JsonObject result = JsonObjectUtil.deepMerge(source, allFields);

    assertThat(
        result.getJsonObject("clientDestinations").getString("domain"),
        equalTo(NEW_DOMAIN));
    assertThat(
        result.getJsonObject("clientDestinations").getInteger("port"),
        equalTo(NEW_PORT));
  }

  @Test
  public void whenSourceObjectMergedWithUpdateAndNewDeepFields_expectAllDeepFieldsAreOverridenAndNewAdded() {
    JsonObject updateAndAdd = new JsonObject()
            .put("clientDestinations", new JsonObject()
                .put("domain", NEW_DOMAIN)
                .put("port", NEW_PORT)
                .put("newField", true));

    JsonObject result = JsonObjectUtil.deepMerge(source, updateAndAdd);

    assertThat(
        result.getJsonObject("clientDestinations").getString("domain"),
        equalTo(NEW_DOMAIN));
    assertThat(
        result.getJsonObject("clientDestinations").getInteger("port"),
        equalTo(NEW_PORT));
    assertThat(
        result.getJsonObject("clientDestinations").getBoolean("newField"),
        equalTo(true));
  }

  @Test
  public void whenSourceObjectMergedWithNewObject_expectNewObjectAdded() {
    JsonObject updateAndAdd = new JsonObject()
            .put("clientOptions", new JsonObject()
                .put("a", "aaa")
                .put("b", 1234));

    JsonObject result = JsonObjectUtil.deepMerge(source, updateAndAdd);

    assertThat(
        result.getJsonObject("clientDestinations").getString("domain"),
        equalTo(OLD_DOMAIN));
    assertThat(
        result.getJsonObject("clientDestinations").getInteger("port"),
        equalTo(OLD_PORT));
    assertThat(
        result.getJsonObject("clientOptions").getString("a"),
        equalTo("aaa"));
    assertThat(
        result.getJsonObject("clientOptions").getInteger("b"),
        equalTo(1234));
  }

  @Test
  public void whenSourceObjectMergedWithNewObjectAndOverrides_expectNewObjectAddedAndOverridesHappened() {
    JsonObject updateAndAdd = new JsonObject()
            .put("clientDestinations", new JsonObject()
                .put("domain", NEW_DOMAIN))
            .put("clientOptions", new JsonObject()
                .put("a", "aaa")
                .put("b", 1234));

    JsonObject result = JsonObjectUtil.deepMerge(source, updateAndAdd);

    assertThat(
        result.getJsonObject("clientDestinations").getString("domain"),
        equalTo(NEW_DOMAIN));
    assertThat(
        result.getJsonObject("clientDestinations").getInteger("port"),
        equalTo(OLD_PORT));
    assertThat(
        result.getJsonObject("clientOptions").getString("a"),
        equalTo("aaa"));
    assertThat(
        result.getJsonObject("clientOptions").getInteger("b"),
        equalTo(1234));
  }

}
