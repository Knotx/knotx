package io.knotx.example.monolith.handlebars.arrays;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;
import io.vertx.core.json.JsonArray;

import java.io.IOException;


/**
 * Get i-th element of json array.<br>
 * Usage:
 * <pre>
 *     {{element array i}}
 * </pre>
 */
public class ElementHelper implements CustomHandlebarsHelper {
  @Override
  public String getName() {
    return "element";
  }

  @Override
  public Object apply(Object firstParam, Options options) throws IOException {
    int index = options.param(0);
    if (firstParam instanceof JsonArray) {
      return ((JsonArray) firstParam).getValue(index);
    }
    return null;
  }
}
