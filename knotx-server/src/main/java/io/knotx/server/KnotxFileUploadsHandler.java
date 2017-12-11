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
package io.knotx.server;

import io.knotx.dataobjects.FileData;
import io.knotx.dataobjects.KnotContext;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnotxFileUploadsHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnotxFileUploadsHandler.class);

  @Override
  public void handle(RoutingContext context) {
    KnotContext knotContext = context.get(KnotContext.KEY);

    if (!context.fileUploads().isEmpty()) {
      final Set<FileData> uploadedFiles = context.fileUploads().stream()
          .map(FileData::new)
          .collect(Collectors.toSet());
      LOGGER.debug("{} file(s) uploaded, updating KnotContext", uploadedFiles.size());
      knotContext.setFilesData(uploadedFiles);

      context.put(KnotContext.KEY, knotContext);
    }

    context.next();
  }

  public static Handler<RoutingContext> create() {
    return new KnotxFileUploadsHandler();
  }
}
