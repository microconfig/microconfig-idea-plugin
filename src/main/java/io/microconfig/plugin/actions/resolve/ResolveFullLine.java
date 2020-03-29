package io.microconfig.plugin.actions.resolve;

import io.microconfig.core.properties.DeclaringComponentImpl;
import io.microconfig.plugin.actions.handler.ActionHandler;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.PluginContext;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.core.properties.ConfigFormat.PROPERTIES;
import static io.microconfig.core.properties.PropertyImpl.parse;
import static io.microconfig.plugin.actions.resolve.Hints.showHint;

@RequiredArgsConstructor
public class ResolveFullLine implements ActionHandler {
    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        String currentLine = context.currentLine();
        Map<String, String> valueByEnv = api.resolveFullLineForEachEnv(currentLine, context.currentFile(), context.projectDir());

        showHint(getKey(currentLine), valueByEnv, context);
    }

    private String getKey(String currentLine) {
        return parse(currentLine, PROPERTIES, new DeclaringComponentImpl("", "", "")).getKey();
    }
}