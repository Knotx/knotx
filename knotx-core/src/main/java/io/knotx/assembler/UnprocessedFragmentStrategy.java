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
package io.knotx.assembler;

import io.knotx.dataobjects.Fragment;


// TODO: Rename to FragmentsAssembleStrategy
public enum UnprocessedFragmentStrategy {

  AS_IS {
    @Override
    protected String get(Fragment fragment) {
      return fragment.content();
    }
  },

  IGNORE {
    @Override
    protected String get(Fragment fragment) {
      if (!fragment.isRaw() && fragment.unprocessed()) {
        return "<!-- SNIPPET IGNORED -->";
      } else {
        return fragment.content();
      }
    }
  };

  // TODO: Rename to getBody or extractBody
  protected abstract String get(Fragment fragment);

}
