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
package io.knotx.adapter.common.placeholders;

public class SlingUriInfo {

  private String path;

  private String[] pathParts;

  private String selectorString;

  private String[] selectors;

  private String extension;

  private String suffix;

  public SlingUriInfo(String path, String[] pathParts, String selectorString, String[] selectors,
      String extension, String suffix) {
    this.path = path;
    this.pathParts = pathParts;
    this.selectorString = selectorString;
    this.selectors = selectors;
    this.extension = extension;
    this.suffix = suffix;
  }

  public String getPath() {
    return path;
  }

  public String[] getPathParts() {
    return pathParts;
  }

  public String getPathPart(int index) {
    return pathParts != null && 0 <= index && index < pathParts.length ? pathParts[index] : null;
  }

  public String getSelectorString() {
    return selectorString;
  }

  public String[] getSelectors() {
    return selectors;
  }

  public String getSelector(int index) {
    return selectors != null && 0 <= index && index < selectors.length ? selectors[index] : null;
  }

  public String getExtension() {
    return extension;
  }

  public String getSuffix() {
    return suffix;
  }

}
