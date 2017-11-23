package io.knotx.example.monolith.handlebars.math;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;

import java.io.IOException;

/**
 * Checks if value1 is greater or equal to value2.<br>
 * Usage:
 * <pre>
 *     {{#ge value1 value2}}
 *         greater or equal
 *     {{else}}
 *         less
 *     {{/ge}}
 * </pre>
 */
public class GreatherEqualHelper implements CustomHandlebarsHelper<Integer> {
    @Override
    public String getName() {
        return "ge";
    }

    @Override
    public Object apply(Integer firstParam, Options options) throws IOException {
        Integer secondParam = options.param(0);
        return firstParam >= secondParam ? options.fn() : options.inverse();
    }
}
