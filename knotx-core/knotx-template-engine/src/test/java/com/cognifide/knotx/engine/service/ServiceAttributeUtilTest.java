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

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class ServiceAttributeUtilTest {

  @Test
  public void testAttributeWithoutNamespaceAndMethodType() throws Exception {
    String attributeInput = "data-uri";
    String namespace = ServiceAttributeUtil.extractNamespace(attributeInput);
    ServiceCallMethod serviceCallMethod = ServiceAttributeUtil.extractMethodType(attributeInput);

    Assert.assertThat(namespace, is(StringUtils.EMPTY));
    Assert.assertThat(serviceCallMethod, is(ServiceCallMethod.ALL));
  }

  @Test
  public void testAttributeWithNamespaceAndWithoutMethodType() throws Exception {
    String attributeInput = "data-uri-label";
    String namespace = ServiceAttributeUtil.extractNamespace(attributeInput);
    ServiceCallMethod serviceCallMethod = ServiceAttributeUtil.extractMethodType(attributeInput);

    Assert.assertThat(namespace, is("label"));
    Assert.assertThat(serviceCallMethod, is(ServiceCallMethod.ALL));

  }

  @Test
  public void testAttributeWithNamespaceAndMethodTypeAll() throws Exception {
    String attributeInput = "data-uri-all-label";
    String namespace = ServiceAttributeUtil.extractNamespace(attributeInput);
    ServiceCallMethod serviceCallMethod = ServiceAttributeUtil.extractMethodType(attributeInput);
    Assert.assertThat(namespace, is("label"));
    Assert.assertThat(serviceCallMethod, is(ServiceCallMethod.ALL));
  }

  @Test
  public void testAttributeWithNamespaceAndMethodTypePOST() throws Exception {
    String attributeInput = "data-uri-post-label";
    String namespace = ServiceAttributeUtil.extractNamespace(attributeInput);
    ServiceCallMethod serviceCallMethod = ServiceAttributeUtil.extractMethodType(attributeInput);
    Assert.assertThat(namespace, is("label"));
    Assert.assertThat(serviceCallMethod, is(ServiceCallMethod.POST));
  }


  @Test(expected = RuntimeException.class)
  public void testAttributeWithTwoNamespaces() throws Exception {
    String attributeInput = "data-uri-message-label";
    ServiceAttributeUtil.extractNamespace(attributeInput);
  }

  @Test
  public void testAttributeWithMethodTypeAndWithoutNamespace() throws Exception {
    String attributeInput = "data-uri-get";

    String namespace = ServiceAttributeUtil.extractNamespace(attributeInput);
    ServiceCallMethod serviceCallMethod = ServiceAttributeUtil.extractMethodType(attributeInput);

    Assert.assertThat(namespace, is(StringUtils.EMPTY));
    Assert.assertThat(serviceCallMethod, is(ServiceCallMethod.GET));
  }

  @Test(expected = RuntimeException.class)
  public void testAttributeWithBrokenNamespace() throws Exception {
    String attributeInput = "data-uri--label";
    ServiceAttributeUtil.extractNamespace(attributeInput);
  }

}
