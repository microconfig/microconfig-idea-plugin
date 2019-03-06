package io.microconfig.plugin.actions.placeholders;

import io.microconfig.plugin.actions.common.PluginContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.microconfig.utils.FileUtils.LINES_SEPARATOR;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

public class PlaceholderUtils {
    private static final String DEFAULT_ENV_KEY = "";

    public static void printValues(String placeholder, Map<String, String> values, PluginContext context) {
        if (values.isEmpty()) {
            context.showErrorHing("Can't find placeholder values: " + placeholder);
            return;
        }

        String message = placeholder
                + LINES_SEPARATOR
                + LINES_SEPARATOR
                + defaultEnvValue(values)
                + values.entrySet()
                .stream()
                .sorted(comparing(Map.Entry::getKey))
                .map(e -> message(e.getKey(), e.getValue()))
                .collect(joining(LINES_SEPARATOR));

        context.showInfoHing(message);
    }

    @NotNull
    private static String defaultEnvValue(Map<String, String> values) {
        return values.containsKey(DEFAULT_ENV_KEY) ? message(DEFAULT_ENV_KEY, values.remove(DEFAULT_ENV_KEY) + LINES_SEPARATOR) : "";
    }

    private static String message(String env, String value) {
        return (env.isEmpty() ? "default" : env) + ": " + value;
    }
}
