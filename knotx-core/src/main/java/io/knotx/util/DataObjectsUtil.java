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
package io.knotx.util;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import io.vertx.reactivex.core.MultiMap;
import java.util.Arrays;

public final class DataObjectsUtil {

  private DataObjectsUtil() {
    //util constructor
  }

  /**
   * Equality operator for MultiMap objects.
   * MultiMap represents keys as String, values as List of Strings.
   *
   * @param self - compared {@link MultiMap} object self
   * @param that - compared {@link MultiMap} object that
   * @return - true if objects are equals, false otherwise
   */
  public static boolean equalsMultiMap(MultiMap self, MultiMap that) {
    return Objects.equal(self.names(), that.names()) &&
        self.names().stream()
            .allMatch(name -> that.contains(name) && self.getAll(name).equals(that.getAll(name)));
  }

  /**
   * Equality operator for Buffer objects. It assumes the buffer might contain binary data
   *
   * @param self - compared Buffer object self
   * @param that - compared Buffer object that
   * @return - true if objects are equals, false otherwise
   */
  public static boolean equalsBody(io.vertx.core.buffer.Buffer self,
      io.vertx.core.buffer.Buffer that) {
    if (self == that) {
      return true;
    }
    if (self != null) {
      if (that != null) {
        return Arrays
            .equals(self.getBytes(), that.getBytes());
      } else {
        return false;
      }
    }
    return false;
  }

  /**
   * Method computing hashCode of the give MultiMap.
   * MultiMap is treated as a list of key-value pairs where each key is a String and
   * the corresponding value is a List of Strings.
   *
   * @param multiMap - object to compute hashcode from
   * @return - hashcode of the given {@link MultiMap} object
   */
  public static int multiMapHash(MultiMap multiMap) {
    return multiMap.getDelegate().entries().stream().mapToInt(
        entry -> 31 * entry.getKey().hashCode() + (entry.getValue() == null ? 0
            : entry.getValue().hashCode())
    ).reduce(0, (sum, hash) -> 41 * sum + hash);
  }

  /**
   * toString() implementation for MultiMap object
   *
   * @param multiMap - {@link MultiMap}
   * @return String representing given MultiMap
   */
  public static String toString(MultiMap multiMap) {
    StringBuilder result = new StringBuilder();
    multiMap.names().stream().forEach(
        name -> result.append(name).append(":").append(Joiner.on(";").join(multiMap.getAll(name)))
            .append("|")
    );

    return result.toString();
  }
}
