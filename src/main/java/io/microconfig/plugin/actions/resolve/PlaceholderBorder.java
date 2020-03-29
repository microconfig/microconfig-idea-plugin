package io.microconfig.plugin.actions.resolve;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class PlaceholderBorder {
    private final String value;

    private final int start;
    private final int end;

    private static PlaceholderBorder empty() {
        return new PlaceholderBorder(null, -1, -1);
    }

    public static PlaceholderBorder borders(String line, int offset) {
        int start = line.lastIndexOf("${", offset);
        if (start < 0) return empty();

        int middle = line.indexOf('@', start);
        if (middle < 0) return empty();

        int finish = findFinishIndex(line, middle);
        return (finish < 0 || finish < offset) ? empty() : new PlaceholderBorder(line.substring(start, finish), start, finish);
    }

    //todo
    private static int findFinishIndex(String line, int middle) {
        int openBrackets = 1;
        for (int i = middle + 1; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '$' && line.length() > i + 1 && line.charAt(i + 1) == '{') {
                openBrackets++;
            } else if (c == '}') {
                --openBrackets;
                if (openBrackets == 0) {
                    return i + 1;
                }
            }
        }

        return -1;
    }

    public boolean isInsidePlaceholder() {
        return start >= 0 && start < end;
    }

    public String value() {
        return value;
    }
}