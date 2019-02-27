package io.microconfig.plugin.utils;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class PlaceholderUtils {
    private static final String PATTERN = "^.*?\\$\\{.*?}.*?$";

    public static boolean hasPlaceholder(String currentLine) {
        return currentLine.matches(PATTERN);
    }

    public static Optional<String> placeholderSubstring(String line, int offset) {
        return placeholderBorders(line, offset)
                .map(b -> line.substring(b.start, b.end + 1));
    }

    public static boolean insidePlaceholderBrackets(String line, int offset) {
        return placeholderBorders(line, offset).isPresent();
    }

    private static Optional<Borders> placeholderBorders(String line, int offset) {
        String leftString = line.substring(0, offset);
        int start = leftString.lastIndexOf("${");
        if (start < 0) return empty();

        int previousEnd = leftString.lastIndexOf('}');
        if (previousEnd > start) return empty();

        int end = line.indexOf('}', offset);
        return end > 0 ? of(new Borders(start, end)) : empty();
    }

    @AllArgsConstructor
    @ToString
    private static class Borders {
        private final int start;
        private final int end;
    }
}