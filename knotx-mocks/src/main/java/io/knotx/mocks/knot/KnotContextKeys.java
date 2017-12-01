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
package io.knotx.mocks.knot;

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.KnotContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.file.AsyncFile;
import io.vertx.reactivex.core.file.FileSystem;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

enum KnotContextKeys {
  RESPONSE("clientResponse") {
    @Override
    Optional<Object> defaultValue(KnotContext context) {
      return Optional.of(new ClientResponse().setStatusCode(HttpResponseStatus.OK.code()));
    }
  },
  REQUEST("clientRequest") {
    @Override
    Optional<Object> defaultValue(KnotContext context) {
      return context.getClientRequest() != null ? Optional.of(context.getClientRequest())
          : Optional.empty();
    }
  },
  FRAGMENTS("fragments") {
    @Override
    Optional<Object> defaultValue(KnotContext context) {
      return Optional.ofNullable(context.getFragments());
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

  Observable<Pair<String, Optional<Object>>> valueOrDefault(FileSystem fileSystem,
      JsonObject responseConfig, KnotContext context) {
    return Observable.just(key)
        .filter(responseConfig::containsKey)
        .flatMap(contextKey -> this.mockValue(fileSystem, responseConfig.getString(contextKey)))
        .map(value -> Pair.of(key, value))
        .defaultIfEmpty(Pair.of(key, this.defaultValue(context)));
  }

  Observable<Optional<Object>> mockValue(FileSystem fileSystem, String resourcePath) {
    return fileSystem
        .rxOpen(resourcePath, new OpenOptions().setCreate(false).setWrite(false))
        .flatMap(this::processFile)
        .map(this::toJson)
        .toObservable();
  }

  abstract Optional<Object> defaultValue(KnotContext context);

  private Optional<Object> toJson(Buffer buffer) {
    return Optional.of(buffer.toString().trim().charAt(0) == '{' ? buffer.toJsonObject()
        : buffer.toJsonArray());
  }

  private Single<Buffer> processFile(final AsyncFile asyncFile) {
    return Observable.just(Buffer.buffer())
        .mergeWith(asyncFile.toObservable())
        .reduce(Buffer::appendBuffer)
        .toSingle();
  }
}
