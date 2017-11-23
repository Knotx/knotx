package io.knotx.example.monolith.handlebars.math;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;

import java.io.IOException;

/**
 * Checks if value1 and value2 are equal.<br>
 * Usage:
 * <pre>
 *     {{#eq value1 value2}}
 *         equal
 *     {{else}}
 *         not equal
 *     {{/eq}}
 * </pre>
 */
public class EqualsHelper implements CustomHandlebarsHelper<Integer> {

    @Override
    public String getName() {
        return "eq";
    }

    @Override
    public Object apply(Integer firstParam, Options options) throws IOException {
        Integer secondParam = options.param(0);
        return firstParam == secondParam ? options.fn() : options.inverse();
    }
}
