/*
 * Knot.x - Reactive microservice assembler - Http Repository Connector Verticle
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
package com.cognifide.knotx.launcher;

import static org.junit.Assert.assertEquals;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ClearSystemProperties;

public class SystemPropsConfigurationTest {
  
  private final static String EXPECTED_OBJECT = "{\"someInteger\":1234,\"someBoolean\":false,\"someString\":\"foo\","
      + "\"someNested\":{\"nestedInteger\":1234,\"nestedBoolean\":false,\"nestedString\":\"foo\"}}";
  
  
  @Rule
  public final ClearSystemProperties cleanedProperties = new ClearSystemProperties("io.knotx.KnotxServer",
      "io.knotx.KnotxServer.options.config.httpPort", "io.knotx.KnotxServer.options.config.pass",
      "io.knotx.KnotxServer.options.config.settings.bogus", "io.knotx.KnotxServer.options.config", "io.knotx.KnotxServer.options.haGroup",
      "io.knotx.KnotxServer.options.config.someData");
  
  @Test
  public void setNoEnvValue() {
    SystemPropsConfiguration systemPropsConfiguration = new SystemPropsConfiguration("io.knotx.KnotxServer");
    
    assertEquals(true, systemPropsConfiguration.envConfig().isEmpty());
  }
  
  @Test
  public void setOneValueNoKey() {
    System.setProperty("io.knotx.KnotxServer", "9999");
    SystemPropsConfiguration systemPropsConfiguration = new SystemPropsConfiguration("io.knotx.KnotxServer");
    
    assertEquals(true, systemPropsConfiguration.envConfig().isEmpty());
  }
  
  @Test
  public void setOneEnvValue() {
    System.setProperty("io.knotx.KnotxServer.options.config.httpPort", "9999");
    SystemPropsConfiguration systemPropsConfiguration = new SystemPropsConfiguration("io.knotx.KnotxServer");
    
    assertEquals(1, systemPropsConfiguration.envConfig().size());
    assertEquals(new Integer(9999), systemPropsConfiguration.envConfig().get("options.config.httpPort").getObject());
  }
  
  @Test
  public void setThreeEnvValues() {
    System.setProperty("io.knotx.KnotxServer.options.config.httpPort", "9999");
    System.setProperty("io.knotx.KnotxServer.options.config.pass", "true");
    System.setProperty("io.knotx.KnotxServer.options.config.settings.bogus", "abcd");
    SystemPropsConfiguration systemPropsConfiguration = new SystemPropsConfiguration("io.knotx.KnotxServer");
    
    assertEquals(3, systemPropsConfiguration.envConfig().size());
    assertEquals(new Integer(9999), systemPropsConfiguration.envConfig().get("options.config.httpPort").getObject());
    assertEquals(true, systemPropsConfiguration.envConfig().get("options.config.pass").getObject());
    assertEquals("abcd", systemPropsConfiguration.envConfig().get("options.config.settings.bogus").getObject());
  }
  
  @Test
  public void setFileEnvValue() {
    System.setProperty("io.knotx.KnotxServer.options.config", "file:src/test/resources/sampleConfig.json");
    SystemPropsConfiguration systemPropsConfiguration = new SystemPropsConfiguration("io.knotx.KnotxServer");
    
    assertEquals(1, systemPropsConfiguration.envConfig().size());
    assertEquals(EXPECTED_OBJECT, ((JsonObject) systemPropsConfiguration.envConfig().get("options.config").getObject()).encode());
  }
  
  @Test
  public void testUpdateDescriptorOneValue() {
    System.setProperty("io.knotx.KnotxServer.options.config.httpPort", "9999");
    SystemPropsConfiguration systemPropsConfiguration = new SystemPropsConfiguration("io.knotx.KnotxServer");
    
    JsonObject descriptor = systemPropsConfiguration.updateJsonObject(getDescriptor());
    
    assertEquals(3, descriptor.getJsonObject("options").getJsonObject("config").size());
    assertEquals("value", descriptor.getJsonObject("options").getJsonObject("config").getString("address"));
    assertEquals(new Integer(9999), descriptor.getJsonObject("options").getJsonObject("config").getInteger("httpPort"));
    assertEquals(true, descriptor.getJsonObject("options").getJsonObject("config").containsKey("someData"));
    assertEquals(new Integer(123), descriptor.getJsonObject("options").getJsonObject("config").getJsonObject("someData").getInteger("data"));
    assertEquals("string", descriptor.getJsonObject("options").getJsonObject("config").getJsonObject("someData").getString("string"));
  }
  
  @Test
  public void testMergeDescriptorWithFile() {
    System.setProperty("io.knotx.KnotxServer.options.config", "file:src/test/resources/sampleConfig.json");
    SystemPropsConfiguration systemPropsConfiguration = new SystemPropsConfiguration("io.knotx.KnotxServer");
    
    JsonObject descriptor = systemPropsConfiguration.updateJsonObject(getDescriptor());
    
    assertEquals(7, descriptor.getJsonObject("options").getJsonObject("config").size());
  
    //original data
    assertEquals("value", descriptor.getJsonObject("options").getJsonObject("config").getString("address"));
    assertEquals(new Integer(1234), descriptor.getJsonObject("options").getJsonObject("config").getInteger("httpPort"));
    assertEquals(true, descriptor.getJsonObject("options").getJsonObject("config").containsKey("someData"));
    assertEquals(new Integer(123), descriptor.getJsonObject("options").getJsonObject("config").getJsonObject("someData").getInteger("data"));
    assertEquals("string", descriptor.getJsonObject("options").getJsonObject("config").getJsonObject("someData").getString("string"));
    
    //merged data
    assertEquals(new Integer(1234), descriptor.getJsonObject("options").getJsonObject("config").getInteger("someInteger"));
    assertEquals(false, descriptor.getJsonObject("options").getJsonObject("config").getBoolean("someBoolean"));
    assertEquals("foo", descriptor.getJsonObject("options").getJsonObject("config").getString("someString"));
    
    assertEquals(3, descriptor.getJsonObject("options").getJsonObject("config").getJsonObject("someNested").size());
    assertEquals(new Integer(1234),
        descriptor.getJsonObject("options").getJsonObject("config").getJsonObject("someNested").getInteger("nestedInteger"));
    assertEquals(false, descriptor.getJsonObject("options").getJsonObject("config").getJsonObject("someNested").getBoolean("nestedBoolean"));
    assertEquals("foo", descriptor.getJsonObject("options").getJsonObject("config").getJsonObject("someNested").getString("nestedString"));
  }
  
  @Test
  public void testMergeDescriptorWithFileAndValues() {
    System.setProperty("io.knotx.KnotxServer.options.haGroup", "default");
    System.setProperty("io.knotx.KnotxServer.options.config.httpPort", "9999");
    System.setProperty("io.knotx.KnotxServer.options.config.someData", "file:src/test/resources/sampleConfig.json");
    SystemPropsConfiguration systemPropsConfiguration = new SystemPropsConfiguration("io.knotx.KnotxServer");
    
    JsonObject descriptor = systemPropsConfiguration.updateJsonObject(getDescriptor());
    
    assertEquals("default", descriptor.getJsonObject("options").getString("haGroup"));
    
    assertEquals(3, descriptor.getJsonObject("options").getJsonObject("config").size());
    assertEquals("value", descriptor.getJsonObject("options").getJsonObject("config").getString("address"));
    assertEquals(new Integer(9999), descriptor.getJsonObject("options").getJsonObject("config").getInteger("httpPort"));
    
    JsonObject nested = descriptor.getJsonObject("options").getJsonObject("config").getJsonObject("someData");
    assertEquals(6, nested.size());
    assertEquals(new Integer(123), nested.getInteger("data"));
    assertEquals("string", nested.getString("string"));
    assertEquals(new Integer(1234), nested.getInteger("someInteger"));
    assertEquals(false, nested.getBoolean("someBoolean"));
    assertEquals("foo", nested.getString("someString"));
    
    assertEquals(3, nested.getJsonObject("someNested").size());
    assertEquals(new Integer(1234), nested.getJsonObject("someNested").getInteger("nestedInteger"));
    assertEquals(false, nested.getJsonObject("someNested").getBoolean("nestedBoolean"));
    assertEquals("foo", nested.getJsonObject("someNested").getString("nestedString"));
  }
  
  @Test
  public void testMergeDescriptorWithFileWithArray() {
    System.setProperty("io.knotx.KnotxServer.options.config", "file:src/test/resources/sampleConfigArray.json");
    SystemPropsConfiguration systemPropsConfiguration = new SystemPropsConfiguration("io.knotx.KnotxServer");
  
    JsonObject descriptor = systemPropsConfiguration.updateJsonObject(getDescriptorArray());
  
    assertEquals(6, descriptor.getJsonObject("options").getJsonObject("config").size());
    assertEquals("value", descriptor.getJsonObject("options").getJsonObject("config").getString("address"));
    assertEquals(new Integer(1234), descriptor.getJsonObject("options").getJsonObject("config").getInteger("httpPort"));
    assertEquals(true, descriptor.getJsonObject("options").getJsonObject("config").containsKey("someData"));
    assertEquals(new Integer(123), descriptor.getJsonObject("options").getJsonObject("config").getJsonObject("someData").getInteger("data"));
    assertEquals("string", descriptor.getJsonObject("options").getJsonObject("config").getJsonObject("someData").getString("string"));
  
    assertEquals(1, descriptor.getJsonObject("options").getJsonObject("config").getJsonArray("array2").size());
    assertEquals("123", descriptor.getJsonObject("options").getJsonObject("config").getJsonArray("array2").getString(0));
    
    assertEquals(new Integer(6666), descriptor.getJsonObject("options").getJsonObject("config").getInteger("someInteger"));
    assertEquals(3, descriptor.getJsonObject("options").getJsonObject("config").getJsonArray("array").size());
    assertEquals("abc", descriptor.getJsonObject("options").getJsonObject("config").getJsonArray("array").getString(0));
    assertEquals("def", descriptor.getJsonObject("options").getJsonObject("config").getJsonArray("array").getString(1));
    assertEquals("ghi", descriptor.getJsonObject("options").getJsonObject("config").getJsonArray("array").getString(2));
  }
  
  private JsonObject getDescriptor() {
    return new JsonObject().put("options",
        new JsonObject()
            .put("config",
                new JsonObject()
                    .put("address", "value")
                    .put("httpPort", 1234)
                    .put("someData", new JsonObject()
                        .put("data", 123)
                        .put("string", "string"))
            ));
  }
  
  private JsonObject getDescriptorArray() {
    return new JsonObject().put("options",
        new JsonObject()
            .put("config",
                new JsonObject()
                    .put("address", "value")
                    .put("httpPort", 1234)
                    .put("someData", new JsonObject()
                        .put("data", 123)
                        .put("string", "string"))
                    .put("array", new JsonArray().add("123"))
                    .put("array2", new JsonArray().add("123"))
            ));
  }
}
