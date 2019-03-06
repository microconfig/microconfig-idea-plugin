package io.microconfig.plugin.actions.placeholders;

import io.microconfig.configs.resolver.placeholder.Placeholder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.regex.Matcher;

import static java.lang.Math.max;

@ToString
@RequiredArgsConstructor
public class PlaceholderBorders {
    private final String value;

    private final int start;
    private final int end;

    public static PlaceholderBorders borders(String line, int offset) {
        String substring = line.substring(max(0, line.lastIndexOf("${", offset)));
        Matcher matcher = Placeholder.placeholderMatcher(substring);
        return !matcher.find() ? new PlaceholderBorders(null, -1, -1) : new PlaceholderBorders(matcher.group(), matcher.start(), matcher.end());
    }

    public boolean isInsidePlaceholder() {
        return start >= 0;
    }

    public String value() {
        return value;
    }
}