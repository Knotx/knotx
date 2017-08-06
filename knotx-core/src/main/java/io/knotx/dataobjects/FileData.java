/*
 * Copyright (C) 2017 Cognifide Limited
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
package io.knotx.dataobjects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.FileUpload;


@DataObject(generateConverter = true)
public class FileData {

  private String name;

  private String uploadedFileName;

  private String fileName;

  private long size;

  private String contentType;

  private String contentTransferEncoding;

  private String charSet;

  public FileData() {
    //Nothing to set by default
  }

  public FileData(JsonObject json) {
    FileDataConverter.fromJson(json, this);
  }

  public FileData(String name, String uploadedFileName, String fileName, long size, String contentType, String contentTransferEncoding, String charSet) {
    this.name = name;
    this.uploadedFileName = uploadedFileName;
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.contentTransferEncoding = contentTransferEncoding;
    this.charSet = charSet;
  }

  public FileData(FileUpload fileUpload) {
    this.name = fileUpload.name();
    this.uploadedFileName = fileUpload.uploadedFileName();
    this.fileName = fileUpload.fileName();
    this.size = fileUpload.size();
    this.contentType = fileUpload.contentType();
    this.contentTransferEncoding = fileUpload.contentTransferEncoding();
    this.charSet = fileUpload.charSet();
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    FileDataConverter.toJson(this, json);
    return json;
  }

  public String getName() {
    return name;
  }

  public FileData setName(String name) {
    this.name = name;
    return this;
  }

  public String getUploadedFileName() {
    return uploadedFileName;
  }

  public FileData setUploadedFileName(String uploadedFileName) {
    this.uploadedFileName = uploadedFileName;
    return this;
  }

  public String getFileName() {
    return fileName;
  }

  public FileData setFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  public long getSize() {
    return size;
  }

  public FileData setSize(long size) {
    this.size = size;
    return this;
  }

  public String getContentType() {
    return contentType;
  }

  public FileData setContentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  public String getContentTransferEncoding() {
    return contentTransferEncoding;
  }

  public FileData setContentTransferEncoding(String contentTransferEncoding) {
    this.contentTransferEncoding = contentTransferEncoding;
    return this;
  }

  public String getCharSet() {
    return charSet;
  }

  public FileData setCharSet(String charSet) {
    this.charSet = charSet;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FileData)) {
      return false;
    }
    FileData that = (FileData) o;
    return Objects.equal(name, that.name) &&
        Objects.equal(uploadedFileName, that.uploadedFileName) &&
        Objects.equal(fileName, that.fileName) &&
        Objects.equal(size, that.size) &&
        Objects.equal(contentType, that.contentType) &&
        Objects.equal(contentTransferEncoding, that.contentTransferEncoding) &&
        Objects.equal(charSet, that.charSet);
  }


  @Override
  public int hashCode() {
    return Objects.hashCode(name, uploadedFileName, fileName, size, contentType, contentTransferEncoding, charSet);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", name)
        .add("uploadedFileName", uploadedFileName)
        .add("fileName", fileName)
        .add("size", size)
        .add("contentType", contentType)
        .add("contentTransferEncoding", contentTransferEncoding)
        .add("charSet", charSet)
        .toString();
  }
}
