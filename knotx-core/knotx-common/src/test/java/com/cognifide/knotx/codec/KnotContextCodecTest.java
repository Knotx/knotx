/*
 * Knot.x - Reactive microservice assembler - API
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
package com.cognifide.knotx.codec;

import com.google.common.collect.Lists;

import com.cognifide.knotx.dataobjects.ClientRequest;
import com.cognifide.knotx.dataobjects.ClientResponse;
import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;

import org.junit.Test;

import java.util.List;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class KnotContextCodecTest {

  private static final byte[] SAMPLE_BYTES = new byte[]{(byte) 0xe0, 0x4f, (byte) 0xff,
      (byte) 0xef, (byte) 0xea, 0x3a, 0x69, (byte) 0x7a, (byte) 0xa2, (byte) 0xd8, 0x08, 0x00, 0x2b,
      0x30, 0x30, (byte) 0x9d};
  public KnotContextCodec codec = new KnotContextCodec();

  @Test
  public void whenEmptyKnotContextEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext();
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenKnotContextWithRequestOnlyEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext().setClientRequest(fullRequest());
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenKnotContextWithResponseBinaryOnlyEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext().setClientResponse(fullResponseBinary());
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenKnotContextWithResponseTextOnlyEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext().setClientResponse(fullResponseText());
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenKnotContextWithRequestAndResponseTextOnlyEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext().setClientRequest(fullRequest()).setClientResponse(fullResponseText());
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenKnotContextWithTransitionEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext().setTransition("sample-transition");
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenKnotContextWithTransitionAndRequestEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext().setTransition("sample-transition").setClientRequest(fullRequest());
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenKnotContextWithTransitionRequestAndResponseEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext().setTransition("sample-transition").setClientRequest(fullRequest()).setClientResponse(fullResponseText());
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenKnotContextWithFragmentsEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext().setFragments(fragments());
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenKnotContextWithTransitionFragmentsEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext().setTransition("sample-transition").setFragments(fragments());
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenKnotContextWithTransitionRequestFragmentsEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext().setTransition("sample-transition").setClientRequest(fullRequest()).setFragments(fragments());
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenKnotContextFullEncoded_expectSameAfterDecoding() {
    KnotContext subject = new KnotContext()
        .setTransition("sample-transition")
        .setClientRequest(fullRequest())
        .setClientResponse(fullResponseText())
        .setFragments(fragments());
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  private KnotContext encodeAndDecode(KnotContext input) {
    Buffer wire = Buffer.buffer();
    codec.encodeToWire(wire, input);
    return codec.decodeFromWire(0, wire);
  }

  private ClientRequest fullRequest() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "xxxx").add("B", "yyyy").add("B", "zzzz").add("C", "uuuu");
    MultiMap params = MultiMap.caseInsensitiveMultiMap().add("AP", "aaaa").add("AP", "xxxx").add("BP", "yyyy").add("BP", "zzzz").add("CP", "uuuu");
    MultiMap form = MultiMap.caseInsensitiveMultiMap().add("APF", "aaaa").add("APF", "xxxx").add("BPF", "yyyy").add("BPF", "zzzz").add("CPF", "uuuu");
    return new ClientRequest().setPath("/some/path").setMethod(HttpMethod.OPTIONS).setHeaders(headers).setParams(params).setFormAttributes(form);
  }

  private ClientResponse fullResponseText() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "xxxx").add("C", "cccc");
    return new ClientResponse()
        .setStatusCode(HttpResponseStatus.MULTI_STATUS)
        .setHeaders(headers)
        .setBody(io.vertx.rxjava.core.buffer.Buffer.buffer("body-value"));
  }

  private ClientResponse fullResponseBinary() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "cccc").add("B", "xyz");
    return new ClientResponse()
        .setStatusCode(HttpResponseStatus.MULTI_STATUS)
        .setHeaders(headers)
        .setBody(new io.vertx.rxjava.core.buffer.Buffer(Buffer.buffer(SAMPLE_BYTES)));
  }

  private List<Fragment> fragments() {
    List<Fragment> fragments = Lists.newArrayList();

    Fragment withContext = Fragment.snippet("templating", "TEMPLATING 1");
    withContext.getContext().put("ABC", new JsonObject().put("aa", "bb").put("cc", "dd"));

    fragments.add(Fragment.raw("RAW-FRAGMENT 1"));
    fragments.add(Fragment.raw("RAW-FRAGMENT 2"));
    fragments.add(Fragment.snippet("templating", "TEMPLATING 1"));
    fragments.add(Fragment.snippet("form-1", "FORM-1"));
    fragments.add(Fragment.snippet("form-1", "FORM-1"));
    fragments.add(withContext);

    return fragments;
  }
}
