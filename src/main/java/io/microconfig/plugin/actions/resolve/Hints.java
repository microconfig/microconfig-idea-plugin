package io.microconfig.plugin.actions.resolve;

import io.microconfig.plugin.actions.common.PluginContext;

import java.util.Map;

import static io.microconfig.utils.FileUtils.LINES_SEPARATOR;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

class Hints {
    private static final String DEFAULT_ENV_KEY = "";
    private static final String DEFAULT_ENV_ALIAS = "default";

    public static void showHint(String line, Map<String, String> values, PluginContext context) {
        if (values.isEmpty()) {
            context.showErrorHint("Can't resolve line: " + line);
            return;
        }

        String message = line
                + LINES_SEPARATOR
                + LINES_SEPARATOR
                + defaultEnvValue(values)
                + values.entrySet()
                .stream()
                .sorted(comparing(Map.Entry::getKey))
                .map(e -> envValue(e.getKey(), e.getValue()))
                .collect(joining(LINES_SEPARATOR));

        context.showInfoHint(message);
    }

    private static String defaultEnvValue(Map<String, String> values) {
        return values.containsKey(DEFAULT_ENV_KEY) ? envValue(DEFAULT_ENV_ALIAS, values.remove(DEFAULT_ENV_KEY) + LINES_SEPARATOR) : "";
    }

    private static String envValue(String env, String value) {
        return env + ": " + value;
    }
}