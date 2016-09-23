package com.cognifide.knotx.engine;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by marcin.czeczko on 2016-09-22.
 */
public class SuperTest {
  @Test
  public void test() {
    String jsonRaw = "{\"data\": \"foo\",\"my\": {\"data\": \"foo\"}}";

    JsonObject json = new JsonObject(jsonRaw);

    json.getMap().entrySet().stream()
            .forEach(entry -> System.out.println(entry.getKey()+"="+entry.getValue().getClass()));

//    //I can go through Map and access my objects using map interface
//    JsonObject my = (JsonObject)json.getMap().get("my");
//    System.out.println(my.getString("data"));

    System.out.println("--- Adding new object---");
    JsonObject a = new JsonObject().put("data", "foo").put("my", new JsonObject().put("data", "foo"));

    a.copy().getMap().entrySet().stream()
            .forEach(entry -> System.out.println(entry.getKey()+"="+entry.getValue().getClass()));

    //In this case I need to get JsonObject from map, then get Map in order to access data.
    // it breaks the whole idea that JsonObject can be represented by Map.
//    JsonObject other = (JsonObject)json.getMap().get("other");
//    System.out.println(other.getMap().get("some"));
  }
}
