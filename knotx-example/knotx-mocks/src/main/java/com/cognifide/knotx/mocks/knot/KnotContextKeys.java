/*
 * Knot.x - Mocked services for sample app
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
package com.cognifide.knotx.mocks.knot;

import com.cognifide.knotx.dataobjects.KnotContext;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.file.AsyncFile;
import io.vertx.rxjava.core.file.FileSystem;
import rx.Observable;

enum KnotContextKeys {
  RESPONSE("clientResponse") {
    @Override
    Optional<Object> defaultValue(KnotContext context) {
      return Optional.of(new JsonObject().put("statusCode", 200));
    }
  },
  REQUEST("clientRequest") {
    @Override
    Optional<Object> defaultValue(KnotContext context) {
      return context.clientRequest() != null ? Optional.of(context.clientRequest().toJson()) : Optional.empty();
    }
  },
  FRAGMENTS("fragments") {
    @Override
    Optional<Object> defaultValue(KnotContext context) {
      return context.getFragments().isPresent() ? Optional.of(context.toJson().getJsonArray(this.key)) : Optional.empty();
    }
  },
  TRANSITION("transition") {
    @Override
    Observable<Optional<Object>> mockValue(FileSystem fileSystem, String mockConfigValue) {
      return Observable.just(Optional.of(mockConfigValue));
    }

    @Override
    Optional<Object> defaultValue(KnotContext context) {
      return Optional.empty();
    }
  };

  private final String key;

  KnotContextKeys(String key) {
    this.key = key;
  }

  String key() {
    return key;
  }

  Observable<Pair<String, Optional<Object>>> valueOrDefault(FileSystem fileSystem, JsonObject responseConfig, KnotContext context) {
    return Observable.just(key)
        .filter(responseConfig::containsKey)
        .flatMap(contextKey -> this.mockValue(fileSystem, responseConfig.getString(contextKey)))
        .map(value -> Pair.of(key, value))
        .defaultIfEmpty(Pair.of(key, this.defaultValue(context)));
  }

  Observable<Optional<Object>> mockValue(FileSystem fileSystem, String resourcePath) {
    return fileSystem.openObservable(resourcePath, new OpenOptions().setCreate(false).setWrite(false))
        .flatMap(this::processFile)
        .map(this::toJson);
  }

  abstract Optional<Object> defaultValue(KnotContext context);

  private Optional<Object> toJson(Buffer buffer) {
    return Optional.of(buffer.toString().trim().charAt(0) == '{' ? buffer.toJsonObject() : buffer.toJsonArray());
  }

  private Observable<Buffer> processFile(final AsyncFile asyncFile) {
    return Observable.just(Buffer.buffer())
        .mergeWith(asyncFile.toObservable())
        .reduce(Buffer::appendBuffer);
  }
}
