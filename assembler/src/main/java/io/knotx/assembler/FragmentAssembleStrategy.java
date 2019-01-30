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

import io.knotx.fragment.Fragment;

enum FragmentAssembleStrategy {

  AS_IS {
    @Override
    protected String extractBody(Fragment fragment) {
      return fragment.getBody();
    }
  },

  IGNORE {
    @Override
    protected String extractBody(Fragment fragment) {
      if (fragment.processed()) {
        return fragment.getBody();
      } else {
        return SNIPPET_IGNORED;
      }
    }
  };

  static final String SNIPPET_IGNORED = "<!-- SNIPPET IGNORED -->";

  protected abstract String extractBody(Fragment fragment);

}
