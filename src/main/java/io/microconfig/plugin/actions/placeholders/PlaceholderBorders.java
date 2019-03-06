package io.microconfig.plugin.actions.placeholders;

import io.microconfig.configs.resolver.placeholder.Placeholder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.regex.Matcher;

@ToString
@RequiredArgsConstructor
public class PlaceholderBorders {
    private final String value;

    private final int start;
    private final int end;

    private static PlaceholderBorders empty() {
        return new PlaceholderBorders(null, -1, -1);
    }

    public static PlaceholderBorders borders(String line, int offset) {
        int start = line.lastIndexOf("${", offset);
        if (start < 0) return empty();

        Matcher matcher = Placeholder.placeholderMatcher(line.substring(start));
        return !matcher.find() ? empty() : new PlaceholderBorders(matcher.group(), matcher.start(), matcher.end());
    }

    public boolean isInsidePlaceholder() {
        return start >= 0;
    }

    public String value() {
        return value;
    }
}