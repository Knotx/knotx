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
package com.cognifide.knotx.dataobjects;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.rxjava.core.MultiMap;

public abstract class Codec {

  /**
   * Encode to Wire method that message object class need to implement.
   * It serializes object into the buffer in order to send it through event bus
   *
   * @param buffer - wire buffer
   */
  abstract public void encodeToWire(io.vertx.core.buffer.Buffer buffer);

  /**
   * Decodes buffer into the message object
   *
   * @param pos    - buffer index
   * @param buffer - wire buffer
   */
  abstract public void decodeFromWire(AtomicInteger pos, io.vertx.core.buffer.Buffer buffer);

  /**
   * Encodes Integer value into the wire buffer
   *
   * @param wireBuffer - wire buffer
   * @param value      - encoded value
   */
  protected void encodeInt(Buffer wireBuffer, int value) {
    wireBuffer.appendInt(value);
  }

  /**
   * Decodes Integer from Buffer wire
   *
   * @param pos    - position in the buffer
   * @param buffer - wire buffer
   * @return Decoded Integer value
   */
  protected Integer decodeInt(AtomicInteger pos, Buffer buffer) {
    Integer result = buffer.getInt(pos.get());
    pos.addAndGet(4);
    return result;
  }

  /**
   * Encodes String into the buffer wire
   *
   * @param wireBuffer - buffer wire
   * @param value      - value to be encoded
   */
  protected void encodeString(Buffer wireBuffer, String value) {
    if (value != null) {
      byte[] strBytes = value.getBytes(CharsetUtil.UTF_8);
      wireBuffer.appendInt(strBytes.length);
      wireBuffer.appendBytes(strBytes);
    } else {
      wireBuffer.appendInt(0);
    }
  }

  /**
   * Decodes buffer into the String
   *
   * @param pos    - position in the buffer
   * @param buffer - wire buffer
   * @return Decoded String value
   */
  protected String decodeString(AtomicInteger pos, Buffer buffer) {
    String result = null;
    int len = buffer.getInt(pos.get());
    pos.addAndGet(4);
    if (len > 0) {
      byte[] bytes = buffer.getBytes(pos.get(), pos.get() + len);
      result = new String(bytes, CharsetUtil.UTF_8);
      pos.addAndGet(len);
    }
    return result;
  }

  /**
   * Encodes Buffer into the buffer wire
   *
   * @param wireBuffer - buffer wire
   * @param value      - value to be encoded
   */
  protected void encodeBuffer(Buffer wireBuffer, io.vertx.rxjava.core.buffer.Buffer value) {
    if (value != null) {
      wireBuffer.appendInt(value.length()).appendBuffer((Buffer) value.getDelegate());
    } else {
      wireBuffer.appendInt(0);
    }
  }

  /**
   * Decodes wire buffer into the Buffer object
   *
   * @param pos    - position in the wire buffer
   * @param buffer - wire buffer
   * @return Decoded Buffer value
   */
  protected io.vertx.rxjava.core.buffer.Buffer decodeBuffer(AtomicInteger pos, Buffer buffer) {
    io.vertx.rxjava.core.buffer.Buffer result = null;

    int length = buffer.getInt(pos.get());
    pos.addAndGet(4);
    if (length > 0) {
      result = new io.vertx.rxjava.core.buffer.Buffer(buffer.getBuffer(pos.get(), pos.addAndGet(length)));
    }

    return result;
  }

  /**
   * Encodes Multimap into the buffer wire
   * The Multimap is going to be encoded as following elements of data<br/>
   * keysAmount|key1Length|key1Value|valuesAmount|value1Length|value1|value2Length|value2|..|key2Length|key2Value|...
   *
   * @param wireBuffer - buffer wire
   * @param multiMap   - Multimap object to be encoded
   */
  protected void encodeMultiMap(final Buffer wireBuffer, final MultiMap multiMap) {
    Set<String> names = multiMap.names();
    wireBuffer.appendInt(names.size());

    names.stream().forEach(
        name -> {
          List<String> values = multiMap.getAll(name);
          encodeString(wireBuffer, name);
          wireBuffer.appendInt(values.size());

          values.forEach(
              value -> encodeString(wireBuffer, value)
          );
        }
    );
  }

  /**
   * Decodes wire buffer into the Multimap object
   *
   * @param pos    - position in the wire buffer
   * @param buffer - wire buffer
   * @return Decoded Multimap value
   */
  protected MultiMap decodeMultiMap(AtomicInteger pos, Buffer buffer) {
    MultiMap result = MultiMap.caseInsensitiveMultiMap();
    int namesAmount = buffer.getInt(pos.get());
    pos.addAndGet(4);

    if (namesAmount > 0) {
      for (int nameIdx = namesAmount; nameIdx > 0; nameIdx--) {
        String name = decodeString(pos, buffer);
        int valuesAmount = buffer.getInt(pos.get());
        pos.addAndGet(4);
        for (int valIdx = valuesAmount; valIdx > 0; valIdx--) {
          result.add(name, decodeString(pos, buffer));
        }
      }
    }
    return result;
  }

  /**
   * Equality operator for Multimap objects
   *
   * @param self - compared Multimap object self
   * @param that - compared Multimap object that
   * @return - true if objects are equals, false otherwise
   */
  protected boolean equalsMultimap(MultiMap self, MultiMap that) {
    return Objects.equal(self.names(), that.names()) &&
        self.names().stream().allMatch(name -> that.contains(name) && self.getAll(name).equals(that.getAll(name)));
  }

  /**
   * Equality operator for Buffer objects. It assues the buffer might contain binary data
   *
   * @param self - compared Buffer object self
   * @param that - compared Buffer object that
   * @return - true if objects are equals, false otherwise
   */
  protected boolean equalsBody(io.vertx.rxjava.core.buffer.Buffer self, io.vertx.rxjava.core.buffer.Buffer that) {
    if (self == that) return true;
    if (self != null) {
      if (that != null) {
        return Arrays.equals(((io.vertx.core.buffer.Buffer) self.getDelegate()).getBytes(), ((io.vertx.core.buffer.Buffer) that.getDelegate()).getBytes());
      } else {
        return false;
      }
    }
    return false;
  }

  /**
   * Method computing hashCode of the give multimap
   *
   * @param multiMap - object to compute hashcode from
   * @return - hashcode of the given Multimap object
   */
  protected int multiMapHash(MultiMap multiMap) {
    return multiMap.names().stream()
        .mapToInt(name -> Optional.ofNullable(multiMap.get(name))
            .map(String::hashCode)
            .orElse(0))
        .reduce(new Integer(0), (sum, hash) -> 31 * sum + hash);
  }

  /**
   * toString() implementation for Multimap object
   *
   * @return String representing given Multimap
   */
  protected String toString(MultiMap multiMap) {
    StringBuilder result = new StringBuilder();
    multiMap.names().stream().forEach(
        name -> result.append(name).append(":").append(Joiner.on(";").join(multiMap.getAll(name))).append("|")
    );

    return result.toString();
  }
}
