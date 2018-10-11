/*
 * Copyright (C) 2018 Cognifide Limited
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
package io.knotx.server.handler.http.request.body;

import io.knotx.server.KnotxServerVerticle;
import io.knotx.util.StringUtil;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.BodyHandler;

@DataObject(generateConverter = true, publicConverter = false)
public class BodyHandlerOptions {

  /**
   * Default File Upload Limit = -1 (unlimited)
   */
  public final static long DEFAULT_UPLOAD_LIMIT = -1;

  /**
   * Default file upload folder = file-uploads
   */
  private static final String DEFAULT_UPLOAD_DIRECTORY = StringUtil
      .getString(KnotxServerVerticle.KNOTX_FILE_UPLOAD_DIR_PROPERTY,
          BodyHandler.DEFAULT_UPLOADS_DIRECTORY);

  private String fileUploadDirectory;
  private Long fileUploadLimit;

  public BodyHandlerOptions(JsonObject json) {
    init();
    if (json != null) {
      BodyHandlerOptionsConverter.fromJson(json, this);
    }
  }

  public void init() {
    fileUploadLimit = DEFAULT_UPLOAD_LIMIT;
    fileUploadDirectory = DEFAULT_UPLOAD_DIRECTORY;
  }


  /**
   * @return limit of the file size when uploading
   */
  public Long getFileUploadLimit() {
    return fileUploadLimit;
  }

  /**
   * Set the file upload limit in bytes
   *
   * @param fileUploadLimit size limit in bytes
   * @return reference to this, so the API can be used fluently
   */
  public BodyHandlerOptions setFileUploadLimit(Long fileUploadLimit) {
    this.fileUploadLimit = fileUploadLimit;
    return this;
  }

  /**
   * @return file upload directory
   */
  public String getFileUploadDirectory() {
    return fileUploadDirectory;
  }

  /**
   * Set the location on Knot.x environment when uploaded files will be stored. These must be an
   * absolute path.
   *
   * @param fileUploadDirectory file upload directory
   * @return reference to this, so the API can be used fluently
   */
  public BodyHandlerOptions setFileUploadDirectory(String fileUploadDirectory) {
    this.fileUploadDirectory = fileUploadDirectory;
    return this;
  }
}
