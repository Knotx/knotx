package io.knotx.example.monolith.handlebars;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;

import java.io.IOException;

public abstract class ConditionalHelper implements CustomHandlebarsHelper<Object> {

  @Override
  public CharSequence apply(Object firstParam, Options options) throws IOException {

    Options.Buffer buffer = options.buffer();
    if (checkCondition(firstParam, options)) {
      buffer.append(options.fn());
    } else {
      buffer.append(options.inverse());
    }
    return buffer;
  }

  protected abstract Boolean checkCondition(Object firstParam, Options options);
}
