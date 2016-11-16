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

import com.cognifide.knotx.dataobjects.ClientRequest;

import org.junit.Test;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.MultiMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ClientRequestCodecTest {

  private final static ClientRequestCodec codec = new ClientRequestCodec();

  @Test
  public void whenClientRequestEmptyEncoded_expectEmptyDecoded() {
    assertThat(encodeAndDecode(new ClientRequest()), equalTo(new ClientRequest()));
  }

  @Test
  public void whenClientRequestWithPathEncoded_expectSameAfterDecoding() {
    ClientRequest subject = new ClientRequest().setPath("/some/path");
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithMethodEncoded_expectSameAfterDecoding() {
    ClientRequest subject = new ClientRequest().setMethod(HttpMethod.OPTIONS);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithPathMethodEncoded_expectSameAfterDecoding() {
    ClientRequest subject = new ClientRequest().setPath("/some/path").setMethod(HttpMethod.OPTIONS);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithPathMethodAndSingleHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa");
    ClientRequest subject = new ClientRequest().setPath("/some/path").setMethod(HttpMethod.OPTIONS).setHeaders(headers);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithPathAndHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa");
    ClientRequest subject = new ClientRequest().setPath("/some/path").setHeaders(headers);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithPathMethodAndMultipleHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("B", "bbbb");
    ClientRequest subject = new ClientRequest().setPath("/some/path").setMethod(HttpMethod.OPTIONS).setHeaders(headers);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithPathAndMultipleHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("B", "bbbb");
    ClientRequest subject = new ClientRequest().setPath("/some/path").setHeaders(headers);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithPathMethodAndSingleListHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "xxxx");
    ClientRequest subject = new ClientRequest().setPath("/some/path").setMethod(HttpMethod.OPTIONS).setHeaders(headers);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithPathMethodAndMultipleListHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "xxxx").add("B", "yyyy").add("B", "zzzz").add("C", "uuuu");
    ClientRequest subject = new ClientRequest().setPath("/some/path").setMethod(HttpMethod.OPTIONS).setHeaders(headers);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithPathMethodParamsAndMultipleListHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "xxxx").add("B", "yyyy").add("B", "zzzz").add("C", "uuuu");
    MultiMap params = MultiMap.caseInsensitiveMultiMap().add("AP", "aaaa").add("AP", "xxxx").add("BP", "yyyy").add("BP", "zzzz").add("CP", "uuuu");
    ClientRequest subject = new ClientRequest().setPath("/some/path").setMethod(HttpMethod.OPTIONS).setHeaders(headers).setParams(params);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithPathMethodParamsFormAndMultipleListHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "xxxx").add("B", "yyyy").add("B", "zzzz").add("C", "uuuu");
    MultiMap params = MultiMap.caseInsensitiveMultiMap().add("AP", "aaaa").add("AP", "xxxx").add("BP", "yyyy").add("BP", "zzzz").add("CP", "uuuu");
    MultiMap form = MultiMap.caseInsensitiveMultiMap().add("APF", "aaaa").add("APF", "xxxx").add("BPF", "yyyy").add("BPF", "zzzz").add("CPF", "uuuu");
    ClientRequest subject = new ClientRequest().setPath("/some/path").setMethod(HttpMethod.OPTIONS).setHeaders(headers).setParams(params).setFormAttributes(form);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithPathMethodFormAndMultipleListHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "xxxx").add("B", "yyyy").add("B", "zzzz").add("C", "uuuu");
    MultiMap form = MultiMap.caseInsensitiveMultiMap().add("APF", "aaaa").add("APF", "xxxx").add("BPF", "yyyy").add("BPF", "zzzz").add("CPF", "uuuu");
    ClientRequest subject = new ClientRequest().setPath("/some/path").setMethod(HttpMethod.OPTIONS).setHeaders(headers).setFormAttributes(form);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientRequestWithPathMethodFormEncoded_expectSameAfterDecoding() {
    MultiMap form = MultiMap.caseInsensitiveMultiMap().add("APF", "aaaa").add("APF", "xxxx").add("BPF", "yyyy").add("BPF", "zzzz").add("CPF", "uuuu");
    ClientRequest subject = new ClientRequest().setPath("/some/path").setMethod(HttpMethod.OPTIONS).setFormAttributes(form);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }


  @Test
  public void whenFullClientRequestEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "xxxx").add("B", "yyyy").add("B", "zzzz").add("C", "uuuu");
    MultiMap params = MultiMap.caseInsensitiveMultiMap().add("AP", "aaaa").add("AP", "xxxx").add("BP", "yyyy").add("BP", "zzzz").add("CP", "uuuu");
    MultiMap form = MultiMap.caseInsensitiveMultiMap().add("APF", "aaaa").add("APF", "xxxx").add("BPF", "yyyy").add("BPF", "zzzz").add("CPF", "uuuu");
    ClientRequest subject = new ClientRequest()
        .setPath("/some/path")
        .setMethod(HttpMethod.CONNECT)
        .setHeaders(headers)
        .setParams(params)
        .setFormAttributes(form);

    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  private ClientRequest encodeAndDecode(ClientRequest input) {
    Buffer wire = Buffer.buffer();
    codec.encodeToWire(wire, input);
    return codec.decodeFromWire(0, wire);
  }
}
