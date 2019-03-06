package io.microconfig.plugin.actions.placeholders;

import io.microconfig.plugin.PluginContext;

import java.util.Map;

import static io.microconfig.utils.FileUtils.LINES_SEPARATOR;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.joining;

public class PlaceholderUtils {
    public static void printValues(Map<String, String> values, PluginContext context) {
        if (values.isEmpty()) {
            context.showErrorHing("Can't find placeholder values");
            return;
        }

        String message = values.entrySet().stream()
                .sorted(comparingInt(e -> rate(e.getKey())))
                .map(e -> message(e.getKey(), e.getValue()))
                .collect(joining(LINES_SEPARATOR));

        context.showInfoHing(message);
    }

    private static int rate(String env) {
        return env.length();
    }

    private static String message(String env, String value) {
        return (env.isEmpty() ? "default" : env) + ": " + value;
    }
}
