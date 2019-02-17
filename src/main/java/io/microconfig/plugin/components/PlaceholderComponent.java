package io.microconfig.plugin.components;

import io.microconfig.plugin.PluginContext;

public class PlaceholderComponent implements MicroconfigComponent {

    private static final String regexp = "^.*?\\$\\{.*?\\}.*?$";

    private final PluginContext context;
    private final String currentLine;

    public PlaceholderComponent(PluginContext context, String currentLine) {
        this.context = context;
        this.currentLine = currentLine;
    }

    public static boolean hasPlaceholder(String currentLine) {
        return currentLine.matches(regexp);
    }

    @Override
    public void react() {

    }

}
