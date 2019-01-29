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
package io.knotx.junit.converter;

import static org.mockito.Mockito.when;

import io.knotx.dataobjects.SnippetFragment;
import io.knotx.junit5.util.FileReader;
import java.io.IOException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.mockito.Mockito;

/**
 * Simplifies argument type conversion for parameterized tests (these annotated with {@linkplain
 * ParameterizedTest})
 */
public class FragmentArgumentConverter extends SimpleArgumentConverter {

  private static final String PARAMETER_SEPARATOR_REGEX = "\\|";

  @Override
  protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
    if (!String.class.equals(source.getClass())) {
      throw new ArgumentConversionException("This converter supports only String as source object");
    }

    String toConvert = String.valueOf(source);

    if (targetType.equals(SnippetFragment.class)) {
      try {
        return createFragmentMock(toConvert);
      } catch (IOException e) {
        throw new ArgumentConversionException("Exception thrown during conversion", e);
      }
    }

    throw new ArgumentConversionException(
        "Unsupported object type for conversion: " + targetType.toString());
  }

  /**
   * Builds mock of {@link SnippetFragment} from provided fragmentParameters. The only
   * mandatory value of the fragmentParameters is fragment content file, that will be the fragment
   * content. There 2 additional params:
   *
   * <ul>
   * <li>custom snippet tag name (default value is <bb>script</bb>)
   * <li>custom snippet parameters prefix (default value is <bb>data-knotx-</bb>)
   * </ul>
   *
   * You can embed them in the fragmentParameters, separated by <bb>|</bb> character.<br>
   * <br>
   * <i>Example:</i> "content.txt|knotx:snippet|data-knotx-" will produce SnippetFragment mock with
   * content read from content.txt, snippet tag name set to "knotx:snippet" and snippet parameters
   * prefix set to "data-knotx-". If the last character of fragmentParameters is the separator
   * <bb>|</bb>, then snippet parameters prefix will be set to empty string (no prefix).
   */
  static SnippetFragment createFragmentMock(String fragmentParameters) throws IOException {
    final String[] params = fragmentParameters.split(PARAMETER_SEPARATOR_REGEX);
    final String fragmentContentFile = params[0];
    final String fragmentContent = FileReader.readText(fragmentContentFile);

    SnippetFragment fragmentMock = Mockito.mock(SnippetFragment.class);
    when(fragmentMock.content()).thenReturn(fragmentContent);
    return fragmentMock;
  }

}
