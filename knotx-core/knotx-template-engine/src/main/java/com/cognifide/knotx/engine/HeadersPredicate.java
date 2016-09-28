package com.cognifide.knotx.engine;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public final class HeadersPredicate {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadersPredicate.class);

    private static final String WILDCARD = "*";

    private HeadersPredicate(){
        //to prevent initialization
    }

    public static Predicate<String> shouldBePreserved(String header) {
        return allowedHeader -> {
            if (allowedHeader.contains(WILDCARD)) {
                Pattern compiledPattern = getPattern(allowedHeader);
                return compiledPattern.matcher(header).matches();
            } else {
                return allowedHeader.equals(header);
            }
        };
    }

    private static Pattern getPattern(String allowedHeader) {
        String pattern = "^" + allowedHeader.replace(WILDCARD, "(.+)") + "$";
        Pattern compiledPattern;
        try{
            compiledPattern = Pattern.compile(pattern);
        }catch (PatternSyntaxException e){
            LOGGER.error("Invalid allowed headers configuration syntax: " + allowedHeader, e);
            throw new RuntimeException("Application error");
        }
        return compiledPattern;
    }
}
