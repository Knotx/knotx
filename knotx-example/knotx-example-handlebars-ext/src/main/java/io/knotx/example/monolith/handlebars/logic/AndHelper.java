package io.knotx.example.monolith.handlebars.logic;

import com.github.jknack.handlebars.Options;
import io.knotx.example.monolith.handlebars.ConditionalHelper;

import java.util.Arrays;

public class AndHelper extends ConditionalHelper {

    @Override
    public String getName() {
        return "and";
    }

    @Override
    protected Boolean checkCondition(Object firstParam, Options options) {
        return !options.isFalsy(firstParam) && Arrays.stream(options.params)
                .allMatch(param -> !options.isFalsy(param));
    }

}
