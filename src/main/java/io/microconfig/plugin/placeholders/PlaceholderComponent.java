package io.microconfig.plugin.placeholders;

import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceholderComponent implements MicroconfigComponent {
    private static final String regexp = "^.*?\\$\\{.*?\\}.*?$";

    private final PluginContext context;
    private final String currentLine;

    public static boolean hasPlaceholder(String currentLine) {
        return currentLine.matches(regexp);
    }

    @Override
    public void react() {
    }
}
