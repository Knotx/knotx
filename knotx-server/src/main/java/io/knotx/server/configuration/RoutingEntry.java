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
package io.knotx.server.configuration;

import java.util.Map;

public class RoutingEntry {

  private String path;
  private boolean csrfEnabled;
  private String address;
  private Map<String, RoutingEntry> onTransition;

  public RoutingEntry(String path, boolean csrfEnabled, String address, Map<String, RoutingEntry> onTransition) {
    this.path = path;
    this.csrfEnabled = csrfEnabled;
    this.address = address;
    this.onTransition = onTransition;
  }

  public String path() {
    return path;
  }

  public boolean isCsrfEnabled() {
    return csrfEnabled;
  }

  public String address() {
    return address;
  }

  public Map<String, RoutingEntry> onTransition() {
    return onTransition;
  }
}
