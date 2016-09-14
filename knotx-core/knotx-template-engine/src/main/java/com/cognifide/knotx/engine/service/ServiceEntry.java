/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
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
package com.cognifide.knotx.engine.service;


import com.cognifide.knotx.api.ServiceCallMethod;
import com.cognifide.knotx.api.TemplateEngineRequest;
import com.cognifide.knotx.engine.TemplateEngineConfiguration;
import com.cognifide.knotx.engine.TemplateEngineConsts;
import com.cognifide.knotx.engine.parser.HtmlFragment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jsoup.nodes.Attribute;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import io.vertx.core.http.HttpMethod;

public class ServiceEntry {

  private String relatedAttribute;
  private String placeholderNamespace;
  private ServiceCallMethod methodType;
  private String serviceUri;
  private TemplateEngineConfiguration.ServiceMetadata serviceMetadata;
  private Map<String, Object> serviceResult;

  private ServiceEntry() {
    //Hidden constructors
  }

  public static ServiceEntry of(Attribute serviceAttribute) {
    ServiceEntry entry = new ServiceEntry();
    entry.relatedAttribute = serviceAttribute.getKey();
    entry.placeholderNamespace = ServiceAttributeUtil.extractNamespace(entry.relatedAttribute);
    entry.methodType = ServiceAttributeUtil.extractMethodType(entry.relatedAttribute);
    entry.serviceUri = serviceAttribute.getValue();
    return entry;
  }

  public ServiceEntry setResult(Map<String, Object> serviceResult) {
    this.serviceResult = serviceResult;
    return this;
  }

  public String getRelatedAttribute() {
    return relatedAttribute;
  }

  public String getServiceUri() {
    return serviceUri;
  }

  public Map<String, Object> getResult() {
    return serviceResult;
  }

  public Map<String, Object> getResultWithNamespaceAsKey() {
    if (StringUtils.isNotEmpty(placeholderNamespace)) {
      return Collections.singletonMap(placeholderNamespace, serviceResult);
    } else {
      return serviceResult;
    }
  }

  public boolean canServeRequest(HtmlFragment fragment, TemplateEngineRequest request) {
    boolean canServeMethodType = canServeMethodType(request.getServerRequestMethod());

    if (isRequestFormPostWithId(request)) {
      return canServeMethodType && canServeFormPost(fragment, request);
    } else {
      return canServeMethodType;
    }
  }

  public String getPlaceholderNamespace() {
    return placeholderNamespace;
  }

  public ServiceCallMethod getMethodType() {
    return methodType;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ServiceEntry) {
      final ServiceEntry other = (ServiceEntry) o;
      return new EqualsBuilder()
          .append(serviceUri, other.getServiceUri())
          .append(placeholderNamespace, other.getPlaceholderNamespace())
          .append(methodType, other.getMethodType())
          .isEquals();
    } else {
      return false;
    }

  }

  @Override
  public int hashCode() {
    return Objects.hash(serviceUri, placeholderNamespace, methodType);
  }

  public ServiceEntry setServiceMetadata(TemplateEngineConfiguration.ServiceMetadata serviceMetadata) {
    this.serviceMetadata = serviceMetadata;
    return this;
  }

  public String getDomain() {
    return serviceMetadata.getDomain();
  }

  public Integer getPort() {
    return serviceMetadata.getPort();
  }


  private boolean canServeMethodType(HttpMethod requestMethodType) {
    ServiceCallMethod methodTypeFromRequest = ServiceCallMethod.from(requestMethodType);
    return Objects.equals(this.methodType, methodTypeFromRequest)
        || Objects.equals(this.methodType, ServiceCallMethod.ALL);
  }


  private boolean canServeFormPost(HtmlFragment fragment, TemplateEngineRequest request) {
    String htmlFragmentId = fragment.getDataId();
    String requestFormId = request.getFormAttributes().get(TemplateEngineConsts.FORM_ID_ATTRIBUTE);
    return Objects.equals(requestFormId, htmlFragmentId) || ServiceCallMethod.POST != (methodType);
  }

  private boolean isRequestFormPostWithId(TemplateEngineRequest request) {
    if (request.getServerRequestMethod() != HttpMethod.POST) {
      return false;
    }
    String requestFormId = request.getFormAttributes().get(TemplateEngineConsts.FORM_ID_ATTRIBUTE);
    return StringUtils.isNotEmpty(requestFormId);
  }
}
