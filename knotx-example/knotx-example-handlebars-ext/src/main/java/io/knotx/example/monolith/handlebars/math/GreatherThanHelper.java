package io.knotx.example.monolith.handlebars.math;

import com.github.jknack.handlebars.Options;
import io.knotx.knot.templating.handlebars.CustomHandlebarsHelper;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

/**
 * Checks if value1 is greater than value2.<br>
 * Usage:
 * <pre>
 *     {{#gt value1 value2}}
 *         greater
 *     {{else}}
 *         equal or less
 *     {{/gt}}
 * </pre>
 */
public class GreatherThanHelper implements CustomHandlebarsHelper<Integer> {
    @Override
    public String getName() {
        return "gt";
    }

    @Override
    public Object apply(Integer firstParam, Options options) throws IOException {
        Integer secondParam = options.param(0);
        return firstParam > secondParam ? options.fn() : options.inverse();
    }
}
