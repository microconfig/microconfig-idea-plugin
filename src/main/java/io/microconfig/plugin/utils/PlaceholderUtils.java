package io.microconfig.plugin.utils;

import java.util.Optional;

import static java.util.Optional.empty;

public class PlaceholderUtils {

    public static String placeholderSubstring(String line, int offset) {
        return insideBrackets(line, offset).orElse(line);
    }

    private static Optional<String> insideBrackets(String line, int offset) {
        int previousStart = line.substring(0, offset + 1).lastIndexOf("${");
        int previousEnd = line.substring(0, offset + 1).lastIndexOf('}');
        if (previousEnd > 0 && previousEnd > previousStart) return empty();

        int nextEnd = line.indexOf('}', offset);

        if (previousStart > 0 && nextEnd > 0) return Optional.of(line.substring(previousStart + 2, nextEnd));

        return empty();
    }

}
