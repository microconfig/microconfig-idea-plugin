package io.microconfig.plugin.utils;

import lombok.AllArgsConstructor;

import java.util.Optional;

import static java.util.Optional.empty;

public class PlaceholderUtils {
    public static Optional<String> placeholderSubstring(String line, int offset) {
        return placeholderBorders(line, offset)
            .map(b -> line.substring(b.start, b.end + 1));
    }

    public static boolean insidePlaceholderBrackets(String line, int offset) {
        return placeholderBorders(line, offset).isPresent();
    }

    static Optional<Borders> placeholderBorders(String line, int offset) {
        String leftString = line.substring(0, offset);
        int start = leftString.lastIndexOf("${");
        int previousEnd = leftString.lastIndexOf('}');
        if (previousEnd > start) return empty();

        int end = line.indexOf('}', offset);
        return end > 0 ? Optional.of(new Borders(start, end)) : empty();
    }

    @AllArgsConstructor
    private static class Borders {
        private final int start;
        private final int end;
    }
}