package io.microconfig.plugin.placeholders;

import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;

import java.util.Map;

import static io.microconfig.plugin.utils.PlaceholderUtils.placeholderSubstring;

public class ResolvePlaceholderValue extends ResolvePlaceholderBase implements MicroconfigComponent {

    public ResolvePlaceholderValue(MicroconfigApi api, PluginContext context, String currentLine) {
        super(api, context, currentLine);
    }

    @Override
    public void react() {
        String placeholder = placeholderSubstring(currentLine, context.caret.getLogicalPosition().column)
            .orElseThrow(() -> new IllegalArgumentException("Not inside placeholder brackets"));

        //todo error hint if special placeholder
        Map<String, String> values = api.placeholderValues(context.projectDir(), placeholder);
        printValues(values);
    }
}
