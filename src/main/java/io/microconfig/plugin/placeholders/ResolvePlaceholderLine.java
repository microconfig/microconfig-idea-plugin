package io.microconfig.plugin.placeholders;

import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

import static io.microconfig.plugin.utils.ContextUtils.showErrorHing;
import static io.microconfig.plugin.utils.ContextUtils.showInfoHing;
import static java.util.Comparator.comparingInt;

@RequiredArgsConstructor
public class ResolvePlaceholderLine implements MicroconfigComponent {
    private static final String regexp = "^.*?\\$\\{.*?\\}.*?$";

    private final MicroconfigApi api;
    private final PluginContext context;
    private final String currentLine;

    public static boolean hasPlaceholder(String currentLine) {
        return currentLine.matches(regexp);
    }

    @Override
    public void react() {
        Map<String, String> values = api.placeholderValues(context.projectDir(), currentLine);
        if (values.isEmpty()) {
            showErrorHing(context.editor, "Can't find placeholder values");
            return;
        }

        String message = values.entrySet().stream()
            .sorted(comparingInt(e -> rate(e.getKey())))
            .map(e -> message(e.getKey(), e.getValue()))
            .collect(Collectors.joining("\n"));

        showInfoHing(context.editor, message);
    }

    private static int rate(String env) {
        return env.length();
    }

    private static String message(String env, String value) {
        return env.isEmpty()
            ? "default: " + value
            : env + ": " + value;
    }
}
