package io.knotx.example.monolith.handlebars.uri;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Decodes given value.<br>
 * Usage:
 * <pre>
 *     {{decode_uri value}}
 * </pre>
 * If value is "http%3A%2F%2Fexample.com", the output will be "http://example.com"
 */
public class DecodeHelper implements CustomHandlebarsHelper<Object> {
  @Override
  public String getName() {
    return "decode_uri";
  }

  @Override
  public Object apply(Object value, Options options) throws IOException {
    Options.Buffer buffer = options.buffer();
    buffer.append(URLDecoder.decode(String.valueOf(value), StandardCharsets.UTF_8.name()));
    return buffer;
  }
}
