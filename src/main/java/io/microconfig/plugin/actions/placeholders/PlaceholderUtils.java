package io.microconfig.plugin.actions.placeholders;

import io.microconfig.plugin.actions.common.PluginContext;

import java.util.Map;

import static io.microconfig.utils.FileUtils.LINES_SEPARATOR;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

public class PlaceholderUtils {
    public static void printValues(String placeholder, Map<String, String> values, PluginContext context) {
        if (values.isEmpty()) {
            context.showErrorHing("Can't find placeholder values: " + placeholder);
            return;
        }

        String message = placeholder
                + LINES_SEPARATOR
                + LINES_SEPARATOR
                + values.entrySet()
                .stream()
                .sorted(comparing(Map.Entry::getKey))
                .map(e -> message(e.getKey(), e.getValue()))
                .collect(joining(LINES_SEPARATOR));

        context.showInfoHing(message);
    }

    private static String message(String env, String value) {
        return (env.isEmpty() ? "default" : env) + ": " + value;
    }
}
