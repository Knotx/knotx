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


import com.cognifide.knotx.dataobjects.ServiceCallMethod;
import com.cognifide.knotx.dataobjects.RenderRequest;
import com.cognifide.knotx.engine.TemplateEngineConfiguration;
import com.cognifide.knotx.engine.TemplateEngineConsts;
import com.cognifide.knotx.engine.parser.HtmlFragment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jsoup.nodes.Attribute;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

public class ServiceEntry {

  private String placeholderNamespace;
  private ServiceCallMethod methodType;
  private String serviceUri;
  private TemplateEngineConfiguration.ServiceMetadata serviceMetadata;

  private ServiceEntry() {
    //Hidden constructors
  }

  public static ServiceEntry of(Attribute serviceAttribute) {
    ServiceEntry entry = new ServiceEntry();

    String relatedAttribute = serviceAttribute.getKey();
    entry.placeholderNamespace = ServiceAttributeUtil.extractNamespace(relatedAttribute);
    entry.methodType = ServiceAttributeUtil.extractMethodType(relatedAttribute);
    entry.serviceUri = serviceAttribute.getValue();
    return entry;
  }

  public String getPlaceholderNamespace() {
    return placeholderNamespace;
  }

  public ServiceCallMethod getMethodType() {
    return methodType;
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

  public List<Pattern> getHeadersPatterns(){
    return serviceMetadata.getAllowedRequestHeaderPatterns();
  }

  public String getServiceUri() {
    return serviceUri;
  }

  public JsonObject getResultWithNamespaceAsKey(JsonObject result) {
    if (StringUtils.isNotEmpty(placeholderNamespace)) {
      return new JsonObject().put(placeholderNamespace, result);
    } else {
      return result;
    }
  }

  public boolean canServeRequest(HtmlFragment fragment, RenderRequest renderRequest) {
    boolean canServeMethodType = canServeMethodType(renderRequest.request().method());

    if (isRequestFormPostWithId(renderRequest)) {
      return canServeMethodType && canServeFormPost(fragment, renderRequest);
    } else {
      return canServeMethodType;
    }
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

  private boolean canServeMethodType(HttpMethod requestMethodType) {
    ServiceCallMethod methodTypeFromRequest = ServiceCallMethod.from(requestMethodType);
    return Objects.equals(this.methodType, methodTypeFromRequest)
        || Objects.equals(this.methodType, ServiceCallMethod.ALL);
  }

  private boolean isRequestFormPostWithId(RenderRequest renderRequest) {
    if (renderRequest.request().method() != HttpMethod.POST) {
      return false;
    }
    String requestFormId = renderRequest.request().formAttributes().get(TemplateEngineConsts.FORM_ID_ATTRIBUTE);
    return StringUtils.isNotEmpty(requestFormId);
  }

  private boolean canServeFormPost(HtmlFragment fragment, RenderRequest renderRequest) {
    Optional<String> fragmentId = Optional.ofNullable(fragment.getDataId());
    Optional<String> formId = Optional.ofNullable(renderRequest.request().formAttributes())
        .map(attr -> attr.get(TemplateEngineConsts.FORM_ID_ATTRIBUTE));

    return fragmentId.equals(formId) || ServiceCallMethod.POST != methodType;
  }
}
