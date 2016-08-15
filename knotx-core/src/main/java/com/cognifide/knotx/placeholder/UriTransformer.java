/*
 * Knot.x - Reactive microservice assembler
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.cognifide.knotx.placeholder;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;

@Component
public class UriTransformer {

    public String getServiceUrl(Element snippet, UriPlaceholderResolver resolver) {
        final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");
        final String templateCallUri = snippet.attr("data-call-uri");
        return helper.replacePlaceholders(templateCallUri, resolver);
    }

}
