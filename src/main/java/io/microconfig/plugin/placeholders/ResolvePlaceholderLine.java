package io.microconfig.plugin.placeholders;

import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;

import java.util.Map;

public class ResolvePlaceholderLine extends ResolvePlaceholderBase implements MicroconfigComponent {
    public ResolvePlaceholderLine(MicroconfigApi api, PluginContext context, String currentLine) {
        super(api, context, currentLine);
    }

    @Override
    public void react() {
        Map<String, String> values = api.resolvePropertyValueForEachEnv(currentLine, context.projectDir());
        printValues(values);
    }
}