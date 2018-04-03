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
package io.knotx.repository.http;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true, publicConverter = false)
public class ClientDestination {

  private String scheme;
  private String domain;
  private int port;
  private String hostHeader;

  /**
   * Default constructor
   */
  public ClientDestination() {
    //Nothing to do
  }


  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public ClientDestination(ClientDestination other) {
    this.scheme = other.scheme;
    this.domain = other.domain;
    this.port = other.port;
    this.hostHeader = other.hostHeader;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public ClientDestination(JsonObject json) {
    ClientDestinationConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ClientDestinationConverter.toJson(this, json);
    return json;
  }

  /**
   *
   * @return URL scheme to be used to communicate with repository
   */
  public String getScheme() {
    return scheme;
  }

  /**
   * Set the URL scheme used to communicate with the repository
   * @param scheme an URL scheme (http, https)
   * @return a reference to this, so the API can be used fluently
   */
  public ClientDestination setScheme(String scheme) {
    this.scheme = scheme;
    return this;
  }

  /**
   *
   * @return domain of the http repository
   */
  public String getDomain() {
    return domain;
  }

  /**
   * Set the domain of the http repository
   * @param domain a domain of the remote location of the repository
   * @return a reference to this, so the API can be used fluently
   */
  public ClientDestination setDomain(String domain) {
    this.domain = domain;
    return this;
  }

  /**
   *
   * @return HTTP port of the http repository
   */
  public int getPort() {
    return port;
  }

  /**
   * Set the HTTP port of the http repository
   * @param port a HTTP port of the remote location of the repository
   * @return a reference to this, so the API can be used fluently
   */
  public ClientDestination setPort(int port) {
    this.port = port;
    return this;
  }

  /**
   *
   * @return Host header override to be used with a communication to the repository
   */
  public String getHostHeader() {
    return hostHeader;
  }

  /**
   * Set the host header override to be used with a communication to the repository.
   * If it's set, it overrides any value in the 'Host' header, and sets the SNI SSL to the same value.
   * @param hostHeader a host header value
   * @return a reference to this, so the API can be used fluently
   */
  public ClientDestination setHostHeader(String hostHeader) {
    this.hostHeader = hostHeader;
    return this;
  }
}
