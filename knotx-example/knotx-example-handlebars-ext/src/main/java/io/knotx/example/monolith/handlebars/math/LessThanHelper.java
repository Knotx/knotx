package io.knotx.example.monolith.handlebars.math;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;

import java.io.IOException;

/**
 * Checks if value1 is smaller than value2.<br>
 * Usage:
 * <pre>
 *     {{#lt value1 value2}}
 *          smaller
 *     {{else}}
 *         greater or equal
 *     {{/lt}}
 * </pre>
 */
public class LessThanHelper implements CustomHandlebarsHelper<Integer> {
    @Override
    public String getName() {
        return "lt";
    }

    @Override
    public Object apply(Integer firstParam, Options options) throws IOException {
        int secondParam = options.param(0);
        return firstParam < secondParam ? options.fn() : options.inverse();
    }
}
