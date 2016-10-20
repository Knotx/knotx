package com.cognifide.knotx.dataobjects;

import io.vertx.core.json.JsonObject;

public interface JsonAdapter {

  /**
   * @return the JSON representation of the object.
   */
  JsonObject toJson();

}
