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
package io.knotx.repository.fs;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * Describes a file system repository configuration
 */
@DataObject(generateConverter = true, publicConverter = false)
public class FilesystemRepositoryOptions {

  /**
   * Default EB address of the file system repository verticle
   */
  public final String DEFAULT_ADDRESS = "knotx.core.repository.filesystem";

  /**
   * Default root folder of the filesystem repository
   */
  public final String DEFAULT_CATALOGUE = StringUtils.EMPTY;

  private String address;
  private String catalogue;

  /**
   * Default constructor
   */
  public FilesystemRepositoryOptions() {
    init();
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public FilesystemRepositoryOptions(FilesystemRepositoryOptions other) {
    this.address = other.address;
    this.catalogue = other.catalogue;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public FilesystemRepositoryOptions(JsonObject json) {
    init();
    FilesystemRepositoryOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    FilesystemRepositoryOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    address = DEFAULT_ADDRESS;
    catalogue = DEFAULT_CATALOGUE;
  }

  /**
   * @return EB address of the verticle
   */
  public String getAddress() {
    return address;
  }

  /**
   * Set the EB address of the file system repository verticle
   *
   * @param address an EB address of the verticle
   * @return a reference to this, so the API can be used fluently
   */
  public FilesystemRepositoryOptions setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * @return Root folder of the files on filesystem repository
   */
  public String getCatalogue() {
    return catalogue;
  }

  /**
   * Set the root folder of the repository on file system.
   * If catalogue equals empty string a verticle will look for the files in classpath
   *
   * @param catalogue a root path to the repository files
   * @return a reference to this, so the API can be used fluently
   */
  public FilesystemRepositoryOptions setCatalogue(String catalogue) {
    this.catalogue = catalogue;
    return this;
  }
}
