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

import com.cognifide.knotx.dataobjects.ClientResponse;

import org.junit.Test;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.rxjava.core.MultiMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ClientResponseCodecTest {

  private final static ClientResponseCodec codec = new ClientResponseCodec();

  private static final byte[] SAMPLE_BYTES = new byte[]{(byte) 0xe0, 0x4f, (byte) 0xff,
      (byte) 0xef, (byte) 0xea, 0x3a, 0x69, (byte) 0x7a, (byte) 0xa2, (byte) 0xd8, 0x08, 0x00, 0x2b,
      0x30, 0x30, (byte) 0x9d};

  @Test
  public void whenClientResponseEmptyEncoded_expectEmptyDecoded() {
    assertThat(encodeAndDecode(new ClientResponse()), equalTo(new ClientResponse()));
  }

  @Test
  public void whenClientResponseWithStatusEncoded_expectSameAfterDecoding() {
    ClientResponse subject = new ClientResponse().setStatusCode(HttpResponseStatus.MULTI_STATUS);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientResponseWithStatusAndSingleHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa");
    ClientResponse subject = new ClientResponse().setStatusCode(HttpResponseStatus.MULTI_STATUS).setHeaders(headers);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientResponseWithStatusAndMultipleHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("B", "bbbb");
    ClientResponse subject = new ClientResponse().setStatusCode(HttpResponseStatus.MULTI_STATUS).setHeaders(headers);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientResponseWithStatusAndSingleListHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "xxxx");
    ClientResponse subject = new ClientResponse().setStatusCode(HttpResponseStatus.MULTI_STATUS).setHeaders(headers);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenClientResponseWithStatusAndMultipleListHeaderEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "xxxx").add("B", "yyyy").add("B", "zzzz").add("C", "uuuu");
    ClientResponse subject = new ClientResponse().setStatusCode(HttpResponseStatus.MULTI_STATUS).setHeaders(headers);
    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenFullClientResponseEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa");
    ClientResponse subject = new ClientResponse()
        .setStatusCode(HttpResponseStatus.MULTI_STATUS)
        .setHeaders(headers)
        .setBody(io.vertx.rxjava.core.buffer.Buffer.buffer("body-value"));

    assertThat(encodeAndDecode(subject), equalTo(subject));
  }

  @Test
  public void whenFullClientResponseWithBinaryBodyEncoded_expectSameAfterDecoding() {
    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("A", "aaaa").add("A", "cccc").add("B", "xyz");
    ClientResponse subject = new ClientResponse()
        .setStatusCode(HttpResponseStatus.MULTI_STATUS)
        .setHeaders(headers)
        .setBody(new io.vertx.rxjava.core.buffer.Buffer(Buffer.buffer(SAMPLE_BYTES)));

    assertThat(encodeAndDecode(subject), equalTo(subject));
  }


  private ClientResponse encodeAndDecode(ClientResponse input) {
    Buffer wire = Buffer.buffer();
    codec.encodeToWire(wire, input);
    return codec.decodeFromWire(0, wire);
  }
}
