package io.microconfig.plugin.utils;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

import static java.util.Optional.empty;

public class PlaceholderUtils {
    public static String placeholderSubstring(String line, int offset) {
        return valueInsideBrackets(line, offset).orElse(line);
    }

    private static Optional<String> valueInsideBrackets(String line, int offset) {
        return placeholderBorders(line, offset)
            .map(sE -> line.substring(sE.getLeft() + 2, sE.getRight()));
    }

    public static boolean insidePlaceholderBrackets(String line, int offset) {
        return placeholderBorders(line, offset).isPresent();
    }

    static Optional<Pair<Integer, Integer>> placeholderBorders(String line, int offset) {
        int previousStart = line.substring(0, offset + 1).lastIndexOf("${");
        int previousEnd = line.substring(0, offset + 1).lastIndexOf('}');
        if (previousEnd > 0 && previousEnd > previousStart) return empty();

        int nextEnd = line.indexOf('}', offset);
        return nextEnd > 0 ? Optional.of(Pair.of(previousStart, nextEnd)) : empty();
    }
}