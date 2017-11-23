package io.knotx.example.monolith.handlebars.math;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;

import java.io.IOException;

/**
 * Checks if value1 is smaller or equal to value2.<br>
 * Usage:
 * <pre>
 *     {{#le value1 value2}}
 *          smaller or equal
 *     {{else}}
 *         greater
 *     {{/le}}
 * </pre>
 */
public class LessEqualHelper implements CustomHandlebarsHelper<Integer> {
    @Override
    public String getName() {
        return "le";
    }

    @Override
    public Object apply(Integer firstParam, Options options) throws IOException {
        int secondParam = options.param(0);
        return firstParam <= secondParam ? options.fn() : options.inverse();
    }
}
