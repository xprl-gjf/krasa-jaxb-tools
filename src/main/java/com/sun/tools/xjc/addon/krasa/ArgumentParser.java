package com.sun.tools.xjc.addon.krasa;

import java.util.Optional;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class ArgumentParser {
    private final String arg;
    private int counter;

    public ArgumentParser(String arg) {
        this.arg = arg;
    }

    public int getCounter() {
        return counter;
    }
    
    public Optional<String> extractString(String paramName) {
        return extractArgument(paramName, v -> v);
    }
    
    public Optional<Boolean> extractBoolean(String paramName) {
        return extractArgument(paramName, v -> Boolean.parseBoolean(v));
    }
    
    private <T> Optional<T> extractArgument(String paramName,
            Function<String,T> convert) {
        int idx = arg.indexOf(paramName);
        if (idx > 0) {
            counter++;
            final String value = arg.substring(idx + paramName.length() + 1);
            return Optional.of(convert.apply(value));
        }
        return Optional.empty();
    }
    
}
